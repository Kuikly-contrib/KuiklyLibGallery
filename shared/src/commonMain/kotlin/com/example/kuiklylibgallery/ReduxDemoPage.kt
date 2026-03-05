package com.example.kuiklylibgallery

import com.example.kuiklylibgallery.base.BasePager
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.layout.FlexAlign
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuiklyx.redux.ReduxComposeView
import com.tencent.kuiklyx.redux.Store
import com.tencent.kuiklyx.redux.Reducer
import com.tencent.kuiklyx.redux.createStore

// =============================================================================
// State 定义
// =============================================================================

/**
 * 全局应用状态
 * 包含计数器和主题色两个状态字段，用于演示 Redux 状态管理
 */
internal data class ReduxAppState(
    val count: Int = 0,
    val themeColorIndex: Int = 0
)

// =============================================================================
// Action 定义
// =============================================================================

/** 增加计数 */
internal data class Increment(val amount: Int = 1)

/** 减少计数 */
internal data class Decrement(val amount: Int = 1)

/** 重置计数 */
internal object ResetCount

/** 切换主题色 */
internal object SwitchTheme

// =============================================================================
// Reducer 定义
// =============================================================================

/** 主题色列表 */
private val themeColors = listOf(
    Color(0xFF6200EE), // 紫色
    Color(0xFF03DAC5), // 青色
    Color(0xFFFF5722), // 橙色
    Color(0xFF4CAF50), // 绿色
    Color(0xFF2196F3), // 蓝色
    Color(0xFFE91E63), // 粉色
)

/**
 * Reducer：根据 Action 生成新的 State
 * 纯函数，不可变状态，单向数据流
 */
internal val appReducer: Reducer<ReduxAppState> = { state: ReduxAppState, action: Any ->
    when (action) {
        is Increment -> state.copy(count = state.count + action.amount)
        is Decrement -> state.copy(count = state.count - action.amount)
        is ResetCount -> state.copy(count = 0)
        is SwitchTheme -> state.copy(themeColorIndex = (state.themeColorIndex + 1) % themeColors.size)
        else -> state
    }
}

// =============================================================================
// CountView 组件 —— 展示计数，通过 UseSelector 订阅状态
// =============================================================================

internal class CountView(store: Store<ReduxAppState>) :
    ReduxComposeView<ReduxAppState, CountViewAttr, CountViewEvent>(store) {

    /** 通过 UseSelector 订阅 count 状态，自动更新视图 */
    private val count by UseSelector { it.count }

    /** 通过 UseSelector 订阅主题色索引 */
    private val themeColorIndex by UseSelector { it.themeColorIndex }

    override fun createEvent(): CountViewEvent = CountViewEvent()
    override fun createAttr(): CountViewAttr = CountViewAttr()

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    marginTop(12f)
                    marginLeft(16f)
                    marginRight(16f)
                    padding(24f)
                    backgroundColor(Color(0xFFF5F5F5))
                    borderRadius(16f)
                    alignItems(FlexAlign.CENTER)
                }

                // 计数数值
                Text {
                    attr {
                        text("${ctx.count}")
                        fontSize(64f)
                        fontWeightBold()
                        color(themeColors[ctx.themeColorIndex])
                    }
                }

                // 标签
                Text {
                    attr {
                        text("当前计数")
                        fontSize(14f)
                        color(Color(0xFF999999))
                        marginTop(4f)
                    }
                }
            }
        }
    }
}

internal class CountViewAttr : ComposeAttr()
internal class CountViewEvent : ComposeEvent()

internal fun ViewContainer<*, *>.CountDisplay(
    store: Store<ReduxAppState>,
    init: CountView.() -> Unit
) {
    addChild(CountView(store), init)
}

// =============================================================================
// ButtonsView 组件 —— 通过 store.dispatch 发送 Action 更新状态
// =============================================================================

