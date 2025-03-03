package demo.hook.find

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
object PadModeHook : SwitchHook("PadModeHook"), IDexFind {
    private object MethodIsPad : DescriptorData("PadModeHook.MethodIsPad")

    override val location = "实验"
    override val funcName = "平板模式"
    override val funcDesc = "可在当前设备登录另一台设备的微信号"

    override fun initOnce() {
        MethodIsPad.desc.toAppMethod().hook {
            beforeIfEnabled {
                resultTrue()
            }
        }
    }

    override fun dexFind(dexKit: DexKitBridge) {
        MethodIsPad.desc = dexKit.findMethod {
            matcher {
                usingEqStrings("Lenovo TB-9707F")
            }
        }.single().descriptor
    }
}
