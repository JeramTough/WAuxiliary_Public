package demo.hook.find

import android.view.View
import android.widget.Toast
import com.highcapable.yukihookapi.hook.type.android.ImageViewClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.FloatType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import me.hd.wauxv.data.config.DescriptorData
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.api.IDexFind
import me.hd.wauxv.hook.base.SwitchHook
import me.hd.wauxv.hook.factory.toAppMethod
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
@HookAnno
@ViewAnno
object RoundAvatarHook : SwitchHook("RoundAvatarHook"), IDexFind {
    private object MethodDrawable : DescriptorData("RoundAvatarHook.MethodDrawable")

    override val location = "美化"
    override val funcName = "圆形头像"
    override val funcDesc = "可自定义微信全局头像渲染的圆形弧度"
    override var onClick: ((View) -> Unit)? = { layoutView ->
        Toast.makeText(layoutView.context, "暂不支持自定义弧度哦~", Toast.LENGTH_SHORT).show()
    }

    override fun initOnce() {
        MethodDrawable.desc.toAppMethod().hook {
            beforeIfEnabled {
                args(2).set(0.38f)
            }
        }
    }

    override fun dexFind(dexKit: DexKitBridge) {
        MethodDrawable.desc = dexKit.findMethod {
            matcher {
                paramTypes(ImageViewClass, StringClass, FloatType, BooleanType)
                usingEqStrings("MicroMsg.AvatarDrawable")
            }
        }.single().descriptor
    }
}
