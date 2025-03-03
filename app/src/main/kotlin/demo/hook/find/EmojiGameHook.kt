package demo.hook.find

import android.view.LayoutInflater
import android.view.View
import com.highcapable.yukihookapi.hook.type.java.IntType
import me.hd.wauxv.data.config.DefaultData
import me.hd.wauxv.data.config.DescriptorData
import me.hd.wauxv.databinding.ModuleDialogEmojiGameBinding
import me.hd.wauxv.factory.showDialog
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.api.IDexFind
import me.hd.wauxv.hook.base.SwitchHook
import me.hd.wauxv.hook.factory.toAppMethod
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.enums.MatchType

@Obfuscate
@HookAnno
@ViewAnno
object EmojiGameHook : SwitchHook("EmojiGameHook"), IDexFind {
    enum class MorraType(val index: Int) { SCISSORS(0), STONE(1), PAPER(2) }
    enum class DiceFace(val index: Int) { ONE(0), TWO(1), THREE(2), FOUR(3), FIVE(4), SIX(5) }

    private object MethodRandom : DescriptorData("EmojiGameHook.MethodRandom")
    private object ValMorra : DefaultData("EmojiGameHook.ValMorra", intDefVal = 0)
    private object ValDice : DefaultData("EmojiGameHook.ValDice", intDefVal = 0)

    override val location = "辅助"
    override val funcName = "表情游戏"
    override val funcDesc = "预先自定义设置猜拳和骰子的随机结果"
    override var onClick: ((View) -> Unit)? = { layoutView ->
        val binding = ModuleDialogEmojiGameBinding.inflate(LayoutInflater.from(layoutView.context))
        when (ValMorra.intVal) {
            MorraType.SCISSORS.index -> binding.moduleDialogRbMorra0.isChecked = true
            MorraType.STONE.index -> binding.moduleDialogRbMorra1.isChecked = true
            MorraType.PAPER.index -> binding.moduleDialogRbMorra2.isChecked = true
        }
        when (ValDice.intVal) {
            DiceFace.ONE.index -> binding.moduleDialogRbDice1.isChecked = true
            DiceFace.TWO.index -> binding.moduleDialogRbDice2.isChecked = true
            DiceFace.THREE.index -> binding.moduleDialogRbDice3.isChecked = true
            DiceFace.FOUR.index -> binding.moduleDialogRbDice4.isChecked = true
            DiceFace.FIVE.index -> binding.moduleDialogRbDice5.isChecked = true
            DiceFace.SIX.index -> binding.moduleDialogRbDice6.isChecked = true
        }
        layoutView.context.showDialog {
            title = funcName
            view = binding.root
            positiveButton("保存") {
                ValMorra.intVal = when (binding.moduleDialogRgMorra.checkedRadioButtonId) {
                    binding.moduleDialogRbMorra0.id -> MorraType.SCISSORS.index
                    binding.moduleDialogRbMorra1.id -> MorraType.STONE.index
                    binding.moduleDialogRbMorra2.id -> MorraType.PAPER.index
                    else -> MorraType.SCISSORS.index
                }
                ValDice.intVal = when (binding.moduleDialogRgDice.checkedRadioButtonId) {
                    binding.moduleDialogRbDice1.id -> DiceFace.ONE.index
                    binding.moduleDialogRbDice2.id -> DiceFace.TWO.index
                    binding.moduleDialogRbDice3.id -> DiceFace.THREE.index
                    binding.moduleDialogRbDice4.id -> DiceFace.FOUR.index
                    binding.moduleDialogRbDice5.id -> DiceFace.FIVE.index
                    binding.moduleDialogRbDice6.id -> DiceFace.SIX.index
                    else -> DiceFace.ONE.index
                }
            }
            negativeButton()
        }
    }

    override fun initOnce() {
        MethodRandom.desc.toAppMethod().hook {
            afterIfEnabled {
                val type = args(0).int()
                val originResult = result<Int>()
                result = when (type) {
                    2 -> ValMorra.intVal
                    5 -> ValDice.intVal
                    else -> originResult
                }
            }
        }
    }

    override fun dexFind(dexKit: DexKitBridge) {
        MethodRandom.desc = dexKit.findMethod {
            matcher {
                returnType = IntType.name
                paramTypes(IntType, IntType)
                invokeMethods {
                    add { name = "currentTimeMillis" }
                    add { name = "nextInt" }
                    matchType = MatchType.Contains
                }
            }
        }.single().descriptor
    }
}
