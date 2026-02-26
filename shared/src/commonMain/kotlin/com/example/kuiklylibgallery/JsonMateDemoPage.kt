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
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.nvi.serialization.json.JSONArray
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.mtt.gradle.jsonmate.runtime.annotation.FromJSONObject
import com.tencent.mtt.gradle.jsonmate.runtime.annotation.JSONField
import kotlinx.serialization.SerialName

/**
 * JsonMate 功能演示页面
 *
 * JsonMate 是基于 KSP 的插件，能够为标记了 @FromJSONObject 的类
 * 自动生成从 Kuikly 的 JSONObject 反序列化的代码
 */
@Page("JsonMateDemoPage")
internal class JsonMateDemoPage : BasePager() {

    // 测试结果状态
    private var basicResult by observable("点击按钮测试基础反序列化...")
    private var nestedResult by observable("点击按钮测试嵌套对象反序列化...")
    private var listResult by observable("点击按钮测试列表反序列化...")
    private var defaultValueResult by observable("点击按钮测试默认值功能...")

    /**
     * 1. 基础反序列化测试
     */
    private fun testBasicDeserialization() {
        try {
            val jsonObject = JSONObject().apply {
                put("name", "张三")
                put("user_id", "12345")
                put("email", "zhangsan@example.com")
                put("age", 25)
            }

            val user = User.fromJSONObject(jsonObject)

            basicResult = """
                ✅ 基础反序列化成功！

                原始 JSON:
                {
                  "name": "张三",
                  "user_id": "12345",
                  "email": "zhangsan@example.com",
                  "age": 25
                }

                解析结果:
                • 姓名: ${user.name}
                • 用户ID: ${user.userId}
                • 邮箱: ${user.email}
                • 年龄: ${user.age}

                说明:
                • @FromJSONObject 注解标记类
                • @SerialName 用于字段映射
                • 自动生成 fromJSONObject 方法
            """.trimIndent()
        } catch (e: Exception) {
            basicResult = "❌ 测试失败: ${e.message}"
        }
    }

    /**
     * 2. 嵌套对象反序列化测试
     */
    private fun testNestedDeserialization() {
        try {
            val addressJson = JSONObject().apply {
                put("city", "北京")
                put("street", "中关村大街1号")
                put("zip_code", "100080")
            }

            val jsonObject = JSONObject().apply {
                put("name", "李四")
                put("user_id", "67890")
                put("age", 30)
                put("address", addressJson)
            }

            val userWithAddress = UserWithAddress.fromJSONObject(jsonObject)

            nestedResult = """
                ✅ 嵌套对象反序列化成功！

                原始 JSON:
                {
                  "name": "李四",
                  "user_id": "67890",
                  "age": 30,
                  "address": {
                    "city": "北京",
                    "street": "中关村大街1号",
                    "zip_code": "100080"
                  }
                }

                解析结果:
                • 姓名: ${userWithAddress.name}
                • 用户ID: ${userWithAddress.userId}
                • 年龄: ${userWithAddress.age}
                • 地址:
                  - 城市: ${userWithAddress.address?.city}
                  - 街道: ${userWithAddress.address?.street}
                  - 邮编: ${userWithAddress.address?.zipCode}

                说明:
                • 支持嵌套对象自动解析
                • 嵌套类也需要 @FromJSONObject 注解
            """.trimIndent()
        } catch (e: Exception) {
            nestedResult = "❌ 测试失败: ${e.message}"
        }
    }

    /**
     * 3. 列表反序列化测试
     */
    private fun testListDeserialization() {
        try {
            val tagsArray = JSONArray().apply {
                put("Kotlin")
                put("KMM")
                put("Kuikly")
            }

            val jsonObject = JSONObject().apply {
                put("title", "JsonMate 使用指南")
                put("author", "开发者")
                put("tags", tagsArray)
                put("view_count", 1000)
            }

            val article = Article.fromJSONObject(jsonObject)

            listResult = """
                ✅ 列表反序列化成功！

                原始 JSON:
                {
                  "title": "JsonMate 使用指南",
                  "author": "开发者",
                  "tags": ["Kotlin", "KMM", "Kuikly"],
                  "view_count": 1000
                }

                解析结果:
                • 标题: ${article.title}
                • 作者: ${article.author}
                • 标签: ${article.tags.joinToString(", ")}
                • 阅读数: ${article.viewCount}

                说明:
                • 支持 List<String> 类型
                • 支持 List<Int> 等基础类型列表
            """.trimIndent()
        } catch (e: Exception) {
            listResult = "❌ 测试失败: ${e.message}"
        }
    }