internal class ButtonsView(store: Store<ReduxAppState>) :
    ReduxComposeView<ReduxAppState, ButtonsViewAttr, ButtonsViewEvent>(store) {

    override fun createEvent(): ButtonsViewEvent = ButtonsViewEvent()
    override fun createAttr(): ButtonsViewAttr = ButtonsViewAttr()

    /**
     * 创建操作按钮
     */
    private fun buildButton(
        btnText: String,
        bgColor: Color,
        onClick: () -> Unit
    ): ViewBuilder {
        return {
            Button {
                attr {
                    titleAttr {
                        text(btnText)
                        color(Color.WHITE)
                        fontSize(16f)
                    }
                    padding(12f)
                    backgroundColor(bgColor)
                    borderRadius(8f)
                    height(48f)
                    flex(1f)
                    margin(4f)
                    allCenter()
                }
                event {
                    click {
                        onClick.invoke()
                    }
                }
            }
        }
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    flexDirection(FlexDirection.COLUMN)
                    marginLeft(16f)
                    marginRight(16f)
                    marginTop(16f)
                }

                // 第一行：+1 / -1
                View {
                    attr {
                        flexDirection(FlexDirection.ROW)
                        marginBottom(8f)
                    }

                    ctx.buildButton("➕ 加 1", Color(0xFF4CAF50)) {
                        ctx.store.dispatch(Increment(1))
                    }.invoke(this)

                    ctx.buildButton("➖ 减 1", Color(0xFFFF5722)) {
                        ctx.store.dispatch(Decrement(1))
                    }.invoke(this)
                }

                // 第二行：+5 / -5
                View {
                    attr {
                        flexDirection(FlexDirection.ROW)
                        marginBottom(8f)
                    }

                    ctx.buildButton("➕ 加 5", Color(0xFF2196F3)) {
                        ctx.store.dispatch(Increment(5))
                    }.invoke(this)

                    ctx.buildButton("➖ 减 5", Color(0xFFE91E63)) {
                        ctx.store.dispatch(Decrement(5))
                    }.invoke(this)
                }

                // 第三行：重置 / 切换主题
                View {
                    attr {
                        flexDirection(FlexDirection.ROW)
                    }

                    ctx.buildButton("🔄 重置", Color(0xFF9E9E9E)) {
                        ctx.store.dispatch(ResetCount)
                    }.invoke(this)

                    ctx.buildButton("🎨 切换主题", Color(0xFF6200EE)) {
                        ctx.store.dispatch(SwitchTheme)
                    }.invoke(this)
                }
            }
        }
    }
}

internal class ButtonsViewAttr : ComposeAttr()
internal class ButtonsViewEvent : ComposeEvent()

internal fun ViewContainer<*, *>.ActionButtons(
    store: Store<ReduxAppState>,
    init: ButtonsView.() -> Unit
) {
    addChild(ButtonsView(store), init)
}

// =============================================================================
// StatusView 组件 —— 展示 Redux 状态详情
// =============================================================================

internal class StatusView(store: Store<ReduxAppState>) :
    ReduxComposeView<ReduxAppState, StatusViewAttr, StatusViewEvent>(store) {

    private val count by UseSelector { it.count }
    private val themeColorIndex by UseSelector { it.themeColorIndex }

    override fun createEvent(): StatusViewEvent = StatusViewEvent()
    override fun createAttr(): StatusViewAttr = StatusViewAttr()

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    marginTop(20f)
                    marginLeft(16f)
                    marginRight(16f)
                    padding(16f)
                    backgroundColor(Color(0xFF263238))
                    borderRadius(12f)
                }

                Text {
                    attr {
                        text("📊 Redux State 详情")
                        fontSize(16f)
                        fontWeightBold()
                        color(Color(0xFF4CAF50))
                        marginBottom(8f)
                    }
                }

                Text {
                    attr {
                        text(
                            "State {\n" +
                            "  count: ${ctx.count}\n" +
                            "  themeColorIndex: ${ctx.themeColorIndex}\n" +
                            "  themeColor: ${ctx.getColorName(ctx.themeColorIndex)}\n" +
                            "}"
                        )
                        fontSize(14f)
                        color(Color(0xFFE0E0E0))
                        lineHeight(22f)
                    }
                }
            }
        }
    }

    private fun getColorName(index: Int): String {
        return when (index) {
            0 -> "紫色 (Purple)"
            1 -> "青色 (Teal)"
            2 -> "橙色 (Orange)"
            3 -> "绿色 (Green)"
            4 -> "蓝色 (Blue)"
            5 -> "粉色 (Pink)"
            else -> "未知"
        }
    }
}

internal class StatusViewAttr : ComposeAttr()
internal class StatusViewEvent : ComposeEvent()

internal fun ViewContainer<*, *>.StateStatus(
    store: Store<ReduxAppState>,
    init: StatusView.() -> Unit
) {
    addChild(StatusView(store), init)
}

// =============================================================================
// ReduxDemoPage 主页面
// =============================================================================

/**
 * kuiklyx-redux 演示页面
 *
 * 演示功能：
 * 1. 全局状态（State）定义 —— data class 不可变状态
 * 2. 动作（Action）定义 —— data class / object 描述意图
 * 3. Reducer 纯函数 —— 根据 Action 生成新 State
 * 4. Store 初始化与传递 —— createStore 创建，传递给子组件
 * 5. UseSelector 订阅状态 —— 自动响应式更新视图
 * 6. store.dispatch 分发动作 —— 触发状态变更
 */
