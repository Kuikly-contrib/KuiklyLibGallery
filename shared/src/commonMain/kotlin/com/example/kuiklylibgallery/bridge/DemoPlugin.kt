package com.example.kuiklylibgallery.bridge

import com.tencent.kuikly.core.module.CallbackFn
import com.tencent.kuiklyx.bridge.plugin.PluginModule

/**
 * 演示用 Bridge 插件
 * 插件名: "demo"
 * 原生方法路由格式: demo.xxx
 */
internal class DemoPlugin : PluginModule() {

    override fun pluginName(): String {
        return PLUGIN_NAME
    }

    /**
     * 显示 Toast 提示
     * 原生路由: demo.showToast
     */
    fun showToast(message: String, callback: CallbackFn? = null) {
        callNative(SHOW_TOAST, message, callback)
    }

    /**
     * 获取设备信息
     * 原生路由: demo.getDeviceInfo
     */
    fun getDeviceInfo(callback: CallbackFn? = null) {
        callNative(GET_DEVICE_INFO, null, callback)
    }

    /**
     * 同步获取当前时间戳
     * 原生路由: demo.getTimestamp
     */
    fun getTimestamp(): String {
        return syncCallNative<String>(GET_TIMESTAMP, null, null) ?: ""
    }

    /**
     * 打开URL
     * 原生路由: demo.openUrl
     */
    fun openUrl(url: String, callback: CallbackFn? = null) {
        callNative(OPEN_URL, url, callback)
    }

    companion object {
        const val PLUGIN_NAME = "demo"

        private const val SHOW_TOAST = "showToast"
        private const val GET_DEVICE_INFO = "getDeviceInfo"
        private const val GET_TIMESTAMP = "getTimestamp"
        private const val OPEN_URL = "openUrl"
    }
}