package com.example.kuiklylibgallery

import com.example.kuiklylibgallery.base.BasePager
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.*
import com.tencent.kuiklyx.viewmodel.ViewModel
import com.tencent.kuiklyx.viewmodel.viewModelStore
import kotlinx.datetime.Clock

// =============================================================================
// 示例 ViewModel：计数器
// =============================================================================

/**
 * 计数器 ViewModel
 *
 * 继承自 kuiklyx-viewmodel 的 ViewModel 基类，
 * 演示生命周期回调：onResumed / onPaused / onCleared
 */
class CounterDemoViewModel(override val pagerId: String) : ViewModel() {

    var count: Int = 0
        private set

    /** 生命周期日志，供 UI 展示 */
    val lifecycleLogs = mutableListOf<String>()

    private fun log(msg: String) {
        val ts = Clock.System.now().toEpochMilliseconds() % 100000
        lifecycleLogs.add(0, "[$ts] $msg")
        // 最多保留 20 条
        if (lifecycleLogs.size > 20) {
            lifecycleLogs.removeAt(lifecycleLogs.lastIndex)
        }
    }

    fun increment() {
        count++
        log("计数 +1 → $count")
    }

    fun decrement() {
        if (count > 0) count--
        log("计数 -1 → $count")
    }

    fun reset() {
        count = 0
        log("计数重置 → 0")
    }

    // ---- 生命周期回调 ----

    override fun onResumed() {
        super.onResumed()
        log("🟢 onResumed() — 页面可见")
    }

    override fun onPaused() {
        super.onPaused()
        log("🟡 onPaused() — 页面不可见")
    }

    override fun onCleared() {
        super.onCleared()
        log("🔴 onCleared() — 页面销毁，资源释放")
        println("CounterDemoViewModel onCleared()")
    }
}

// =============================================================================
// 示例 ViewModel：用户信息
// =============================================================================

/**
 * 用户信息 ViewModel
 *
 * 演示在同一个 Pager 内通过 viewModelStore 获取多个不同类型的 ViewModel
 */
class UserDemoViewModel(override val pagerId: String) : ViewModel() {

    var userName: String = "访客"
        private set
    var isLoggedIn: Boolean = false
        private set
    var loginTimestamp: Long = 0L
        private set

    val lifecycleLogs = mutableListOf<String>()

    private fun log(msg: String) {
        val ts = Clock.System.now().toEpochMilliseconds() % 100000
        lifecycleLogs.add(0, "[$ts] $msg")
        if (lifecycleLogs.size > 20) {
            lifecycleLogs.removeAt(lifecycleLogs.lastIndex)
        }
    }

    fun login(name: String) {
        userName = name
        isLoggedIn = true
        loginTimestamp = Clock.System.now().toEpochMilliseconds()
        log("登录成功: $name")
    }

    fun logout() {
        log("登出: $userName")
        userName = "访客"
        isLoggedIn = false
        loginTimestamp = 0L
    }

    override fun onResumed() {
        super.onResumed()
        log("🟢 onResumed()")
    }

    override fun onPaused() {
        super.onPaused()
        log("🟡 onPaused()")
    }

    override fun onCleared() {
        super.onCleared()
        log("🔴 onCleared()")
        println("UserDemoViewModel onCleared()")
    }
}

// =============================================================================
// Demo 页面
// =============================================================================

/**
 * kuiklyx-viewmodel 演示页面
 *
 * 演示功能：
 * 1. 通过 viewModelStore 委托获取 ViewModel 单例
 * 2. ViewModel 生命周期回调（onResumed / onPaused / onCleared）
 * 3. 同一 Pager 内多个 ViewModel 共存
 */
@Page("ViewModelDemoPage")
internal class ViewModelDemoPage : BasePager() {

    // ---- 通过 viewModelStore 委托获取 ViewModel ----
    private val counterVM by viewModelStore { pagerId ->
        CounterDemoViewModel(pagerId)
    }
    private val userVM by viewModelStore { pagerId ->
        UserDemoViewModel(pagerId)
    }

    // ---- UI 响应式状态 ----
    private var counterValue by observable(0)
    private var userName by observable("访客")
    private var isLoggedIn by observable(false)
    private var loginTimeStr by observable("未登录")
    private var logText by observable("暂无日志...")

    override fun created() {
        super.created()
        syncUI()
    }