@Page("ReduxDemoPage")
internal class ReduxDemoPage : BasePager() {

    /** 创建 Redux Store，传入 reducer 和初始 State */
    private val store = createStore(
        appReducer,
        ReduxAppState()
    )

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr {
                backgroundColor(Color(0xFFF5F7FA))
            }

            List {
                attr {
                    flex(1f)
                }

                // 导航栏
                RouterNavBar {
                    attr {
                        title = "Redux Demo"
                    }
                }

                View {
                    attr {
                        flexDirection(FlexDirection.COLUMN)
                        padding(0f)
                    }

                    // 标题说明
                    View {
                        attr {
                            marginLeft(16f)
                            marginRight(16f)
                            marginTop(16f)
                        }
                        Text {
                            attr {
                                text("kuiklyx-redux 演示")
                                fontSize(22f)
                                fontWeightBold()
                                color(Color(0xFF1A1A1A))
                                marginBottom(4f)
                            }
                        }
                        Text {
                            attr {
                                text("单向数据流 · 单一数据源 · 不可变状态")
                                fontSize(14f)
                                color(Color(0xFF666666))
                                marginBottom(4f)
                            }
                        }
                    }

                    // ===== 计数器展示区（两个 CountView 共享同一 Store）=====
                    View {
                        attr {
                            marginLeft(16f)
                            marginRight(16f)
                            marginTop(16f)
                            padding(8f)
                            backgroundColor(Color(0xFFEDE7F6))
                            borderRadius(12f)
                        }
                        Text {
                            attr {
                                text("📌 两个 CountView 共享同一个 Store")
                                fontSize(13f)
                                color(Color(0xFF6200EE))
                                marginBottom(4f)
                            }
                        }
                    }

                    // 第一个 CountView
                    CountDisplay(ctx.store) {}

                    // 第二个 CountView（与第一个共享 Store，验证状态同步）
                    CountDisplay(ctx.store) {}

                    // ===== 操作按钮区 =====
                    ActionButtons(ctx.store) {}

                    // ===== State 状态详情 =====
                    StateStatus(ctx.store) {}

                    // ===== 原理说明 =====
                    View {
                        attr {
                            marginTop(20f)
                            marginLeft(16f)
                            marginRight(16f)
                            padding(16f)
                            backgroundColor(Color(0xFFF5F5F5))
                            borderRadius(8f)
                        }
                        Text {
                            attr {
                                text(
                                    """
                                    📖 kuiklyx-redux 核心概念：

                                    ✅ State（状态）
                                       - data class，不可变
                                       - 单一数据源，所有状态集中管理

                                    ✅ Action（动作）
                                       - 描述"发生了什么"
                                       - data class 携带 payload

                                    ✅ Reducer（归约器）
                                       - 纯函数: (State, Action) → State
                                       - 通过 state.copy() 生成新状态

                                    ✅ Store（仓库）
                                       - createStore(reducer, initialState)
                                       - store.dispatch(action) 分发动作
                                       - 传递给 ReduxComposeView 子组件

                                    ✅ UseSelector（选择器）
                                       - val x by UseSelector { it.xxx }
                                       - 自动订阅状态变化，响应式更新 UI

                                    ✅ ReduxComposeView（基类）
                                       - 连接 Redux Store 与 Kuikly 视图
                                       - 提供 UseSelector、store 等能力
                                    """.trimIndent()
                                )
                                fontSize(13f)
                                color(Color(0xFF424242))
                                lineHeight(20f)
                            }
                        }
                    }

                    // 数据流示意
                    View {
                        attr {
                            marginTop(16f)
                            marginLeft(16f)
                            marginRight(16f)
                            marginBottom(32f)
                            padding(16f)
                            backgroundColor(Color(0xFFE8EAF6))
                            borderRadius(8f)
                        }
                        Text {
                            attr {
                                text(
                                    "🔄 数据流向：\n\n" +
                                    "  用户点击按钮\n" +
                                    "    ↓\n" +
                                    "  store.dispatch(Action)\n" +
                                    "    ↓\n" +
                                    "  Reducer(State, Action) → NewState\n" +
                                    "    ↓\n" +
                                    "  UseSelector 自动检测变化\n" +
                                    "    ↓\n" +
                                    "  UI 自动更新 ✨"
                                )
                                fontSize(14f)
                                color(Color(0xFF303F9F))
                                lineHeight(22f)
                            }
                        }
                    }
                }
            }
        }
    }
}
