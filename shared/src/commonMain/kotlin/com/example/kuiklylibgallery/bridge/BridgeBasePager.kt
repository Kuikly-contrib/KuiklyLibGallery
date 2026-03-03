package com.example.kuiklylibgallery.bridge

import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.reactive.handler.*
import com.tencent.kuiklyx.bridge.pager.TMBasePager
import com.tencent.kuiklyx.bridge.plugin.PluginModule

/**
 * Bridge 页面基类
 * 继承自 TMBasePager，支持 kuiklyx-bridge 插件路由
 */
internal abstract class BridgeBasePager : TMBasePager() {

    private var nightModel: Boolean? by observable(null)

    /**
     * 注册 Bridge 插件
     * 插件采用懒加载方式，只有首次 getPlugin 时才会创建实例
     */
    override fun createPlugins(): Map<String, () -> PluginModule> {
        return mapOf(
            DemoPlugin.PLUGIN_NAME to { DemoPlugin() },
        )
    }

    /**
     * 注册额外的 Module
     * ⚠️ 必须包含 super.createExternalModules()，否则 BridgeModule 不会被注册
     */
    override fun createExternalModules(): Map<String, Module>? {
        return super.createExternalModules().orEmpty()
    }

    override fun created() {
        super.created()
        isNightMode()
    }

    override fun themeDidChanged(data: JSONObject) {
        super.themeDidChanged(data)
        nightModel = data.optBoolean(IS_NIGHT_MODE_KEY)
    }

    override fun isNightMode(): Boolean {
        if (nightModel == null) {
            nightModel = pageData.params.optBoolean(IS_NIGHT_MODE_KEY)
        }
        return nightModel!!
    }

    override fun debugUIInspector(): Boolean {
        return false
    }

    companion object {
        const val IS_NIGHT_MODE_KEY = "isNightMode"
    }
}