package com.example.kuiklylibgallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import kotlinx.datetime.Clock
import com.example.kuiklylibgallery.base.BasePager
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View

/**
 * 示例 ViewModel - 计数器
 */
class CounterViewModel : ViewModel() {
    var count: Int = 0
        private set

    fun increment() {
        count++
    }

    fun decrement() {
        if (count > 0) count--
    }

    fun reset() {
        count = 0
    }

    override fun onCleared() {
        super.onCleared()
        println("CounterViewModel onCleared() 被调用 - ViewModel 被清理")
    }
}

/**
 * 示例 ViewModel - 用户信息
 */
class UserViewModel : ViewModel() {
    var userName: String = "访客"
        private set
    var loginTime: Long = 0
        private set
    var isLoggedIn: Boolean = false
        private set

    fun login(name: String) {
        userName = name
        loginTime = Clock.System.now().toEpochMilliseconds()
        isLoggedIn = true
    }

    fun logout() {
        userName = "访客"
        loginTime = 0
        isLoggedIn = false
    }

    override fun onCleared() {
        super.onCleared()
        println("UserViewModel onCleared() 被调用 - ViewModel 被清理")
    }
}

/**
 * 自定义 ViewModelStoreOwner 实现
 */
class MyViewModelStoreOwner : ViewModelStoreOwner {
    private val store = ViewModelStore()

    override val viewModelStore: ViewModelStore
        get() = store

    fun clear() {
        store.clear()
    }
}

/**
 * Lifecycle 库使用示例页面
 * 展示 ViewModel 和 Lifecycle 的各种功能
 */
@Page("LifecycleDemoPage")
internal class LifecycleDemoPage : BasePager() {

    // ViewModelStoreOwner
    private val viewModelStoreOwner = MyViewModelStoreOwner()

    // ViewModels
    private lateinit var counterViewModel: CounterViewModel
    private lateinit var userViewModel: UserViewModel

    // UI 状态（响应式）
    private var counterValue by observable(0)
    private var userName by observable("访客")
    private var isLoggedIn by observable(false)
    private var loginTimeStr by observable("未登录")
    private var logMessages by observable("")
    private var viewModelStatus by observable("ViewModel 状态：未初始化")

    private var isInitialized = false

    override fun created() {
        super.created()
        initViewModels()
    }

    /**
     * 初始化 ViewModels
     */
    private fun initViewModels() {
        try {
            // 创建自定义 Factory，支持创建我们的 ViewModel
            val factory = viewModelFactory {
                initializer { CounterViewModel() }
                initializer { UserViewModel() }
            }

            // 使用 ViewModelProvider.create() 获取 Provider 实例
            val provider = ViewModelProvider.create(
                viewModelStoreOwner,
                factory,
                CreationExtras.Empty
            )

            counterViewModel = provider[CounterViewModel::class]
            userViewModel = provider[UserViewModel::class]

            viewModelStatus = "ViewModel 初始化成功！"
            appendLog("ViewModelProvider 创建成功")
            appendLog("CounterViewModel 获取成功")
            appendLog("UserViewModel 获取成功")

            // 同步初始状态
            syncUIState()

        } catch (e: Exception) {
            viewModelStatus = "ViewModel 初始化失败: ${e.message}"
            appendLog("错误: ${e.message}")
        }
    }

    /**
     * 同步 UI 状态
     */
    private fun syncUIState() {
        counterValue = counterViewModel.count
        userName = userViewModel.userName
        isLoggedIn = userViewModel.isLoggedIn
        loginTimeStr = if (userViewModel.isLoggedIn) {
            formatTime(userViewModel.loginTime)
        } else {
            "未登录"
        }
    }

    /**
     * 添加日志
     */
    private fun appendLog(message: String) {
        val timestamp = Clock.System.now().toEpochMilliseconds() % 100000
        logMessages = "[$timestamp] $message\n$logMessages"
    }

    /**
     * 格式化时间戳
     */
    private fun formatTime(timestamp: Long): String {
        return "时间戳: $timestamp"
    }

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
                ctx.createSectionTitle("androidx.lifecycle 测试", Color(0xFF6200EE)).invoke(this)