    /** 将 ViewModel 数据同步到 UI 响应式字段 */
    private fun syncUI() {
        counterValue = counterVM.count
        userName = userVM.userName
        isLoggedIn = userVM.isLoggedIn
        loginTimeStr = if (userVM.isLoggedIn) "时间戳: ${userVM.loginTimestamp}" else "未登录"
        // 合并两个 ViewModel 的日志
        val allLogs = (counterVM.lifecycleLogs + userVM.lifecycleLogs)
            .sortedByDescending { it }
        logText = if (allLogs.isEmpty()) "暂无日志..." else allLogs.joinToString("\n")
    }

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
                        title = "ViewModel Demo"
                    }
                }

                View {
                    attr {
                        flexDirection(FlexDirection.COLUMN)
                        padding(16f)
                    }

                    // ---- 标题说明 ----
                    Text {
                        attr {
                            text("kuiklyx-viewmodel 演示")
                            fontSize(22f)
                            fontWeightBold()
                            color(Color(0xFF1A1A1A))
                            marginBottom(8f)
                        }
                    }
                    Text {
                        attr {
                            text("通过 viewModelStore 委托获取 ViewModel 实例，\n自动绑定 Pager 生命周期。")
                            fontSize(14f)
                            color(Color(0xFF666666))
                            marginBottom(20f)
                        }
                    }

                    // ========== 1. 计数器 ViewModel ==========
                    ctx.createSectionHeader(this, "CounterDemoViewModel", Color(0xFF6200EE))

                    // 计数器数值
                    View {
                        attr {
                            marginTop(12f)
                            padding(24f)
                            backgroundColor(Color(0xFFEDE7F6))
                            borderRadius(12f)
                            allCenter()
                        }
                        Text {
                            attr {
                                text("${ctx.counterValue}")
                                fontSize(48f)
                                fontWeightBold()
                                color(Color(0xFF6200EE))
                            }
                        }
                    }

                    // 计数器按钮行
                    View {
                        attr {
                            flexDirection(FlexDirection.ROW)
                            marginTop(12f)
                        }

                        ctx.createActionBtn(this, "➖ 减少", Color(0xFFFF5722)) {
                            ctx.counterVM.decrement()
                            ctx.syncUI()
                        }
                        ctx.createActionBtn(this, "➕ 增加", Color(0xFF4CAF50)) {
                            ctx.counterVM.increment()
                            ctx.syncUI()
                        }
                        ctx.createActionBtn(this, "🔄 重置", Color(0xFF9E9E9E)) {
                            ctx.counterVM.reset()
                            ctx.syncUI()
                        }
                    }

                    // ========== 2. 用户 ViewModel ==========
                    ctx.createSectionHeader(this, "UserDemoViewModel", Color(0xFF00897B))

                    // 用户信息卡片
                    View {
                        attr {
                            marginTop(12f)
                            padding(16f)
                            backgroundColor(
                                if (ctx.isLoggedIn) Color(0xFFE8F5E9) else Color(0xFFFFF9C4)
                            )
                            borderRadius(12f)
                        }

                        Text {
                            attr {
                                text("👤 用户: ${ctx.userName}")
                                fontSize(18f)
                                fontWeightBold()
                                color(Color(0xFF1A1A1A))
                            }
                        }
                        Text {
                            attr {
                                text("状态: ${if (ctx.isLoggedIn) "已登录 ✅" else "未登录 ❌"}")
                                fontSize(14f)
                                color(Color(0xFF666666))
                                marginTop(6f)
                            }
                        }
                        Text {
                            attr {
                                text("⏰ ${ctx.loginTimeStr}")
                                fontSize(13f)
                                color(Color(0xFF999999))
                                marginTop(4f)
                            }
                        }
                    }

                    // 登录 / 登出按钮行
                    View {
                        attr {
                            flexDirection(FlexDirection.ROW)
                            marginTop(12f)
                        }

                        ctx.createActionBtn(
                            this,
                            "🔐 登录",
                            if (ctx.isLoggedIn) Color(0xFFBDBDBD) else Color(0xFF2196F3)
                        ) {
                            if (!ctx.userVM.isLoggedIn) {
                                ctx.userVM.login("Demo 用户")
                                ctx.syncUI()
                            }
                        }
                        ctx.createActionBtn(
                            this,
                            "🚪 登出",
                            if (ctx.isLoggedIn) Color(0xFFF44336) else Color(0xFFBDBDBD)
                        ) {
                            if (ctx.userVM.isLoggedIn) {
                                ctx.userVM.logout()
                                ctx.syncUI()
                            }
                        }
                    }

                    // ========== 3. ViewModel 单例验证 ==========
                    ctx.createSectionHeader(this, "单例验证", Color(0xFFFF6F00))

                    Text {
                        attr {
                            text("viewModelStore 保证同一 Pager 内，\n同类型 ViewModel 只有一个实例。\n多次获取返回的是同一个对象。")
                            fontSize(13f)
                            color(Color(0xFF666666))
                            marginTop(8f)
                            marginBottom(8f)
                        }
                    }

                    View {
                        attr {
                            padding(16f)
                            backgroundColor(Color(0xFFFFF3E0))
                            borderRadius(12f)
                        }

                        // 再次通过 viewModelStore 获取，验证单例
                        val verifyVM by ctx.viewModelStore { pagerId ->
                            CounterDemoViewModel(pagerId)
                        }
                        Text {
                            attr {
                                text(
                                    "counterVM === verifyVM : ${ctx.counterVM === verifyVM}\n" +
                                    "counterVM.count = ${ctx.counterVM.count}\n" +
                                    "verifyVM.count  = ${verifyVM.count}"
                                )
                                fontSize(14f)
                                color(Color(0xFFE65100))
                                fontWeightBold()
                            }
                        }
                    }

                    // ========== 4. 生命周期日志 ==========
                    ctx.createSectionHeader(this, "生命周期日志", Color(0xFF37474F))

                    Text {
                        attr {
                            text("切换页面（前后台）可观察 onResumed / onPaused 回调")
                            fontSize(13f)
                            color(Color(0xFF666666))
                            marginTop(8f)
                            marginBottom(8f)
                        }
                    }

                    View {
                        attr {
                            padding(12f)
                            backgroundColor(Color(0xFF263238))
                            borderRadius(8f)
                            minHeight(120f)
                        }
                        Text {
                            attr {
                                text(ctx.logText)
                                fontSize(12f)
                                color(Color(0xFF4CAF50))
                                lineHeight(18f)
                            }
                        }
                    }

                    // ========== 5. API 说明 ==========
                    ctx.createSectionHeader(this, "API 说明", Color(0xFF546E7A))

                    View {
                        attr {
                            marginTop(12f)
                            padding(16f)
                            backgroundColor(Color(0xFFF5F5F5))
                            borderRadius(8f)
                        }
                        Text {
                            attr {
                                text(
                                    """
                                    本页面演示的 kuiklyx-viewmodel API：

                                    ✅ ViewModel (com.tencent.kuiklyx.viewmodel)
                                       - 继承 ViewModel，传入 pagerId
                                       - onResumed()  → 页面可见
                                       - onPaused()   → 页面不可见
                                       - onCleared()  → 页面销毁

                                    ✅ viewModelStore 委托
                                       - Pager 中: val vm by viewModelStore { ... }
                                       - ComposeView 中同样支持
                                       - 同类型 ViewModel 保证单例

                                    ✅ 自动生命周期绑定
                                       - 与 Pager 的 DID_APPEAR / DID_DISAPPEAR 关联
                                       - 页面销毁时自动调用 onCleared()
                                    """.trimIndent()
                                )
                                fontSize(14f)
                                color(Color(0xFF424242))
                                lineHeight(22f)
                            }
                        }
                    }

                    // 底部留白
                    View {
                        attr {
                            height(32f)
                        }
                    }
                }
            }
        }
    }

    // ---- 辅助方法 ----

    /** 创建章节标题 */
    private fun createSectionHeader(
        container: ViewContainer<*, *>,
        title: String,
        bgColor: Color
    ) {
        with(container) {
            View {
                attr {
                    marginTop(20f)
                    padding(10f)
                    paddingLeft(14f)
                    paddingRight(14f)
                    backgroundColor(bgColor)
                    borderRadius(8f)
                }
                Text {
                    attr {
                        text(title)
                        fontSize(16f)
                        fontWeightBold()
                        color(Color.WHITE)
                    }
                }
            }
        }
    }

    /** 创建操作按钮（平分宽度） */
    private fun createActionBtn(
        container: ViewContainer<*, *>,
        label: String,
        bgColor: Color,
        onClick: () -> Unit
    ) {
        with(container) {
            View {
                attr {
                    flex(1f)
                    margin(4f)
                    padding(14f)
                    backgroundColor(bgColor)
                    borderRadius(8f)
                    allCenter()
                }
                event {
                    click { onClick() }
                }
                Text {
                    attr {
                        text(label)
                        fontSize(15f)
                        fontWeightBold()
                        color(Color.WHITE)
                    }
                }
            }
        }
    }
}
