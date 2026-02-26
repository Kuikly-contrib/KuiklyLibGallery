package com.example.kuiklylibgallery


import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.directives.vif
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Kotlin Serialization Demo
 * 演示如何使用 kotlinx.serialization 进行序列化和反序列化
 */
@Page("SerializationDemo")
internal class SerializationDemo : Pager() {

    // 可观察的状态变量
    private var result1 by observable("")
    private var result2 by observable("")
    private var result3 by observable("")
    private var result4 by observable("")

    // 数据模型
    @Serializable
    data class User(
        val id: Int,
        val name: String,
        val email: String,
        val age: Int
    )

    @Serializable
    data class Company(
        val name: String,
        val address: String,
        val employees: List<User>
    )

    // JSON 配置
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    override fun body(): ViewBuilder {
        val ctx = this
        val jsonFormatter = this.json

        return {
            attr {
                backgroundColor(Color(0xFFF5F5F5))
                flexDirectionColumn()
            }

            // 顶部标题栏
            View {
                attr {
                    height(60f)
                    backgroundColor(Color(0xFF6200EE))
                    allCenter()
                }
                Text {
                    attr {
                        text("Serialization Demo")
                        color(Color.WHITE)
                        fontSize(20f)
                        fontWeightBold()
                    }
                }
            }

            // 可滚动内容区域
            Scroller {
                attr {
                    flex(1f)
                    flexDirectionColumn()
                    padding(16f)
                }

                // 示例 1: 简单对象序列化
                View {
                    attr {
                        width(pagerData.pageViewWidth - 32f)
                        backgroundColor(Color.WHITE)
                        borderRadius(12f)
                        padding(16f)
                        marginBottom(16f)
                        flexDirectionColumn()
                    }

                    Text {
                        attr {
                            text("1. 简单对象序列化")
                            fontSize(18f)
                            fontWeightBold()
                            color(Color(0xFF212121))
                            marginBottom(8f)
                        }
                    }

                    Text {
                        attr {
                            text("将 User 对象序列化为 JSON 字符串")
                            fontSize(14f)
                            color(Color(0xFF757575))
                            marginBottom(12f)
                        }
                    }

                    Button {
                        attr {
                            size(pagerData.pageViewWidth - 64f, 44f)
                            borderRadius(8f)
                            marginBottom(12f)
                            backgroundColor(Color(0xFF6200EE))
                            titleAttr {
                                text("序列化 User")
                                color(Color.WHITE)
                                fontSize(14f)
                            }
                            highlightBackgroundColor(Color(0xFF3700B3))
                        }
                        event {
                            click {
                                try {
                                    val user = User(
                                        id = 1,
                                        name = "张三",
                                        email = "zhangsan@example.com",
                                        age = 25
                                    )
                                    ctx.result1 = jsonFormatter.encodeToString(user)
                                } catch (e: Exception) {
                                    ctx.result1 = "错误: ${e.message}"
                                }
                            }
                        }
                    }

                    vif({ctx.result1.isNotEmpty()}) {
                        View {
                            attr {
                                backgroundColor(Color(0xFFF5F5F5))
                                borderRadius(8f)
                                padding(12f)
                                marginTop(8f)
                            }
                            Text {
                                attr {
                                    text(ctx.result1)
                                    fontSize(12f)
                                    color(Color(0xFF424242))
                                }
                            }
                        }
                    }
                }

                // 示例 2: 复杂对象序列化
                View {
                    attr {
                        width(pagerData.pageViewWidth - 32f)
                        backgroundColor(Color.WHITE)
                        borderRadius(12f)
                        padding(16f)
                        marginBottom(16f)
                        flexDirectionColumn()
                    }

                    Text {
                        attr {
                            text("2. 复杂对象序列化")
                            fontSize(18f)
                            fontWeightBold()
                            color(Color(0xFF212121))
                            marginBottom(8f)
                        }
                    }

                    Text {
                        attr {
                            text("将包含列表的 Company 对象序列化")
                            fontSize(14f)
                            color(Color(0xFF757575))
                            marginBottom(12f)
                        }
                    }

                    Button {
                        attr {
                            size(pagerData.pageViewWidth - 64f, 44f)
                            borderRadius(8f)
                            marginBottom(12f)
                            backgroundColor(Color(0xFF6200EE))
                            titleAttr {
                                text("序列化 Company")
                                color(Color.WHITE)
                                fontSize(14f)
                            }
                            highlightBackgroundColor(Color(0xFF3700B3))
                        }
                        event {
                            click {
                                try {
                                    val company = Company(
                                        name = "科技公司",
                                        address = "深圳市南山区",
                                        employees = listOf(
                                            User(1, "张三", "zhangsan@example.com", 25),
                                            User(2, "李四", "lisi@example.com", 30),
                                            User(3, "王五", "wangwu@example.com", 28)
                                        )
                                    )
                                    ctx.result2 = jsonFormatter.encodeToString(company)
                                } catch (e: Exception) {
                                    ctx.result2 = "错误: ${e.message}"
                                }
                            }
                        }
                    }

                    vif({ctx.result2.isNotEmpty()}) {
                        View {
                            attr {
                                backgroundColor(Color(0xFFF5F5F5))
                                borderRadius(8f)
                                padding(12f)
                                marginTop(8f)
                            }
                            Text {
                                attr {
                                    text(ctx.result2)
                                    fontSize(12f)
                                    color(Color(0xFF424242))
                                }
                            }
                        }
                    }
                }

                // 示例 3: JSON 反序列化
                View {
                    attr {
                        width(pagerData.pageViewWidth - 32f)
                        backgroundColor(Color.WHITE)
                        borderRadius(12f)
                        padding(16f)
                        marginBottom(16f)
                        flexDirectionColumn()
                    }

                    Text {
                        attr {
                            text("3. JSON 反序列化")
                            fontSize(18f)
                            fontWeightBold()
                            color(Color(0xFF212121))
                            marginBottom(8f)
                        }
                    }

                    Text {
                        attr {
                            text("将 JSON 字符串反序列化为 User 对象")
                            fontSize(14f)
                            color(Color(0xFF757575))
                            marginBottom(12f)
                        }
                    }

                    Button {
                        attr {
                            size(pagerData.pageViewWidth - 64f, 44f)
                            borderRadius(8f)
                            marginBottom(12f)
                            backgroundColor(Color(0xFF6200EE))
                            titleAttr {
                                text("反序列化 JSON")
                                color(Color.WHITE)
                                fontSize(14f)
                            }
                            highlightBackgroundColor(Color(0xFF3700B3))
                        }
                        event {
                            click {
                                try {
                                    val jsonString = """
                                        {
                                            "id": 100,
                                            "name": "赵六",
                                            "email": "zhaoliu@example.com",
                                            "age": 35
                                        }
                                    """.trimIndent()

                                    val user = jsonFormatter.decodeFromString<User>(jsonString)
                                    ctx.result3 = "反序列化成功!\n\n" +
                                            "ID: ${user.id}\n" +
                                            "姓名: ${user.name}\n" +
                                            "邮箱: ${user.email}\n" +
                                            "年龄: ${user.age}"
                                } catch (e: Exception) {
                                    ctx.result3 = "错误: ${e.message}"
                                }
                            }
                        }
                    }

                    vif({ctx.result3.isNotEmpty()}) {
                        View {
                            attr {
                                backgroundColor(Color(0xFFF5F5F5))
                                borderRadius(8f)
                                padding(12f)
                                marginTop(8f)
                            }
                            Text {
                                attr {
                                    text(ctx.result3)
                                    fontSize(12f)
                                    color(Color(0xFF424242))
                                }
                            }
                        }
                    }
                }

                // 示例 4: 列表序列化
                View {
                    attr {
                        width(pagerData.pageViewWidth - 32f)
                        backgroundColor(Color.WHITE)
                        borderRadius(12f)
                        padding(16f)
                        marginBottom(16f)
                        flexDirectionColumn()
                    }

                    Text {
                        attr {
                            text("4. 列表序列化")
                            fontSize(18f)
                            fontWeightBold()
                            color(Color(0xFF212121))
                            marginBottom(8f)
                        }
                    }

                    Text {
                        attr {
                            text("将用户列表序列化为 JSON 数组")
                            fontSize(14f)
                            color(Color(0xFF757575))
                            marginBottom(12f)
                        }
                    }

                    Button {
                        attr {
                            size(pagerData.pageViewWidth - 64f, 44f)
                            borderRadius(8f)
                            marginBottom(12f)
                            backgroundColor(Color(0xFF6200EE))
                            titleAttr {
                                text("序列化列表")
                                color(Color.WHITE)
                                fontSize(14f)
                            }
                            highlightBackgroundColor(Color(0xFF3700B3))
                        }
                        event {
                            click {
                                try {
                                    val users = listOf(
                                        User(1, "用户A", "usera@example.com", 20),
                                        User(2, "用户B", "userb@example.com", 22),
                                        User(3, "用户C", "userc@example.com", 24)
                                    )
                                    ctx.result4 = jsonFormatter.encodeToString(users)
                                } catch (e: Exception) {
                                    ctx.result4 = "错误: ${e.message}"
                                }
                            }
                        }
                    }

                    vif({ctx.result4.isNotEmpty()}) {
                        View {
                            attr {
                                backgroundColor(Color(0xFFF5F5F5))
                                borderRadius(8f)
                                padding(12f)
                                marginTop(8f)
                            }
                            Text {
                                attr {
                                    text(ctx.result4)
                                    fontSize(12f)
                                    color(Color(0xFF424242))
                                }
                            }
                        }
                    }
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