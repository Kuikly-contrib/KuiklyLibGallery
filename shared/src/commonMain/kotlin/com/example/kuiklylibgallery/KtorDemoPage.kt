package com.example.kuiklylibgallery

import com.example.kuiklylibgallery.base.BasePager
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.module.NetworkModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Kuikly Network 网络请求示例页面
 * 展示 HTTP 请求的各种用法
 * 
 * 使用 Kuikly 内置的 NetworkModule
 * 回调会自动在正确的 Context 线程执行，避免线程断言失败
 */
@Page("KtorDemoPage")
internal class KtorDemoPage : BasePager() {

    // 响应式状态
    private var simpleGetResult by observable("点击按钮发起请求...")
    private var stringGetResult by observable("点击按钮发起请求...")
    private var postResult by observable("点击按钮发起请求...")
    private var headerResult by observable("点击按钮发起请求...")
    private var errorHandlingResult by observable("点击按钮发起请求...")
    private var timeoutResult by observable("点击按钮发起请求...")

    // 获取 NetworkModule 实例
    private val networkModule: NetworkModule
        get() = acquireModule(NetworkModule.MODULE_NAME)

    /**
     * 1. 简单 GET 请求
     * 使用 NetworkModule.requestGet
     */
    private fun demonstrateSimpleGet() {
        simpleGetResult = "请求中..."
        
        networkModule.requestGet(
            url = "https://httpbin.org/get",
            param = JSONObject()
        ) { data, success, errorMsg, response ->
            if (success) {
                val dataStr = data.toString()
                simpleGetResult = """
                    GET 请求成功！
                    
                    状态码: ${response.statusCode ?: "N/A"}
                    响应数据长度: ${dataStr.length} 字符
                    
                    响应内容 (前500字符):
                    ${dataStr.take(500)}...
                    
                    API 说明:
                    • 使用 NetworkModule.requestGet
                    • 回调自动在 Context 线程执行
                """.trimIndent()
            } else {
                simpleGetResult = """
                    请求失败
                    
                    错误信息: $errorMsg
                """.trimIndent()
            }
        }
    }

    /**
     * 2. String GET 请求 + JSON 解析
     * 使用 NetworkModule.requestGet
     */
    private fun demonstrateStringGet() {
        stringGetResult = "请求中..."
        
        networkModule.requestGet(
            url = "https://jsonplaceholder.typicode.com/posts/1",
            param = JSONObject()
        ) { data, success, errorMsg, response ->
            if (success) {
                // 解析 JSON
                try {
                    val json = Json { ignoreUnknownKeys = true }
                    // 从 JSONObject 中获取原始数据字符串
                    val rawData = if (data.has("data")) {
                        data.optString("data")
                    } else {
                        data.toString()
                    }
                    val post = json.decodeFromString<PostData>(rawData)
                    
                    stringGetResult = """
                        String GET + JSON 解析成功！
                        
                        获取到的文章:
                        • ID: ${post.id}
                        • 用户ID: ${post.userId}
                        • 标题: ${post.title}
                        • 内容: ${post.body.take(80)}...
                        
                        状态码: ${response.statusCode ?: "N/A"}
                        
                        API 说明:
                        • 使用 NetworkModule.requestGet
                        • 使用 kotlinx.serialization 解析 JSON
                    """.trimIndent()
                } catch (e: Exception) {
                    stringGetResult = """
                        请求成功，但 JSON 解析失败
                        
                        原始数据: ${data.toString().take(300)}...
                        解析错误: ${e.message}
                    """.trimIndent()
                }
            } else {
                stringGetResult = """
                    请求失败
                    
                    错误信息: $errorMsg
                """.trimIndent()
            }
        }
    }

    /**
     * 3. POST 请求
     * 使用 NetworkModule.httpRequest
     */
    private fun demonstratePost() {
        postResult = "请求中..."
        
        val postData = JSONObject().apply {
            put("name", "Kuikly")
            put("message", "Hello from Kuikly NetworkModule!")
        }
        
        val headers = JSONObject().apply {
            put("Content-Type", "application/json")
        }
        
        networkModule.httpRequest(
            url = "https://httpbin.org/post",
            isPost = true,
            param = postData,
            headers = headers,
            timeout = 30
        ) { data, success, errorMsg, response ->
            if (success) {
                val dataStr = data.toString()
                postResult = """
                    POST 请求成功！
                    
                    状态码: ${response.statusCode ?: "N/A"}
                    
                    响应内容 (前600字符):
                    ${dataStr.take(600)}...
                    
                    API 说明:
                    • 使用 NetworkModule.httpRequest
                    • isPost = true 发送 POST 请求
                    • headers 设置请求头
                """.trimIndent()
            } else {
                postResult = """
                    请求失败
                    
                    错误信息: $errorMsg
                """.trimIndent()
            }
        }
    }

