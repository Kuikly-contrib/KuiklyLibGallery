package com.example.kuiklylibgallery.module

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.tencent.kuikly.core.render.android.export.KuiklyRenderBaseModule
import com.tencent.kuikly.core.render.android.export.KuiklyRenderCallback
import com.example.kuiklylibgallery.KRApplication
import com.example.kuiklylibgallery.KuiklyRenderActivity
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date

class KRBridgeModule : KuiklyRenderBaseModule() {

    override fun call(method: String, params: String?, callback: KuiklyRenderCallback?): Any? {
        return when (method) {
            "ssoRequest" -> {
                ssoRequest(params, callback)
            }

            "showAlert" -> {
                showAlert(params, callback)
            }

            "closePage" -> {
                closePage(params)
            }

            "openPage" -> {
                openPage(params)
            }

            "copyToPasteboard" -> {
                copyToPasteboard(params)
            }

            "toast" -> {
                toast(params)
            }

            "log" -> {
                log(params)
            }

            "reportDT" -> {
                reportDT(params)
            }

            "reportRealtime" -> {
                reportRealtime(params)
            }

            "qqLiveSSORequest" -> {
                qqLiveSSORequest(params, callback)
            }

            "localServeTime" -> {
                localServeTime(params, callback)
            }

            "currentTimestamp" -> {
                currentTimestamp(params)
            }

            "dateFormatter" -> {
                dateFormatter(params)
            }

            else -> handleBridgePlugin(method, params, callback)
        }
    }

    // ===================== kuiklyx-bridge 插件路由 =====================

    /**
     * 处理 kuiklyx-bridge 插件路由
     * 方法格式: "pluginName.methodName"，如 "demo.showToast"
     */
    private fun handleBridgePlugin(method: String, params: String?, callback: KuiklyRenderCallback?): Any? {
        val dotIndex = method.indexOf('.')
        if (dotIndex < 0) {
            callback?.invoke(mapOf("code" to -1, "msg" to "方法不存在: $method"))
            return null
        }
        val pluginName = method.substring(0, dotIndex)
        val methodName = method.substring(dotIndex + 1)

        return when (pluginName) {
            "demo" -> handleDemoPlugin(methodName, params, callback)
            else -> {
                callback?.invoke(mapOf("code" to -1, "msg" to "未知插件: $pluginName"))
                null
            }
        }
    }

    /**
     * Demo 插件方法分发
     */
    private fun handleDemoPlugin(method: String, params: String?, callback: KuiklyRenderCallback?): Any? {
        return when (method) {
            "showToast" -> {
                val message = params ?: "Hello"
                activity?.runOnUiThread {
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                }
                callback?.invoke(mapOf("code" to 0, "msg" to "success"))
                null
            }
            "getDeviceInfo" -> {
                val deviceInfo = JSONObject().apply {
                    put("brand", Build.BRAND)
                    put("model", Build.MODEL)
                    put("sdkVersion", Build.VERSION.SDK_INT)
                    put("androidVersion", Build.VERSION.RELEASE)
                    put("platform", "Android")
                }
                callback?.invoke(mapOf(
                    "code" to 0,
                    "msg" to "success",
                    "data" to deviceInfo
                ))
                null
            }
            "getTimestamp" -> {
                System.currentTimeMillis().toString()
            }
            "openUrl" -> {
                val url = params ?: ""
                if (url.isNotEmpty() && activity != null) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        activity?.startActivity(intent)
                        callback?.invoke(mapOf("code" to 0, "msg" to "success"))
                    } catch (e: Exception) {
                        callback?.invoke(mapOf("code" to -1, "msg" to "打开URL失败: ${e.message}"))
                    }
                } else {
                    callback?.invoke(mapOf("code" to -1, "msg" to "URL为空或Activity不可用"))
                }
                null
            }
            else -> {
                callback?.invoke(mapOf("code" to -1, "msg" to "未知方法: demo.$method"))
                null
            }
        }
    }

    private fun reportRealtime(params: String?) {
    }

    private fun reportDT(params: String?) {
    }

    private fun log(params: String?) {
        if (params == null) {
            return
        }

        val paramJSON = JSONObject(params)
        Log.i("KuiklyRender", paramJSON.optString("content"))
    }

    private fun toast(params: String?) {
        if (params == null) {
            return
        }
        val paramJSON = JSONObject(params)
        Toast.makeText(
            KRApplication.application,
            paramJSON.optString("content"),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun copyToPasteboard(params: String?) {
        if (params == null) {
            return
        }

        val paramJSON = JSONObject(params)
        (context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.also {
            it.setPrimaryClip(ClipData.newPlainText(MODULE_NAME, paramJSON.optString("content")))
        }
    }

    private fun openPage(params: String?) {
        if (params == null) {
            return
        }
        val ctx = context ?: return
        val paramJSON = JSONObject(params)
        val url = paramJSON.optString("url")
    }

    private fun closePage(params: String?) {
        activity?.finish()
    }

    private fun showAlert(params: String?, callback: KuiklyRenderCallback?) {
        if (params == null) {
            return
        }
        val paramJSON = JSONObject(params)
        val titleText = paramJSON.optString("title")
        val message = paramJSON.optString("message")
        val buttons = paramJSON.optJSONArray("buttons") ?: JSONArray()
    }

    private fun ssoRequest(params: String?, callback: KuiklyRenderCallback?) {}

    private fun qqLiveSSORequest(params: String?, callback: KuiklyRenderCallback?) {
    }

    private fun localServeTime(params: String?, callback: KuiklyRenderCallback?) {
        val time = (System.currentTimeMillis() / 1000.0)
        callback?.invoke(
            mapOf(
                "time" to time
            )
        )
    }

    private fun currentTimestamp(params: String?): String {
        return (System.currentTimeMillis()).toString()
    }

    private fun dateFormatter(params: String?): String {
        val paramJSONObject = JSONObject(params ?: "{}")
        val data = Date(paramJSONObject.optLong("timeStamp"))
        val format = SimpleDateFormat(paramJSONObject.optString("format"))
        return format.format(data)
    }

    companion object {
        const val MODULE_NAME = "HRBridgeModule"
    }
}

private fun JSONObject.toMap(): Map<Any, Any> {
    val map = mutableMapOf<Any, Any>()
    val keys = keys()
    while (keys.hasNext()) {
        val key = keys.next()
        when (val v = opt(key)) {
            is JSONObject -> {
                map[key] = v.toMap()
            }

            else -> {
                v?.also {
                    map[key] = it
                }
            }
        }
    }
    return map
}