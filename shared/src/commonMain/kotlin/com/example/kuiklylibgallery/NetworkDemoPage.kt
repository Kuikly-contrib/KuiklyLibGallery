package com.example.kuiklylibgallery

import com.example.kuiklylibgallery.base.BasePager
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kmm.network.service.VBTransportService
import com.tencent.kmm.network.export.VBTransportStringRequest
import com.tencent.kmm.network.export.VBTransportGetRequest
import com.tencent.kmm.network.export.VBTransportPostRequest
import com.tencent.kmm.network.export.VBTransportContentType
import com.tencent.kmm.network.export.VBTransportResultCode
import com.tencent.kuikly.core.log.KLog
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuiklyx.coroutines.Kuikly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume

private const val TAG = "KtorDemoPage"

/**
 * 使用 com.tencent.kuiklybase:network 库（VBTransportService）
 * 底层基于 Ktor (Android/iOS) / libcurl (HarmonyOS)
 * 
 * 与官方 demo 保持一致，回调中只在控制台打印日志，不更新 UI
 */
@Page("NetworkDemoPage")
internal class KtorDemoPage : BasePager() {
    
    val ctx = this
    var response by observable("等待请求结果...")


    override fun pageDidAppear() {
        // 页面加载后自动发起所有请求
        demonstrateSimpleGet()
        demonstrateStringGet()
        demonstratePost()
        demonstrateHeaders()
        demonstrateErrorHandling()
    }

    /**
     * 1. 简单 GET 请求
     */
    private fun demonstrateSimpleGet() {
        println("[$TAG] === 1. 简单 GET 请求 ===")

        val request = VBTransportGetRequest().apply {
            url = "https://httpbin.org/get"
            logTag = "SimpleGetDemo"
            header["Content-Type"] = VBTransportContentType.JSON.toString()
            useCurl = false
        }

        // 在 IO 线程执行请求，拿到结果后切到 Kuikly 线程更新 observable，触发 UI 刷新
        GlobalScope.launch(Dispatchers.Kuikly[ctx]) {
            val dataStr = suspendCancellableCoroutine<String> { continuation ->
                VBTransportService.sendGetRequest(request) { response ->
                    val result = if (response.errorCode == VBTransportResultCode.CODE_OK) {
                        response.data?.toString() ?: ""
                    } else {
                        "请求失败，错误码: ${response.errorCode}, 错误信息: ${response.errorMessage}"
                    }
                    if (continuation.isActive) {
                        continuation.resume(result)
                    }
                }
            }

            withContext(Dispatchers.Kuikly[ctx]) {
                response = dataStr
            }

            if (dataStr.startsWith("请求失败")) {
                KLog.i(TAG,"GET 请求失败: $dataStr")
            } else {
                KLog.i(TAG,"GET 请求成功！响应数据长度: ${dataStr.length} 字符")
                KLog.i(TAG,"响应内容 (前500字符): ${dataStr.take(500)}")
            }
        }
    }

    /**
     * 2. String GET 请求 + JSON 解析
     */
    private fun demonstrateStringGet() {
        println("[$TAG] === 2. String GET + JSON 解析 ===")

        val request = VBTransportStringRequest().apply {
            url = "https://jsonplaceholder.typicode.com/posts/1"
            logTag = "StringGetDemo"
            useCurl = false
        }

        VBTransportService.sendStringRequest(request) { response ->
            if (response.errorCode == VBTransportResultCode.CODE_OK) {
                try {
                    val json = Json { ignoreUnknownKeys = true }
                    val rawData = response.data
                    val post = json.decodeFromString<PostData>(rawData)
                    println("[$TAG] String GET + JSON 解析成功！")
                    println("[$TAG] 文章 ID: ${post.id}, 用户ID: ${post.userId}")
                    println("[$TAG] 标题: ${post.title}")
                    println("[$TAG] 内容: ${post.body.take(80)}...")
                } catch (e: Exception) {
                    println("[$TAG] JSON 解析失败: ${e.message}")
                    println("[$TAG] 原始数据: ${response.data.take(300)}")
                }
            } else {
                println("[$TAG] String GET 请求失败，错误码: ${response.errorCode}, 错误信息: ${response.errorMessage}")
            }
        }
    }

    /**
     * 3. POST 请求
     */
    private fun demonstratePost() {
        println("[$TAG] === 3. POST 请求 ===")

        val request = VBTransportPostRequest().apply {
            url = "https://httpbin.org/post"
            logTag = "PostDemo"
            header["Content-Type"] = VBTransportContentType.JSON.toString()
            data = """{"name": "Kuikly", "message": "Hello from VBTransportService!"}"""
            useCurl = false
        }

        VBTransportService.sendPostRequest(request) { response ->
            if (response.errorCode == VBTransportResultCode.CODE_OK) {
                var content = response.data
                var len = 0
                if (response.data is ByteArray) {
                    content = (response.data as ByteArray).decodeToString()
                    len = (response.data as ByteArray).size
                } else if (response.data is String) {
                    len = (response.data as String).length
                }
                val dataStr = content?.toString() ?: ""
                println("[$TAG] POST 请求成功！响应长度: $len")
                println("[$TAG] 响应内容 (前600字符): ${dataStr.take(600)}")
            } else {
                println("[$TAG] POST 请求失败，错误码: ${response.errorCode}, 错误信息: ${response.errorMessage}")
            }
        }
    }

