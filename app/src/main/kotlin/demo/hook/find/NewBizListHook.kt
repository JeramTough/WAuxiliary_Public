package demo.hook.find

import android.content.ComponentName
import android.content.Intent
import me.hd.wauxv.data.config.DescriptorData
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.api.IDexFind
import me.hd.wauxv.hook.base.SwitchHook
import me.hd.wauxv.hook.factory.toAppClass
import me.hd.wauxv.hook.factory.toAppMethod
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
@HookAnno
@ViewAnno
object NewBizListHook : SwitchHook("NewBizListHook"), IDexFind {
    private object MethodShowAsActivity : DescriptorData("NewBizListHook.MethodShowAsActivity")
    private object MethodMarkStartOpen : DescriptorData("NewBizListHook.MethodMarkStartOpen")

    override val location = "增强"
    override val funcName = "订阅列表"
    override val funcDesc = "订阅号消息从瀑布流模式改为列表模式"

    override fun initOnce() {
        val newBizConversationUiClass = "com.tencent.mm.ui.conversation.NewBizConversationUI".toAppClass()
        val flutterViewClass = "com.tencent.mm.plugin.brandservice.ui.flutter.BizFlutterTLFlutterViewActivity"
        val timeLineUiClass = "com.tencent.mm.plugin.brandservice.ui.timeline.BizTimeLineUI"
        MethodShowAsActivity.desc.toAppMethod().hook {
            beforeIfEnabled {
                val clz = args(2).cast<Class<*>>()
                if (clz?.name == flutterViewClass) {
                    args(2).set(newBizConversationUiClass)
                }
            }
        }
        MethodMarkStartOpen.desc.toAppMethod().hook {
            beforeIfEnabled {
                val intent = args(0).cast<Intent>()
                if (intent?.component?.className == timeLineUiClass) {
                    intent.component = ComponentName("com.tencent.mm", newBizConversationUiClass.name)
                }
            }
        }
    }

    override fun dexFind(dexKit: DexKitBridge) {
        MethodShowAsActivity.desc = dexKit.findMethod {
            matcher {
                usingEqStrings("com/tencent/mm/flutter/base/MMFlutterInstance", "showAsActivity")
            }
        }.single().descriptor
        MethodMarkStartOpen.desc = dexKit.findMethod {
            matcher {
                usingEqStrings("MicroMsg.BizTimeReport", "markStartOpen")
            }
        }.single().descriptor
    }
}