    /**
     * 4. 默认值功能测试
     */
    private fun testDefaultValue() {
        try {
            // 创建一个缺少部分字段的 JSON
            val jsonObject = JSONObject().apply {
                put("name", "王五")
                // 故意不设置 age，测试默认值
                // 故意不设置 email，测试可空类型
            }

            val user = User.fromJSONObject(jsonObject)

            defaultValueResult = """
                ✅ 默认值功能测试成功！

                原始 JSON (缺少 age 和 email):
                {
                  "name": "王五"
                }

                解析结果:
                • 姓名: ${user.name}
                • 年龄: ${user.age} (使用 @JSONField 默认值)
                • 邮箱: ${user.email ?: "null"} (可空类型)
                • 用户ID: ${user.userId}

                说明:
                • @JSONField(defaultValue = "18") 指定默认值
                • 可空类型字段缺失时为 null
                • 必填字段缺失时使用默认值
            """.trimIndent()
        } catch (e: Exception) {
            defaultValueResult = "❌ 测试失败: ${e.message}"
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
                            text("JsonMate 功能演示")
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
                                JsonMate 是基于 KSP 的代码生成插件：

                                • 自动生成 fromJSONObject 方法
                                • 支持 @FromJSONObject 注解
                                • 支持 @JSONField 指定默认值
                                • 支持 @SerialName 字段映射
                                • 支持嵌套对象和列表

                                点击下方按钮测试各项功能
                            """.trimIndent())
                            fontSize(14f)
                            color(Color(0xFF424242))
                            lineHeight(22f)
                        }
                    }
                }

                // 1. 基础反序列化测试
                ctx.createTestSection(
                    this,
                    title = "1. 基础反序列化",
                    buttonText = "测试基础 JSON 解析",
                    resultProvider = { ctx.basicResult },
                    backgroundColor = Color(0xFFE8F5E9),
                    buttonWidth = ctx.pagerData.pageViewWidth
                ) {
                    ctx.testBasicDeserialization()
                }

                // 2. 嵌套对象测试
                ctx.createTestSection(
                    this,
                    title = "2. 嵌套对象反序列化",
                    buttonText = "测试嵌套对象解析",
                    resultProvider = { ctx.nestedResult },
                    backgroundColor = Color(0xFFE3F2FD),
                    buttonWidth = ctx.pagerData.pageViewWidth
                ) {
                    ctx.testNestedDeserialization()
                }

                // 3. 列表测试
                ctx.createTestSection(
                    this,
                    title = "3. 列表反序列化",
                    buttonText = "测试列表解析",
                    resultProvider = { ctx.listResult },
                    backgroundColor = Color(0xFFFFF3E0),
                    buttonWidth = ctx.pagerData.pageViewWidth
                ) {
                    ctx.testListDeserialization()
                }

                // 4. 默认值测试
                ctx.createTestSection(
                    this,
                    title = "4. 默认值功能",
                    buttonText = "测试默认值",
                    resultProvider = { ctx.defaultValueResult },
                    backgroundColor = Color(0xFFFCE4EC),
                    buttonWidth = ctx.pagerData.pageViewWidth
                ) {
                    ctx.testDefaultValue()
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
                                注解说明：

                                @FromJSONObject
                                • 标记需要生成反序列化代码的类
                                • 自动生成 Companion.fromJSONObject()

                                @JSONField(defaultValue = "...")
                                • 指定字段的默认值
                                • 当 JSON 中缺少该字段时使用

                                @SerialName("json_key")
                                • 指定 JSON 中的字段名
                                • 用于字段名映射

                                使用示例：
                                @FromJSONObject
                                data class User(
                                    val name: String,
                                    @JSONField(defaultValue = "18")
                                    val age: Int,
                                    @SerialName("user_id")
                                    val userId: String
                                )

                                // 使用生成的方法
                                val user = User.fromJSONObject(json)
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

    private fun createTestSection(
        container: com.tencent.kuikly.core.base.ViewContainer<*, *>,
        title: String,
        buttonText: String,
        resultProvider: () -> String,
        backgroundColor: Color,
        buttonWidth: Float,
        onClick: () -> Unit
    ) {
        with(container) {
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
                        text(title)
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
                    backgroundColor(backgroundColor)
                    borderRadius(8f)
                }
                // 点击按钮
                Button {
                    attr {
                        size(buttonWidth - 56f, 44f)  // 56f = marginLeft(16) + marginRight(16) + padding(12)*2
                        borderRadius(8f)
                        marginBottom(12f)
                        backgroundColor(Color(0xFF6200EE))
                        highlightBackgroundColor(Color(0xFF3700B3))
                        titleAttr {
                            text(buttonText)
                            color(Color.WHITE)
                            fontSize(14f)
                        }
                    }
                    event {
                        click {
                            onClick()
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
                            text(resultProvider())
                            fontSize(13f)
                            color(Color(0xFF212121))
                            lineHeight(20f)
                        }
                    }
                }
            }
        }
    }
}

// ========== 数据类定义 ==========

/**
 * 用户数据类 - 演示基础反序列化
 */
@FromJSONObject
data class User(
    val name: String,
    @JSONField(defaultValue = "18")
    val age: Int,
    @SerialName("user_id")
    val userId: String = "",
    val email: String? = null
) {
    companion object
}

/**
 * 地址数据类 - 演示嵌套对象
 */
@FromJSONObject
data class Address(
    val city: String,
    val street: String,
    @SerialName("zip_code")
    val zipCode: String
) {
    companion object
}

/**
 * 带地址的用户 - 演示嵌套对象反序列化
 */
@FromJSONObject
data class UserWithAddress(
    val name: String,
    @SerialName("user_id")
    val userId: String,
    @JSONField(defaultValue = "0")
    val age: Int,
    val address: Address? = null
) {
    companion object
}

/**
 * 文章数据类 - 演示列表反序列化
 */
@FromJSONObject
data class Article(
    val title: String,
    val author: String,
    val tags: List<String> = emptyList(),
    @SerialName("view_count")
    @JSONField(defaultValue = "0")
    val viewCount: Int
) {
    companion object
}
