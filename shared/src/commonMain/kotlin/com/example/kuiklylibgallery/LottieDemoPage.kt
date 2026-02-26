package com.example.kuiklylibgallery

import com.example.kuiklylibgallery.base.BasePager
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.layout.FlexAlign
import com.tencent.kuikly.core.layout.FlexJustifyContent
import com.kuikly.kuiklylottie.LottieView

/**
 * Lottie 动画演示页面
 *
 * kLottieView 是基于 Kuikly 扩展原生 View 机制对 Lottie 的跨端封装组件
 * 支持在 Kuikly 工程中播放 Adobe After Effects 导出的动画
 * 覆盖 Android、iOS、HarmonyOS (Ohos) 三端
 */
@Page("LottieDemoPage")
internal class LottieDemoPage : BasePager() {

    // 动画状态
    private var isPlaying by observable(true)
    private var isLoop by observable(true)
    private var currentProgress by observable(0f)
    private var statusMessage by observable("动画已加载，自动播放中...")

    // LottieView 引用，用于控制播放
    private var lottieViewRef: LottieView? = null

    override fun body(): ViewBuilder {
        val ctx = this

        return {
            attr {
                backgroundColor(Color.WHITE)
            }

            List {
                attr {
                    flex(1f)
                }

                // 标题
                View {
                    attr {
                        margin(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFF6200EE))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text("Lottie 动画演示")
                            fontSize(18f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                }

                // 说明
                View {
                    attr {
                        margin(16f)
                        padding(12f)
                        backgroundColor(Color(0xFFEDE7F6))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text("""
                                kLottieView 是跨端 Lottie 动画组件：

                                • 支持播放 Adobe After Effects 导出的动画
                                • 覆盖 Android、iOS、HarmonyOS 三端
                                • 支持自动播放、循环播放
                                • 提供播放控制方法（play/pause/resume）
                                • 支持进度监听和控制
                                
                                请确保 assets/common/ 目录下有 Lottie JSON 文件
                            """.trimIndent())
                            fontSize(14f)
                            color(Color(0xFF424242))
                            lineHeight(22f)
                        }
                    }
                }

                // 动画展示区域
                View {
                    attr {
                        margin(16f)
                        padding(16f)
                        backgroundColor(Color(0xFFF5F5F5))
                        borderRadius(12f)
                        alignItems(FlexAlign.CENTER)
                    }

                    Text {
                        attr {
                            text("动画展示区")
                            fontSize(16f)
                            fontWeightBold()
                            color(Color(0xFF6200EE))
                            marginBottom(12f)
                        }
                    }

                    // Lottie 动画视图
                    LottieView {
                        ctx.lottieViewRef = this
                        attr {
                            size(250f, 250f)
                            // 使用聊天机器人动画
                            src("common/chatbot.json")
                            autoPlay(true)
                            loop(ctx.isLoop)
                        }
                        event {
                            onAnimationLoaded {
                                ctx.statusMessage = "✅ 动画加载成功！"
                                ctx.isPlaying = true
                            }
                            onAnimationLoadFailed {
                                ctx.statusMessage = "❌ 动画加载失败，请检查文件路径"
                            }
                            onAnimationUpdate { progress ->
                                ctx.currentProgress = progress
                            }
                            onAnimationComplete {
                                ctx.statusMessage = "🎬 动画播放完成"
                                ctx.isPlaying = false
                            }
                            onAnimationRepeat {
                                ctx.statusMessage = "🔄 动画循环播放中..."
                            }
                        }
                    }

                    // 状态显示
                    View {
                        attr {
                            marginTop(12f)
                            padding(8f)
                            backgroundColor(Color(0xFFE8EAF6))
                            borderRadius(4f)
                            width(ctx.pagerData.pageViewWidth - 80f)
                        }
                        Text {
                            attr {
                                text(ctx.statusMessage)
                                fontSize(13f)
                                color(Color(0xFF3F51B5))
                            }
                        }
                    }

                    // 进度显示
                    View {
                        attr {
                            marginTop(8f)
                        }
                        Text {
                            attr {
                                val progressPercent = (ctx.currentProgress * 100).toInt()
                                text("播放进度: $progressPercent%")
                                fontSize(14f)
                                color(Color(0xFF666666))
                            }
                        }
                    }
                }

                // 控制按钮区域
                View {
                    attr {
                        margin(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFF6200EE))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text("播放控制")
                            fontSize(18f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                }

                View {
                    attr {
                        margin(16f)
                        padding(16f)
                        backgroundColor(Color(0xFFF5F5F5))
                        borderRadius(8f)
                    }

                    // 第一行按钮：播放/暂停/继续
                    View {
                        attr {
                            flexDirection(FlexDirection.ROW)
                            justifyContent(FlexJustifyContent.SPACE_BETWEEN)
                            marginBottom(12f)
                        }

                        // 播放按钮
                        Button {
                            attr {
                                size((ctx.pagerData.pageViewWidth - 80f) / 3 - 8f, 44f)
                                borderRadius(8f)
                                backgroundColor(Color(0xFF4CAF50))
                                highlightBackgroundColor(Color(0xFF388E3C))
                                titleAttr {
                                    text("▶ 播放")
                                    color(Color.WHITE)
                                    fontSize(14f)
                                }
                            }
                            event {
                                click {
                                    ctx.lottieViewRef?.play()
                                    ctx.isPlaying = true
                                    ctx.statusMessage = "▶ 从头开始播放"
                                }
                            }
                        }

                        // 暂停按钮
                        Button {
                            attr {
                                size((ctx.pagerData.pageViewWidth - 80f) / 3 - 8f, 44f)
                                borderRadius(8f)
                                backgroundColor(Color(0xFFFF9800))
                                highlightBackgroundColor(Color(0xFFF57C00))
                                titleAttr {
                                    text("⏸ 暂停")
                                    color(Color.WHITE)
                                    fontSize(14f)
                                }
                            }
                            event {
                                click {
                                    ctx.lottieViewRef?.pause()
                                    ctx.isPlaying = false
                                    ctx.statusMessage = "⏸ 动画已暂停"
                                }
                            }
                        }

                        // 继续按钮
                        Button {
                            attr {
                                size((ctx.pagerData.pageViewWidth - 80f) / 3 - 8f, 44f)
                                borderRadius(8f)
                                backgroundColor(Color(0xFF2196F3))
                                highlightBackgroundColor(Color(0xFF1976D2))
                                titleAttr {
                                    text("⏯ 继续")
                                    color(Color.WHITE)
                                    fontSize(14f)
                                }
                            }
                            event {
                                click {
                                    ctx.lottieViewRef?.resume()
                                    ctx.isPlaying = true
                                    ctx.statusMessage = "⏯ 继续播放"
                                }
                            }
                        }
                    }

                    // 第二行按钮：进度控制
                    View {
                        attr {
                            flexDirection(FlexDirection.ROW)
                            justifyContent(FlexJustifyContent.SPACE_BETWEEN)
                            marginBottom(12f)
                        }

                        // 跳转到 0%
                        Button {
                            attr {
                                size((ctx.pagerData.pageViewWidth - 80f) / 4 - 6f, 44f)
                                borderRadius(8f)
                                backgroundColor(Color(0xFF9C27B0))
                                highlightBackgroundColor(Color(0xFF7B1FA2))
                                titleAttr {
                                    text("0%")
                                    color(Color.WHITE)
                                    fontSize(14f)
                                }
                            }
                            event {
                                click {
                                    ctx.lottieViewRef?.progress(0f)
                                    ctx.statusMessage = "跳转到 0%"
                                }
                            }
                        }

                        // 跳转到 25%
                        Button {
                            attr {
                                size((ctx.pagerData.pageViewWidth - 80f) / 4 - 6f, 44f)
                                borderRadius(8f)
                                backgroundColor(Color(0xFF9C27B0))
                                highlightBackgroundColor(Color(0xFF7B1FA2))
                                titleAttr {
                                    text("25%")
                                    color(Color.WHITE)
                                    fontSize(14f)
                                }
                            }
                            event {
                                click {
                                    ctx.lottieViewRef?.progress(0.25f)
                                    ctx.statusMessage = "跳转到 25%"
                                }
                            }
                        }

                        // 跳转到 50%
                        Button {
                            attr {
                                size((ctx.pagerData.pageViewWidth - 80f) / 4 - 6f, 44f)
                                borderRadius(8f)
                                backgroundColor(Color(0xFF9C27B0))
                                highlightBackgroundColor(Color(0xFF7B1FA2))
                                titleAttr {
                                    text("50%")
                                    color(Color.WHITE)
                                    fontSize(14f)
                                }
                            }
                            event {
                                click {
                                    ctx.lottieViewRef?.progress(0.5f)
                                    ctx.statusMessage = "跳转到 50%"
                                }
                            }
                        }

                        // 跳转到 75%
                        Button {
                            attr {
                                size((ctx.pagerData.pageViewWidth - 80f) / 4 - 6f, 44f)
                                borderRadius(8f)
                                backgroundColor(Color(0xFF9C27B0))
                                highlightBackgroundColor(Color(0xFF7B1FA2))
                                titleAttr {
                                    text("75%")
                                    color(Color.WHITE)
                                    fontSize(14f)
                                }
                            }
                            event {
                                click {
                                    ctx.lottieViewRef?.progress(0.75f)
                                    ctx.statusMessage = "跳转到 75%"
                                }
                            }
                        }
                    }

                    // 循环开关
                    View {
                        attr {
                            flexDirection(FlexDirection.ROW)
                            alignItems(FlexAlign.CENTER)
                            justifyContent(FlexJustifyContent.CENTER)
                        }

                        Button {
                            attr {
                                size(ctx.pagerData.pageViewWidth - 80f, 44f)
                                borderRadius(8f)
                                backgroundColor(if (ctx.isLoop) Color(0xFF00BCD4) else Color(0xFF607D8B))
                                highlightBackgroundColor(if (ctx.isLoop) Color(0xFF0097A7) else Color(0xFF455A64))
                                titleAttr {
                                    text(if (ctx.isLoop) "🔄 循环播放: 开启" else "➡️ 循环播放: 关闭")
                                    color(Color.WHITE)
                                    fontSize(14f)
                                }
                            }
                            event {
                                click {
                                    ctx.isLoop = !ctx.isLoop
                                    ctx.statusMessage = if (ctx.isLoop) "循环播放已开启" else "循环播放已关闭"
                                }
                            }
                        }
                    }
                }

                // API 参考
                View {
                    attr {
                        margin(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFF6200EE))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text("API 参考")
                            fontSize(18f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                }

                View {
                    attr {
                        margin(16f)
                        padding(16f)
                        backgroundColor(Color(0xFFF5F5F5))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text("""
                                属性 (Attr):
                                • src(path) - Lottie JSON 文件路径
                                • imagePath(path) - 图片资源文件夹路径
                                • autoPlay(bool) - 自动播放，默认 true
                                • loop(bool) - 循环播放，默认 true

                                方法 (Method):
                                • play() - 从头开始播放
                                • pause() - 暂停播放
                                • resume() - 从暂停处继续
                                • progress(0~1) - 设置播放进度

                                事件 (Event):
                                • onAnimationLoaded - 加载成功
                                • onAnimationLoadFailed - 加载失败
                                • onAnimationUpdate - 进度更新
                                • onAnimationComplete - 播放完成
                                • onAnimationRepeat - 循环重复

                                使用示例:
                                LottieView {
                                    attr {
                                        size(300f, 300f)
                                        src("common/animation.json")
                                        autoPlay(true)
                                        loop(true)
                                    }
                                    event {
                                        onAnimationLoaded { play() }
                                    }
                                }
                            """.trimIndent())
                            fontSize(13f)
                            color(Color(0xFF424242))
                            lineHeight(20f)
                        }
                    }
                }

                // 底部间距
                View {
                    attr {
                        height(32f)
                    }
                }
            }
        }
    }
}