                // ViewModel 状态
                View {
                    attr {
                        margin(16f)
                        padding(16f)
                        backgroundColor(if (ctx.viewModelStatus.startsWith("✅")) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                        borderRadius(12f)
                    }
                    Text {
                        attr {
                            text(ctx.viewModelStatus)
                            fontSize(16f)
                            fontWeightBold()
                            color(if (ctx.viewModelStatus.startsWith("✅")) Color(0xFF2E7D32) else Color(0xFFC62828))
                        }
                    }
                }

                // 1. 计数器 ViewModel 测试
                ctx.createSectionTitle("CounterViewModel 测试", Color(0xFF03DAC5)).invoke(this)

                // 计数器显示
                View {
                    attr {
                        margin(16f)
                        padding(24f)
                        backgroundColor(Color(0xFFE3F2FD))
                        borderRadius(12f)
                        allCenter()
                    }
                    Text {
                        attr {
                            text("计数: ${ctx.counterValue}")
                            fontSize(32f)
                            fontWeightBold()
                            color(Color(0xFF1976D2))
                        }
                    }
                }

                // 计数器按钮
                View {
                    attr {
                        marginLeft(16f)
                        marginRight(16f)
                        flexDirectionRow()
                    }

                    // 减少按钮
                    View {
                        attr {
                            flex(1f)
                            margin(4f)
                            padding(16f)
                            backgroundColor(Color(0xFFFF5722))
                            borderRadius(8f)
                            allCenter()
                        }
                        event {
                            click {
                                ctx.counterViewModel.decrement()
                                ctx.counterValue = ctx.counterViewModel.count
                                ctx.appendLog("计数器减少 -> ${ctx.counterValue}")
                            }
                        }
                        Text {
                            attr {
                                text("➖ 减少")
                                fontSize(16f)
                                fontWeightBold()
                                color(Color.WHITE)
                            }
                        }
                    }

                    // 增加按钮
                    View {
                        attr {
                            flex(1f)
                            margin(4f)
                            padding(16f)
                            backgroundColor(Color(0xFF4CAF50))
                            borderRadius(8f)
                            allCenter()
                        }
                        event {
                            click {
                                ctx.counterViewModel.increment()
                                ctx.counterValue = ctx.counterViewModel.count
                                ctx.appendLog("计数器增加 -> ${ctx.counterValue}")
                            }
                        }
                        Text {
                            attr {
                                text("➕ 增加")
                                fontSize(16f)
                                fontWeightBold()
                                color(Color.WHITE)
                            }
                        }
                    }

                    // 重置按钮
                    View {
                        attr {
                            flex(1f)
                            margin(4f)
                            padding(16f)
                            backgroundColor(Color(0xFF9E9E9E))
                            borderRadius(8f)
                            allCenter()
                        }
                        event {
                            click {
                                ctx.counterViewModel.reset()
                                ctx.counterValue = ctx.counterViewModel.count
                                ctx.appendLog("计数器重置 -> ${ctx.counterValue}")
                            }
                        }
                        Text {
                            attr {
                                text("🔄 重置")
                                fontSize(16f)
                                fontWeightBold()
                                color(Color.WHITE)
                            }
                        }
                    }
                }

                // 2. 用户 ViewModel 测试
                ctx.createSectionTitle("UserViewModel 测试", Color(0xFF03DAC5)).invoke(this)

                // 用户信息卡片
                View {
                    attr {
                        margin(16f)
                        padding(16f)
                        backgroundColor(if (ctx.isLoggedIn) Color(0xFFE8F5E9) else Color(0xFFFFF9C4))
                        borderRadius(12f)
                    }

                    Text {
                        attr {
                            text("👤 用户名: ${ctx.userName}")
                            fontSize(18f)
                            fontWeightBold()
                            color(Color.BLACK)
                        }
                    }

                    Text {
                        attr {
                            text("📊 状态: ${if (ctx.isLoggedIn) "已登录 ✅" else "未登录 ❌"}")
                            fontSize(16f)
                            color(Color(0xFF666666))
                            marginTop(8f)
                        }
                    }

                    Text {
                        attr {
                            text("⏰ ${ctx.loginTimeStr}")
                            fontSize(14f)
                            color(Color(0xFF999999))
                            marginTop(4f)
                        }
                    }
                }

                // 登录/登出按钮
                View {
                    attr {
                        marginLeft(16f)
                        marginRight(16f)
                        flexDirectionRow()
                    }

                    // 登录按钮
                    View {
                        attr {
                            flex(1f)
                            margin(4f)
                            padding(16f)
                            backgroundColor(if (ctx.isLoggedIn) Color(0xFFBDBDBD) else Color(0xFF2196F3))
                            borderRadius(8f)
                            allCenter()
                        }
                        event {
                            click {
                                if (!ctx.isLoggedIn) {
                                    ctx.userViewModel.login("测试用户")
                                    ctx.syncUIState()
                                    ctx.appendLog("用户登录: ${ctx.userName}")
                                }
                            }
                        }
                        Text {
                            attr {
                                text("🔐 登录")
                                fontSize(16f)
                                fontWeightBold()
                                color(Color.WHITE)
                            }
                        }
                    }

                    // 登出按钮
                    View {
                        attr {
                            flex(1f)
                            margin(4f)
                            padding(16f)
                            backgroundColor(if (ctx.isLoggedIn) Color(0xFFF44336) else Color(0xFFBDBDBD))
                            borderRadius(8f)
                            allCenter()
                        }
                        event {
                            click {
                                if (ctx.isLoggedIn) {
                                    ctx.userViewModel.logout()
                                    ctx.syncUIState()
                                    ctx.appendLog("用户登出")
                                }
                            }
                        }
                        Text {
                            attr {
                                text("🚪 登出")
                                fontSize(16f)
                                fontWeightBold()
                                color(Color.WHITE)
                            }
                        }
                    }
                }

                // 3. ViewModelStore 测试
                ctx.createSectionTitle("ViewModelStore 测试", Color(0xFF03DAC5)).invoke(this)

                // 清理 ViewModelStore 按钮
                View {
                    attr {
                        margin(16f)
                        padding(16f)
                        backgroundColor(Color(0xFFE91E63))
                        borderRadius(12f)
                        allCenter()
                    }
                    event {
                        click {
                            ctx.appendLog("清理 ViewModelStore...")
                            ctx.viewModelStoreOwner.clear()
                            ctx.appendLog("ViewModelStore 已清理，onCleared() 应被调用")
                            ctx.viewModelStatus = "⚠️ ViewModelStore 已清理，需重新初始化"
                        }
                    }
                    Text {
                        attr {
                            text("🗑️ 清理 ViewModelStore")
                            fontSize(16f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                }

                // 重新初始化按钮
                View {
                    attr {
                        margin(16f)
                        marginTop(0f)
                        padding(16f)
                        backgroundColor(Color(0xFF673AB7))
                        borderRadius(12f)
                        allCenter()
                    }
                    event {
                        click {
                            ctx.appendLog("重新初始化 ViewModel...")
                            ctx.initViewModels()
                        }
                    }
                    Text {
                        attr {
                            text("🔄 重新初始化 ViewModel")
                            fontSize(16f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                }

                // 4. 运行日志
                ctx.createSectionTitle("运行日志", Color(0xFF03DAC5)).invoke(this)

                View {
                    attr {
                        margin(16f)
                        padding(12f)
                        backgroundColor(Color(0xFF263238))
                        borderRadius(8f)
                        minHeight(150f)
                    }
                    Text {
                        attr {
                            text(if (ctx.logMessages.isEmpty()) "暂无日志..." else ctx.logMessages)
                            fontSize(12f)
                            color(Color(0xFF4CAF50))
                            lineHeight(18f)
                        }
                    }
                }

                // API 说明
                ctx.createSectionTitle("测试的 API", Color(0xFF03DAC5)).invoke(this)
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
                                本页面测试了以下 androidx.lifecycle API：

                                ✅ ViewModel
                                   - 继承 ViewModel 类
                                   - 实现 onCleared() 生命周期回调

                                ✅ ViewModelStore
                                   - 存储和管理 ViewModel 实例
                                   - clear() 清理所有 ViewModel

                                ✅ ViewModelStoreOwner
                                   - 自定义 ViewModelStoreOwner 实现
                                   - 提供 viewModelStore 属性

                                ✅ ViewModelProvider
                                   - 通过 Provider 获取 ViewModel
                                   - 使用 NewInstanceFactory
                            """.trimIndent())
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

    /**
     * 创建章节标题
     */
    private fun createSectionTitle(title: String, bgColor: Color): ViewBuilder {
        return {
            View {
                attr {
                    margin(16f)
                    marginBottom(8f)
                    padding(12f)
                    backgroundColor(bgColor)
                    borderRadius(8f)
                }
                Text {
                    attr {
                        text(title)
                        fontSize(18f)
                        fontWeightBold()
                        color(Color.WHITE)
                    }
                }
            }
        }
    }
}
