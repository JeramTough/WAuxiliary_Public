package demo.hook.find

import android.view.LayoutInflater
import android.view.View
import com.highcapable.yukihookapi.hook.factory.method
import me.hd.wauxv.data.config.DefaultData
import me.hd.wauxv.data.config.DescriptorData
import me.hd.wauxv.data.factory.WechatProcess
import me.hd.wauxv.databinding.ModuleDialogLocationBinding
import me.hd.wauxv.factory.showDialog
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
object LocationHook : SwitchHook("LocationHook"), IDexFind {
    private object MethodListener : DescriptorData("LocationHook.MethodListener")
    private object MethodListenerWgs84 : DescriptorData("LocationHook.MethodListenerWgs84")
    private object MethodDefaultManager : DescriptorData("LocationHook.MethodDefaultManager")
    private object ValLatitude : DefaultData("LocationHook.latitude", floatDefVal = LATITUDE_DEF_VAL)
    private object ValLongitude : DefaultData("LocationHook.longitude", floatDefVal = LONGITUDE_DEF_VAL)

    private const val LATITUDE_DEF_VAL = 16.61953f
    private const val LONGITUDE_DEF_VAL = 98.56146f

    override val location = "辅助"
    override val funcName = "虚拟定位"
    override val funcDesc = "将腾讯定位SDK结果虚拟为指定经纬度"
    override var onClick: ((View) -> Unit)? = { layoutView ->
        val binding = ModuleDialogLocationBinding.inflate(LayoutInflater.from(layoutView.context))
        binding.moduleDialogEdtLatitude.setText("${ValLatitude.floatVal}")
        binding.moduleDialogEdtLongitude.setText("${ValLongitude.floatVal}")
        layoutView.context.showDialog {
            title = funcName
            view = binding.root
            positiveButton {
                ValLatitude.floatVal = binding.moduleDialogEdtLatitude.text.toString().toFloat()
                ValLongitude.floatVal = binding.moduleDialogEdtLongitude.text.toString().toFloat()
            }
            negativeButton()
            neutralButton("重置") {
                ValLatitude.floatVal = LATITUDE_DEF_VAL
                ValLongitude.floatVal = LONGITUDE_DEF_VAL
            }
        }
    }
    override val targetProcess = arrayOf(
        WechatProcess.MAIN_PROCESS.processName,
        WechatProcess.APP_BRAND_0.processName
    )

    override fun initOnce() {
        val methodList = listOf(
            MethodListener.desc.toAppMethod(),
            MethodListenerWgs84.desc.toAppMethod(),
            MethodDefaultManager.desc.toAppMethod()
        )
        methodList.forEach { method ->
            method.hook {
                beforeIfEnabled {
                    val location = args(0).any()!!
                    location::class.java.apply {
                        method { name = "getLatitude" }.hook {
                            beforeIfEnabled {
                                result = ValLatitude.floatVal.toDouble()
                            }
                        }
                        method { name = "getLongitude" }.hook {
                            beforeIfEnabled {
                                result = ValLongitude.floatVal.toDouble()
                            }
                        }
                    }
                    removeSelf()
                }
            }
        }
    }

    override fun dexFind(dexKit: DexKitBridge) {
        MethodListener.desc = dexKit.findMethod {
            matcher {
                name = "onLocationChanged"
                usingEqStrings("MicroMsg.SLocationListener")
            }
        }.single().descriptor
        MethodListenerWgs84.desc = dexKit.findMethod {
            matcher {
                name = "onLocationChanged"
                usingEqStrings("MicroMsg.SLocationListenerWgs84")
            }
        }.single().descriptor
        MethodDefaultManager.desc = dexKit.findMethod {
            matcher {
                name = "onLocationChanged"
                usingEqStrings("MicroMsg.DefaultTencentLocationManager", "[mlocationListener]error:%d, reason:%s")
            }
        }.single().descriptor
    }
}