    /**
     * 4. 自定义请求头
     */
    private fun demonstrateHeaders() {
        println("[$TAG] === 4. 自定义请求头 ===")

        val request = VBTransportStringRequest().apply {
            url = "https://httpbin.org/headers"
            logTag = "HeaderDemo"
            header["X-Custom-Header"] = "Kuikly-Demo"
            header["Accept-Language"] = "zh-CN"
            header["User-Agent"] = "KuiklyApp/1.0"
            useCurl = false
        }

        VBTransportService.sendStringRequest(request) { response ->
            if (response.errorCode == VBTransportResultCode.CODE_OK) {
                println("[$TAG] 自定义请求头测试成功！")
                println("[$TAG] 服务器收到的请求头: ${response.data.take(500)}")
            } else {
                println("[$TAG] 自定义请求头测试失败，错误码: ${response.errorCode}, 错误信息: ${response.errorMessage}")
            }
        }
    }

    /**
     * 5. 错误处理演示
     */
    private fun demonstrateErrorHandling() {
        println("[$TAG] === 5. 错误处理演示 (404) ===")

        val request = VBTransportGetRequest().apply {
            url = "https://httpbin.org/status/404"
            logTag = "ErrorHandlingDemo"
            header["Content-Type"] = VBTransportContentType.JSON.toString()
            useCurl = false
        }

        VBTransportService.sendGetRequest(request) { response ->
            if (response.errorCode == VBTransportResultCode.CODE_OK) {
                println("[$TAG] 404 请求完成（服务器返回了响应）")
                println("[$TAG] 响应数据: ${response.data?.toString()?.take(200) ?: "无数据"}")
                println("[$TAG] 服务器IP: ${response.serverIP}")
            } else {
                println("[$TAG] 请求返回错误")
                println("[$TAG] 错误码: ${response.errorCode}, 错误信息: ${response.errorMessage}")
            }
        }
    }

    override fun body(): ViewBuilder {
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
                            text("VBTransportService 网络库示例")
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
                                本页面演示 VBTransportService 网络库的用法。
                                
                                页面加载后自动发起以下请求，结果输出到控制台日志：
                                
                                1. 简单 GET 请求 (httpbin.org/get)
                                2. String GET + JSON 解析 (jsonplaceholder)
                                3. POST 请求 (httpbin.org/post)
                                4. 自定义请求头 (httpbin.org/headers)
                                5. 错误处理 - 404 (httpbin.org/status/404)
                                
                                请查看控制台输出 (TAG: KtorDemoPage)
                            """.trimIndent())
                            fontSize(14f)
                            color(Color(0xFF424242))
                            lineHeight(22f)
                        }
                    }
                }

                // 请求结果（observable 绑定）
                View {
                    attr {
                        margin(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFF4CAF50))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text("实时响应（来自 observable）")
                            fontSize(16f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                }
                View {
                    attr {
                        margin(16f)
                        padding(12f)
                        backgroundColor(Color(0xFFE8F5E9))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text(this@KtorDemoPage.response.take(600))
                            fontSize(13f)
                            color(Color(0xFF1B5E20))
                            lineHeight(20f)
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
                                VBTransportService 主要 API：
                                
                                GET 请求：
                                • sendGetRequest(VBTransportGetRequest, handler)
                                  返回 VBTransportGetResponse (data: Any?)
                                
                                String GET 请求：
                                • sendStringRequest(VBTransportStringRequest, handler)
                                  返回 VBTransportStringResponse (data: String)
                                
                                POST 请求：
                                • sendPostRequest(VBTransportPostRequest, handler)
                                  返回 VBTransportPostResponse (data: Any?)
                                
                                Bytes POST 请求：
                                • sendBytesRequest(VBTransportBytesRequest, handler)
                                  返回 VBTransportBytesResponse (data: ByteArray?)
                                
                                错误码：
                                • CODE_OK = 0 (成功)
                                • CODE_CANCELED = -10001 (已取消)
                                • CODE_FORCE_TIMEOUT = -2001 (超时)
                                
                                底层实现：
                                • Android/iOS: Ktor (useCurl = false)
                                • HarmonyOS: libcurl (useCurl = true)
                                
                                依赖: com.tencent.kuiklybase:network:0.0.4
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