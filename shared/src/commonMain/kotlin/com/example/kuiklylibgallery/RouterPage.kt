package com.example.kuiklylibgallery

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.utils.urlParams
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.reactive.handler.*
import com.example.kuiklylibgallery.base.BasePager
import com.example.kuiklylibgallery.base.bridgeModule
import com.tencent.kuikly.core.layout.FlexAlign
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.layout.FlexJustifyContent

@Page("router", supportInLocal = true)
internal class RouterPage : BasePager() {

    lateinit var inputRef: ViewRef<InputView>

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

                View {
                    attr {
                        flexDirection(FlexDirection.COLUMN)
                        padding(16f)
                    }

                    // 标题区域
                    View {
                        attr {
                            marginBottom(24f)
                        }

                        Text {
                            attr {
                                text("Kuikly 组件库")
                                fontSize(28f)
                                fontWeightBold()
                                color(Color(0xFF1A1A1A))
                            }
                        }
                    }




                    // AtomicFu 卡片
                    ctx.createDemoCard(
                        this,
                        title = "AtomicFu",
                        description = "原子操作和并发控制",
                        gradientColors = listOf(0xFF667EEA, 0xFF764BA2),
                    ) {
                        ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                            .openPage("AtomicFuDSLTestPage")
                    }


                    // Collection 卡片
                    ctx.createDemoCard(
                        this,
                        title = "Collection",
                        description = "高性能集合类库",
                        gradientColors = listOf(0xFFF093FB, 0xFFF5576C)
                    ) {
                        ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                            .openPage("CollectionTestPage")
                    }


                    // DateTime 卡片
                    ctx.createDemoCard(
                        this,
                        title = "DateTime",
                        description = "日期时间处理工具",
                        gradientColors = listOf(0xFF4FACFE, 0xFF00F2FE)
                    ) {
                        ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                            .openPage("DateTimeDemoTestPage")
                    }


                    // Serialization 卡片
                    ctx.createDemoCard(
                        this,
                        title = "Serialization",
                        description = "序列号工具",
                        gradientColors = listOf(0xFF313F2FE, 0xFF00F2FE)
                    ) {
                        ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                            .openPage("SerializationDemo")
                    }

                    // Okio 卡片
                    ctx.createDemoCard(
                        this,
                        title = "Okio",
                        description = "高效的 I/O 操作库",
                        gradientColors = listOf(0xFF43E97B, 0xFF38F9D7)
                    ) {
                        ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                            .openPage("OkioDemoPage")
                    }

                    // Coroutine 卡片
                    ctx.createDemoCard(
                        this,
                        title = "Coroutines",
                        description = "Kotlin 协程异步编程",
                        gradientColors = listOf(0xFFFA709A, 0xFFFEE140)
                    ) {
                        ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                            .openPage("CoroutineDemoPage")
                    }

                    // Lifecycle 卡片
                    ctx.createDemoCard(
                        this,
                        title = "Lifecycle",
                        description = "ViewModel 和生命周期管理",
                        gradientColors = listOf(0xFF6200EE, 0xFF3700B3)
                    ) {
                        ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                            .openPage("LifecycleDemoPage")
                    }

                    // ktor 卡片
                    ctx.createDemoCard(
                        this,
                        title = "Ktor",
                        description = "Ktor 网络库",
                        gradientColors = listOf(0xFF00BFA5, 0xFF1DE9B6)
                    ) {
                        ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                            .openPage("KtorDemoPage")
                    }

                    // JsonMate 卡片
                    ctx.createDemoCard(
                        this,
                        title = "JsonMate",
                        description = "JSON 反序列化代码生成",
                        gradientColors = listOf(0xFFFF6B6B, 0xFFFFE66D)
                    ) {
                        ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                            .openPage("JsonMateDemoPage")
                    }

                    // JCE 卡片
                    ctx.createDemoCard(
                        this,
                        title = "JCE",
                        description = "高效二进制序列化协议",
                        gradientColors = listOf(0xFF009688, 0xFF4DB6AC)
                    ) {
                        ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                            .openPage("JceDemoPage")
                    }

                    // Lottie 卡片
                    ctx.createDemoCard(
                        this,
                        title = "Lottie",
                        description = "跨端 Lottie 动画组件",
                        gradientColors = listOf(0xFF6200EE, 0xFFBB86FC)
                    ) {
                        ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                            .openPage("LottieDemoPage")
                    }

                    // MMKV 卡片
                    ctx.createDemoCard(
                        this,
                        title = "MMKV",
                        description = "高性能 KV 存储组件",
                        gradientColors = listOf(0xFFE91E63, 0xFFFF5722)
                    ) {
                        ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                            .openPage("MMKVDemoPage")
                    }

                    // 底部间距
                    View {
                        attr {
                            height(20f)
                        }
                    }
                }
            }
        }
    }

    // 创建分类标题
    private fun createSectionTitle(container: ViewContainer<*,*>,title: String) {
        with(container){
            View {
                attr {
                    marginTop(16f)
                    marginBottom(12f)
                }

                Text {
                    attr {
                        text(title)
                        fontSize(18f)
                        fontWeightSemisolid()
                        color(Color(0xFF666666))
                    }
                }
            }
        }
    }

    // 创建演示卡片
    private fun createDemoCard(
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
                    marginBottom(16f)
                    borderRadius(16f)
                    backgroundColor(Color.WHITE)
                }

                Button {
                    attr {
                        width(pagerData.pageViewWidth - 32f)
                        height(100f)
                        borderRadius(16f)
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
                            padding(20f)
                        }
                        

                        // 文字区域
                        View {
                            attr {
                                flex(1f)
                                flexDirection(FlexDirection.COLUMN)
                                justifyContent(FlexJustifyContent.CENTER)
                            }

                            Text {
                                attr {
                                    text(title)
                                    fontSize(20f)
                                    fontWeightBold()
                                    color(Color.WHITE)
                                    marginBottom(4f)
                                }
                            }

                            Text {
                                attr {
                                    text(description)
                                    fontSize(14f)
                                    color(Color(0xE6FFFFFF))
                                }
                            }
                        }

                        // 箭头图标
                        Text {
                            attr {
                                text("→")
                                fontSize(24f)
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

internal class RouterNavigationBar : ComposeView<RouterNavigationBarAttr, ComposeEvent>() {
    override fun createEvent(): ComposeEvent {
        return ComposeEvent()
    }

    override fun createAttr(): RouterNavigationBarAttr {
        return RouterNavigationBarAttr()
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    paddingTop(ctx.pagerData.statusBarHeight)
                    backgroundColor(Color.WHITE)
                }
                // nav bar
                View {
                    attr {
                        height(44f)
                        allCenter()
                    }

                    Text {
                        attr {
                            text(ctx.attr.title)
                            fontSize(17f)
                            fontWeightSemisolid()
                            backgroundLinearGradient(
                                Direction.TO_BOTTOM,
                                ColorStop(Color(0xFF23D3FD), 0f),
                                ColorStop(Color(0xFFAD37FE), 1f)
                            )

                        }
                    }

                }

                vif({ !ctx.attr.backDisable }) {
                    Image {
                        attr {
                            absolutePosition(
                                top = 12f + getPager().pageData.statusBarHeight,
                                left = 12f,
                                bottom = 12f,
                                right = 12f
                            )
                            size(10f, 17f)
                            src("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAASBAMAAAB/WzlGAAAAElBMVEUAAAAAAAAAAAAAAAAAAAAAAADgKxmiAAAABXRSTlMAIN/PELVZAGcAAAAkSURBVAjXYwABQTDJqCQAooSCHUAcVROCHBiFECTMhVoEtRYA6UMHzQlOjQIAAAAASUVORK5CYII=")
                        }
                        event {
                            click {
                                getPager().acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                    .closePage()
                            }
                        }
                    }
                }

            }
        }
    }
}

internal class RouterNavigationBarAttr : ComposeAttr() {
    var title: String by observable("")
    var backDisable = false
}

internal fun ViewContainer<*, *>.RouterNavBar(init: RouterNavigationBar.() -> Unit) {
    addChild(RouterNavigationBar(), init)
}