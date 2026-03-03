package com.example.kuiklylibgallery.module

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import com.tencent.kuiklyx.knative.bridge.plugin.KuiklyPlugin
import org.json.JSONObject

/**
 * Android 原生侧 Demo 插件
 * 插件名: "demo"
 * 对应 Kuikly 侧的 DemoPlugin
 */
class DemoPluginNative : KuiklyPlugin() {

    override fun pluginName(): String = "demo"

    init {
        // 注册 showToast 方法
        registerMethod("showToast", pluginMethod { context, params, callback ->
            val activity = context.getCurrentActivity()
            val message = params ?: "Hello"
            activity?.runOnUiThread {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
            callback?.invoke(
                JSONObject().apply {
                    put("code", 0)
                    put("msg", "success")
                }
            )
            null
        })

        // 注册 getDeviceInfo 方法
        registerMethod("getDeviceInfo", pluginMethod { context, params, callback ->
            val deviceInfo = JSONObject().apply {
                put("brand", Build.BRAND)
                put("model", Build.MODEL)
                put("sdkVersion", Build.VERSION.SDK_INT)
                put("androidVersion", Build.VERSION.RELEASE)
                put("platform", "Android")
            }
            callback?.invoke(
                JSONObject().apply {
                    put("code", 0)
                    put("msg", "success")
                    put("data", deviceInfo)
                }
            )
            null
        })

        // 注册 getTimestamp 方法（同步）
        registerMethod("getTimestamp", pluginMethod { context, params, callback ->
            System.currentTimeMillis().toString()
        })

        // 注册 openUrl 方法
        registerMethod("openUrl", pluginMethod { context, params, callback ->
            val activity = context.getCurrentActivity()
            val url = params ?: ""
            if (activity != null && url.isNotEmpty()) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    activity.startActivity(intent)
                    callback?.invoke(
                        JSONObject().apply {
                            put("code", 0)
                            put("msg", "success")
                        }
                    )
                } catch (e: Exception) {
                    callback?.invoke(
                        JSONObject().apply {
                            put("code", -1)
                            put("msg", "打开URL失败: ${e.message}")
                        }
                    )
                }
            } else {
                callback?.invoke(
                    JSONObject().apply {
                        put("code", -1)
                        put("msg", "URL为空或Activity不可用")
                    }
                )
            }
            null
        })
    }
}