package com.example.kuiklylibgallery.bridge

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.layout.FlexAlign
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.layout.FlexJustifyContent
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.reactive.handler.*
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuiklyx.bridge.Bridge
import com.tencent.kuiklyx.bridge.ext.JsonResult
import com.example.kuiklylibgallery.RouterNavBar

/**
 * Bridge Demo 页面
 * 演示 kuiklyx-bridge 插件路由的使用
 */
@Page("BridgeDemoPage", supportInLocal = true)
internal class BridgeDemoPage : BridgeBasePager() {

    // 日志输出内容
    private var logOutput: String by observable("等待操作...")

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr {
                backgroundColor(Color(0xFFF5F7FA))
            }

            List {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                }

                // 导航栏
                RouterNavBar {
                    attr {
                        title = "Bridge Demo"
                    }
                }

                View {
                    attr {
                        flexDirection(FlexDirection.COLUMN)
                        padding(16f)
                    }

                    // 说明区域
                    View {
                        attr {
                            marginBottom(20f)
                        }
                        Text {
                            attr {
                                text("kuiklyx-bridge 插件路由演示")
                                fontSize(22f)
                                fontWeightBold()
                                color(Color(0xFF1A1A1A))
                            }
                        }
                    }

                    Text {
                        attr {
                            text("以下按钮演示通过 Bridge.getPlugin 调用插件方法，\n插件方法会路由到原生侧执行。")
                            fontSize(14f)
                            color(Color(0xFF666666))
                            marginBottom(20f)
                        }
                    }

                    // 按钮1: 调用 showToast
                    ctx.createActionButton(
                        this,
                        title = "showToast",
                        description = "调用 demo.showToast",
                        gradientColors = listOf(0xFF667EEA, 0xFF764BA2)
                    ) {
                        Bridge.getPlugin<DemoPlugin>(DemoPlugin.PLUGIN_NAME)
                            ?.showToast("Hello from Bridge!") { result ->
                                val jsonResult = JsonResult(result)
                                ctx.logOutput = if (jsonResult.isSuccess()) {
                                    "✅ showToast 成功: ${jsonResult.msg}"
                                } else {
                                    "❌ showToast 失败: code=${jsonResult.code}, msg=${jsonResult.msg}"
                                }
                            } ?: run {
                            ctx.logOutput = "⚠️ DemoPlugin 未获取到（可能原生侧未注册）"
                        }
                    }

                    // 按钮2: 获取设备信息
                    ctx.createActionButton(
                        this,
                        title = "getDeviceInfo",
                        description = "调用 demo.getDeviceInfo",
                        gradientColors = listOf(0xFF4FACFE, 0xFF00F2FE)
                    ) {
                        Bridge.getPlugin<DemoPlugin>(DemoPlugin.PLUGIN_NAME)
                            ?.getDeviceInfo { result ->
                                val jsonResult = JsonResult(result)
                                ctx.logOutput = if (jsonResult.isSuccess()) {
                                    "✅ 设备信息: ${jsonResult.data}"
                                } else {
                                    "❌ getDeviceInfo 失败: code=${jsonResult.code}, msg=${jsonResult.msg}"
                                }
                            } ?: run {
                            ctx.logOutput = "⚠️ DemoPlugin 未获取到（可能原生侧未注册）"
                        }
                    }

                    // 按钮3: 同步获取时间戳
                    ctx.createActionButton(
                        this,
                        title = "getTimestamp (同步)",
                        description = "调用 demo.getTimestamp",
                        gradientColors = listOf(0xFF43E97B, 0xFF38F9D7)
                    ) {
                        val timestamp = Bridge.getPlugin<DemoPlugin>(DemoPlugin.PLUGIN_NAME)
                            ?.getTimestamp() ?: ""
                        ctx.logOutput = if (timestamp.isNotEmpty()) {
                            "✅ 当前时间戳: $timestamp"
                        } else {
                            "⚠️ 获取时间戳为空（可能原生侧未实现）"
                        }
                    }

                    // 按钮4: 打开 URL
                    ctx.createActionButton(
                        this,
                        title = "openUrl",
                        description = "调用 demo.openUrl",
                        gradientColors = listOf(0xFFFA709A, 0xFFFEE140)
                    ) {
                        Bridge.getPlugin<DemoPlugin>(DemoPlugin.PLUGIN_NAME)
                            ?.openUrl("https://kuikly.tencent.com") { result ->
                                val jsonResult = JsonResult(result)
                                ctx.logOutput = if (jsonResult.isSuccess()) {
                                    "✅ openUrl 成功"
                                } else {
                                    "❌ openUrl 失败: code=${jsonResult.code}, msg=${jsonResult.msg}"
                                }
                            } ?: run {
                            ctx.logOutput = "⚠️ DemoPlugin 未获取到（可能原生侧未注册）"
                        }
                    }

                    // 日志输出区域
                    View {
                        attr {
                            marginTop(24f)
                            width(pagerData.pageViewWidth - 32f)
                            borderRadius(12f)
                            backgroundColor(Color(0xFF2D2D2D))
                            padding(16f)
                        }

                        View {
                            attr {
                                flexDirection(FlexDirection.COLUMN)
                            }

                            Text {
                                attr {
                                    text("📋 调用日志")
                                    fontSize(14f)
                                    fontWeightBold()
                                    color(Color(0xFF4FACFE))
                                    marginBottom(8f)
                                }
                            }

                            Text {
                                attr {
                                    text(ctx.logOutput)
                                    fontSize(13f)
                                    color(Color(0xFFE0E0E0))
                                }
                            }
                        }
                    }

                    // 底部间距
                    View {
                        attr {
                            height(40f)
                        }
                    }
                }
            }
        }
    }

    /**
     * 创建操作按钮
     */
    private fun createActionButton(
        container: ViewContainer<*, *>,
        title: String,
        description: String,
        gradientColors: List<Long>,
        onClick: () -> Unit
    ) {
        with(container) {
            View {
                attr {
                    width(pagerData.pageViewWidth - 32f)
                    marginBottom(12f)
                    borderRadius(12f)
                }

                Button {
                    attr {
                        width(pagerData.pageViewWidth - 32f)
                        height(72f)
                        borderRadius(12f)
                        backgroundLinearGradient(
                            Direction.TO_RIGHT,
                            ColorStop(Color(gradientColors[0]), 0f),
                            ColorStop(Color(gradientColors[1]), 1f)
                        )
                    }

                    View {
                        attr {
                            flexDirection(FlexDirection.ROW)
                            alignItems(FlexAlign.CENTER)
                            justifyContent(FlexJustifyContent.SPACE_BETWEEN)
                            padding(16f)
                        }

                        View {
                            attr {
                                flexDirection(FlexDirection.COLUMN)
                            }

                            Text {
                                attr {
                                    text(title)
                                    fontSize(16f)
                                    fontWeightBold()
                                    color(Color.WHITE)
                                    marginBottom(2f)
                                }
                            }

                            Text {
                                attr {
                                    text(description)
                                    fontSize(12f)
                                    color(Color(0xCCFFFFFF))
                                }
                            }
                        }

                        Text {
                            attr {
                                text("▶")
                                fontSize(18f)
                                color(Color(0xCCFFFFFF))
                            }
                        }
                    }

                    event {
                        click {
                            onClick()
                        }
                    }
                }
            }
        }
    }
}