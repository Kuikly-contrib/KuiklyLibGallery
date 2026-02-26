package com.example.kuiklylibgallery

import com.example.kuiklylibgallery.base.BasePager
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.coroutines.async
import com.tencent.kuikly.core.coroutines.delay
import com.tencent.kuikly.core.coroutines.launch
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import kotlinx.datetime.Clock
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Kotlin Coroutines 协程使用示例页面
 * 
 * 演示两种 Kuikly 协程方式：
 * 1. Kuikly 内建协程 (lifecycleScope) - 最简单，始终在 Kuikly 线程
 * 2. suspend 挂起函数 - 使用 suspendCoroutine 包装回调
 */
@Page("CoroutineDemoPage")
internal class CoroutineDemoPage : BasePager() {

    // 响应式状态
    private var builtinCoroutineResult by observable("等待测试...")
    private var suspendFunctionResult by observable("等待测试...")
    private var currentStep by observable("页面初始化")
    private var debugInfo by observable("页面正常加载")

    override fun created() {
        super.created()
        println("CoroutineDemoPage: created() 被调用")
        debugInfo = "页面创建成功"
        currentStep = "页面已创建"
    }

    override fun pageDidAppear() {
        super.pageDidAppear()
        println("CoroutineDemoPage: pageDidAppear() 被调用")
        
        debugInfo = "页面已显示，准备就绪"
        currentStep = "页面已显示"
        
        // 初始化显示内容
        builtinCoroutineResult = """
            📱 Kuikly 内建协程
            
            • 使用 lifecycleScope.launch 启动
            • 始终在 Kuikly 线程执行
            • 无需担心线程安全问题
            
            点击按钮开始测试 →
        """.trimIndent()
        
        suspendFunctionResult = """
            ⏸️ suspend 挂起函数
            
            • 使用 suspendCoroutine 包装回调
            • 将回调式 API 转换为挂起函数
            • 简化异步代码结构
            
            点击按钮开始测试 →
        """.trimIndent()
    }

    /**
     * 方式1: Kuikly 内建协程测试
     * 使用 lifecycleScope.launch - 最简单的方式
     */
    private fun testBuiltinCoroutine() {
        lifecycleScope.launch {
            try {
                builtinCoroutineResult = "🚀 内建协程测试开始..."
                val startTime = currentTimeMillis()
                
                // 使用 Kuikly 内建 delay
                delay(500)
                
                // 并发执行两个任务
                val task1 = lifecycleScope.async { 
                    delay(300)
                    "任务A完成"
                }
                val task2 = lifecycleScope.async { 
                    delay(300)
                    "任务B完成"
                }
                
                val result1 = task1.await()
                val result2 = task2.await()
                
                val totalTime = currentTimeMillis() - startTime
                
                builtinCoroutineResult = """
                    ✅ 内建协程测试成功！
                    
                    执行结果:
                    • $result1
                    • $result2
                    
                    总耗时: ${totalTime}ms
                    (并发执行，约 800ms)
                    
                    特点:
                    • lifecycleScope 自动绑定生命周期
                    • 页面销毁时自动取消
                    • 始终在 Kuikly 线程执行
                """.trimIndent()
                
                currentStep = "内建协程测试完成 ✅"
                
            } catch (e: Exception) {
                builtinCoroutineResult = "❌ 测试失败: ${e.message}"
                println("CoroutineDemoPage: 内建协程测试失败: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * 方式2: suspend 挂起函数测试
     * 使用 suspendCoroutine 包装回调式 API
     */
    private fun testSuspendFunction() {
        lifecycleScope.launch {
            try {
                suspendFunctionResult = "⏸️ suspend 挂起函数测试开始..."
                val startTime = currentTimeMillis()
                
                // 调用挂起函数
                val result1 = fetchDataAsync("参数1")
                val result2 = fetchDataAsync("参数2")
                
                val totalTime = currentTimeMillis() - startTime
                
                suspendFunctionResult = """
                    ✅ suspend 挂起函数测试成功！
                    
                    结果1: $result1
                    结果2: $result2
                    
                    总耗时: ${totalTime}ms
                    (顺序执行，约 600ms)
                    
                    特点:
                    • 使用 suspendCoroutine 包装回调
                    • 将回调式 API 转换为挂起函数
                    • 代码结构更清晰
                    • 避免回调地狱
                """.trimIndent()
                
                currentStep = "suspend 挂起函数测试完成 ✅"
                
            } catch (e: Exception) {
                suspendFunctionResult = "❌ 测试失败: ${e.message}"
                println("CoroutineDemoPage: suspend 测试失败: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * 模拟异步获取数据的挂起函数
     * 使用 suspendCoroutine 将回调式 API 转换为挂起函数
     */
    private suspend fun fetchDataAsync(param: String): String {
        return suspendCoroutine { continuation ->
            // 模拟异步操作（使用 setTimeout 或其他异步机制）
            lifecycleScope.launch {
                delay(300) // 模拟网络延迟
                continuation.resume("获取到数据: $param -> ${currentTimeMillis()}")
            }
        }
    }

    /**
     * 获取当前时间戳（毫秒）
     */
    private fun currentTimeMillis(): Long {
        return Clock.System.now().toEpochMilliseconds()
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
                View {
                    attr {
                        margin(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFFFF6B6B))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text("🔧 Kuikly 协程组件示例")
                            fontSize(18f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                }

                // ==================== 1. 内建协程 ====================
                View {
                    attr {
                        margin(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFF4CAF50))
                        borderRadius(8f)
                    }
                    event {
                        click { ctx.testBuiltinCoroutine() }
                    }
                    Text {
                        attr {
                            text("1️⃣ 测试内建协程 (lifecycleScope)")
                            fontSize(15f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                }
                
                View {
                    attr {
                        marginLeft(16f)
                        marginRight(16f)
                        marginBottom(16f)
                        padding(12f)
                        backgroundColor(Color(0xFFE8F5E9))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text(ctx.builtinCoroutineResult)
                            fontSize(12f)
                            color(Color(0xFF2E7D32))
                            lineHeight(18f)
                        }
                    }
                }

                // ==================== 2. suspend 挂起函数 ====================
                View {
                    attr {
                        margin(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFF9C27B0))
                        borderRadius(8f)
                    }
                    event {
                        click { ctx.testSuspendFunction() }
                    }
                    Text {
                        attr {
                            text("2️⃣ 测试 suspend 挂起函数")
                            fontSize(15f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                }
                
                View {
                    attr {
                        marginLeft(16f)
                        marginRight(16f)
                        marginBottom(16f)
                        padding(12f)
                        backgroundColor(Color(0xFFF3E5F5))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text(ctx.suspendFunctionResult)
                            fontSize(12f)
                            color(Color(0xFF6A1B9A))
                            lineHeight(18f)
                        }
                    }
                }

                // 调试信息
                View {
                    attr {
                        margin(16f)
                        padding(12f)
                        backgroundColor(Color(0xFFECEFF1))
                        borderRadius(8f)
                    }
                    
                    Text {
                        attr {
                            text("🔍 调试信息")
                            fontSize(14f)
                            fontWeightBold()
                            color(Color(0xFF455A64))
                            marginBottom(8f)
                        }
                    }
                    
                    Text {
                        attr {
                            text("状态: " + ctx.currentStep)
                            fontSize(12f)
                            color(Color(0xFF607D8B))
                            marginBottom(4f)
                        }
                    }
                    
                    Text {
                        attr {
                            text("信息: " + ctx.debugInfo)
                            fontSize(12f)
                            color(Color(0xFF607D8B))
                        }
                    }
                }
            }
        }
    }
}