    /**
     * 4. 自定义请求头
     */
    private fun demonstrateHeaders() {
        headerResult = "请求中..."
        
        val headers = JSONObject().apply {
            put("X-Custom-Header", "Kuikly-Demo")
            put("Accept-Language", "zh-CN")
            put("User-Agent", "KuiklyApp/1.0")
        }
        
        networkModule.httpRequest(
            url = "https://httpbin.org/headers",
            isPost = false,
            param = JSONObject(),
            headers = headers,
            timeout = 30
        ) { data, success, errorMsg, response ->
            if (success) {
                val dataStr = data.toString()
                headerResult = """
                    success！
                    
                    状态码: ${response.statusCode ?: "N/A"}
                    响应头字段数: ${response.headerFields.length()}
                    
                    服务器收到的请求头:
                    ${dataStr.take(500)}...
                    
                    API 说明:
                    • 使用 NetworkModule.httpRequest
                    • headers 参数添加自定义请求头
                """.trimIndent()
            } else {
                headerResult = """
                    请求失败
                    
                    错误信息: $errorMsg
                """.trimIndent()
            }
        }
    }

    /**
     * 5. 错误处理演示
     */
    private fun demonstrateErrorHandling() {
        errorHandlingResult = "请求中..."
        
        networkModule.requestGet(
            url = "https://httpbin.org/status/404",
            param = JSONObject()
        ) { data, success, errorMsg, response ->
            errorHandlingResult = if (success) {
                """
                    请求完成（服务器可能返回了错误状态）
                    
                    状态码: ${response.statusCode ?: "N/A"}
                    响应数据: ${data.toString().take(200)}
                    
                    注意：HTTP 状态码错误需要在响应中检查
                """.trimIndent()
            } else {
                """
                    请求返回错误
                    
                    状态码: ${response.statusCode ?: "N/A"}
                    错误信息: $errorMsg
                    
                    错误处理说明:
                    • success = false 表示请求失败
                    • errorMsg 包含错误描述
                    • response.statusCode 包含 HTTP 状态码
                """.trimIndent()
            }
        }
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
                        backgroundColor(Color(0xFF6200EE))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text("Kuikly Network 网络库示例")
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
                        backgroundColor(Color(0xFFF3E5F5))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text("""
                                本页面演示 Kuikly 内置 NetworkModule 的用法：
                                
                                模块: NetworkModule (内置)
                                
                                核心优势:
                                • 回调自动在 Context 线程执行
                                • 避免线程断言失败 (assertContextQueue)
                                • 无需额外初始化
                                
                                支持的方法:
                                • requestGet - GET 请求
                                • requestPost - POST 请求
                                • httpRequest - 通用请求
                                
                                点击下方按钮测试各个功能
                            """.trimIndent())
                            fontSize(14f)
                            color(Color(0xFF424242))
                            lineHeight(22f)
                        }
                    }
                }

                // 1. 简单 GET 请求
                View {
                    attr {
                        margin(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFF03DAC5))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text("1. 简单 GET 请求")
                            fontSize(18f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                }
                View {
                    attr {
                        marginLeft(16f)
                        marginRight(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFFE0F7FA))
                        borderRadius(8f)
                    }
                    // 点击按钮
                    View {
                        attr {
                            padding(10f)
                            backgroundColor(Color(0xFF6200EE))
                            borderRadius(6f)
                            marginBottom(12f)
                            allCenter()
                        }
                        event {
                            click {
                                ctx.demonstrateSimpleGet()
                            }
                        }
                        Text {
                            attr {
                                text("点击发起 GET 请求")
                                fontSize(14f)
                                fontWeightBold()
                                color(Color.WHITE)
                            }
                        }
                    }
                    // 结果展示
                    View {
                        attr {
                            padding(8f)
                            backgroundColor(Color(0xFFF5F5F5))
                            borderRadius(4f)
                        }
                        Text {
                            attr {
                                text(ctx.simpleGetResult)
                                fontSize(13f)
                                color(Color(0xFF212121))
                                lineHeight(20f)
                            }
                        }
                    }
                }

                // 2. String GET + JSON 解析
                View {
                    attr {
                        margin(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFF03DAC5))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text("2. String GET + JSON 解析")
                            fontSize(18f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                }
                View {
                    attr {
                        marginLeft(16f)
                        marginRight(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFFE8F5E9))
                        borderRadius(8f)
                    }
                    View {
                        attr {
                            padding(10f)
                            backgroundColor(Color(0xFF6200EE))
                            borderRadius(6f)
                            marginBottom(12f)
                            allCenter()
                        }
                        event {
                            click {
                                ctx.demonstrateStringGet()
                            }
                        }
                        Text {
                            attr {
                                text("点击获取并解析 JSON")
                                fontSize(14f)
                                fontWeightBold()
                                color(Color.WHITE)
                            }
                        }
                    }
                    View {
                        attr {
                            padding(8f)
                            backgroundColor(Color(0xFFF5F5F5))
                            borderRadius(4f)
                        }
                        Text {
                            attr {
                                text(ctx.stringGetResult)
                                fontSize(13f)
                                color(Color(0xFF212121))
                                lineHeight(20f)
                            }
                        }
                    }
                }

                // 3. POST 请求
                View {
                    attr {
                        margin(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFF03DAC5))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text("3. POST 请求")
                            fontSize(18f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                }
                View {
                    attr {
                        marginLeft(16f)
                        marginRight(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFFFFF3E0))
                        borderRadius(8f)
                    }
                    View {
                        attr {
                            padding(10f)
                            backgroundColor(Color(0xFF6200EE))
                            borderRadius(6f)
                            marginBottom(12f)
                            allCenter()
                        }
                        event {
                            click {
                                ctx.demonstratePost()
                            }
                        }
                        Text {
                            attr {
                                text("点击发起 POST 请求")
                                fontSize(14f)
                                fontWeightBold()
                                color(Color.WHITE)
                            }
                        }
                    }
                    View {
                        attr {
                            padding(8f)
                            backgroundColor(Color(0xFFF5F5F5))
                            borderRadius(4f)
                        }
                        Text {
                            attr {
                                text(ctx.postResult)
                                fontSize(13f)
                                color(Color(0xFF212121))
                                lineHeight(20f)
                            }
                        }
                    }
                }

                // 4. 自定义请求头
                View {
                    attr {
                        margin(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFF03DAC5))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text("4. 自定义请求头")
                            fontSize(18f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                }
                View {
                    attr {
                        marginLeft(16f)
                        marginRight(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFFE1F5FE))
                        borderRadius(8f)
                    }
                    View {
                        attr {
                            padding(10f)
                            backgroundColor(Color(0xFF6200EE))
                            borderRadius(6f)
                            marginBottom(12f)
                            allCenter()
                        }
                        event {
                            click {
                                ctx.demonstrateHeaders()
                            }
                        }
                        Text {
                            attr {
                                text("点击测试自定义 Headers")
                                fontSize(14f)
                                fontWeightBold()
                                color(Color.WHITE)
                            }
                        }
                    }
                    View {
                        attr {
                            padding(8f)
                            backgroundColor(Color(0xFFF5F5F5))
                            borderRadius(4f)
                        }
                        Text {
                            attr {
                                text(ctx.headerResult)
                                fontSize(13f)
                                color(Color(0xFF212121))
                                lineHeight(20f)
                            }
                        }
                    }
                }

                // 5. 错误处理
                View {
                    attr {
                        margin(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFF03DAC5))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text("5. 错误处理")
                            fontSize(18f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                }
                View {
                    attr {
                        marginLeft(16f)
                        marginRight(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFFFCE4EC))
                        borderRadius(8f)
                    }
                    View {
                        attr {
                            padding(10f)
                            backgroundColor(Color(0xFF6200EE))
                            borderRadius(6f)
                            marginBottom(12f)
                            allCenter()
                        }
                        event {
                            click {
                                ctx.demonstrateErrorHandling()
                            }
                        }
                        Text {
                            attr {
                                text("点击测试 404 错误")
                                fontSize(14f)
                                fontWeightBold()
                                color(Color.WHITE)
                            }
                        }
                    }
                    View {
                        attr {
                            padding(8f)
                            backgroundColor(Color(0xFFF5F5F5))
                            borderRadius(4f)
                        }
                        Text {
                            attr {
                                text(ctx.errorHandlingResult)
                                fontSize(13f)
                                color(Color(0xFF212121))
                                lineHeight(20f)
                            }
                        }
                    }
                }

                View {
                    attr {
                        marginLeft(16f)
                        marginRight(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFFF3E5F5))
                        borderRadius(8f)
                    }

                    View {
                        attr {
                            padding(8f)
                            backgroundColor(Color(0xFFF5F5F5))
                            borderRadius(4f)
                        }
                        Text {
                            attr {
                                text(ctx.timeoutResult)
                                fontSize(13f)
                                color(Color(0xFF212121))
                                lineHeight(20f)
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
                        backgroundColor(Color(0xFF03DAC5))
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
                                NetworkModule 主要 API：
                                
                                GET 请求：
                                • requestGet(url, param, callback)
                                
                                POST 请求：
                                • requestPost(url, param, callback)
                                
                                通用请求：
                                • httpRequest(url, isPost, param, 
                                   headers, cookie, timeout, callback)
                                
                                回调参数：
                                • data - JSONObject 响应数据
                                • success - Boolean 请求是否成功
                                • errorMsg - String 错误信息
                                • response.statusCode - HTTP 状态码
                                • response.headerFields - 响应头
                                
                                二进制请求：
                                • requestGetBinary(url, param, callback)
                                • requestPostBinary(url, bytes, callback)
                                
                                注意：回调自动在 Context 线程执行，
                                可安全更新 observable 状态
                            """.trimIndent())
                            fontSize(13f)
                            color(Color(0xFF424242))
                            lineHeight(20f)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 文章数据类，用于演示 JSON 解析
 */
@Serializable
data class PostData(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)