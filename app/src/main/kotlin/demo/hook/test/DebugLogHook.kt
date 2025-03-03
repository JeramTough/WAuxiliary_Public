package demo.hook.test

import android.util.Log
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import me.hd.wauxv.data.factory.Wauxv
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.base.SwitchHook
import me.hd.wauxv.hook.factory.toAppClass
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
@HookAnno
@ViewAnno
object DebugLogHook : SwitchHook("DebugLogHook") {
    override val location = "测试"
    override val funcName = "调试日志"
    override val funcDesc = "将微信应用所有日志调试输出到控制台"

    override fun initOnce() {
        val logClass = "com.tencent.mars.xlog.Xlog".toAppClass()
        logClass.method {
            name = "logMonitor"
            param(LongType, IntType, StringClass, StringClass, StringClass, IntType, IntType, LongType, LongType, StringClass)
        }.hook {
            beforeIfEnabled {
                val level = args(1).int()
                val tag = args(2).string()
                val param = args(9).string()
                when (level) {
                    0 -> Log.v(Wauxv.HOOK_TAG, "$tag: $param")
                    1 -> Log.d(Wauxv.HOOK_TAG, "$tag: $param")
                    2 -> Log.i(Wauxv.HOOK_TAG, "$tag: $param")
                    3 -> Log.w(Wauxv.HOOK_TAG, "$tag: $param")
                    4 -> Log.e(Wauxv.HOOK_TAG, "$tag: $param")
                    5 -> Log.wtf(Wauxv.HOOK_TAG, "$tag: $param")
                }
            }
        }
    }
}
