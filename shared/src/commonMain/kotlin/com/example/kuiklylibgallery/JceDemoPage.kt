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
import com.tencent.kuikly.jce.JceInputStream
import com.tencent.kuikly.jce.JceOutputStream
import com.tencent.kuikly.jce.JceStruct

/**
 * JCE 序列化功能演示页面
 *
 * JCE (Jce Codec Engine) 是腾讯开发的一种高效二进制序列化协议
 * 类似于 Protocol Buffers，具有紧凑、高效的特点
 * 广泛应用于腾讯内部的 RPC 通信和数据存储
 */
@Page("JceDemoPage")
internal class JceDemoPage : BasePager() {

    // 测试结果状态
    private var basicResult by observable("点击按钮测试基础类型序列化...")
    private var structResult by observable("点击按钮测试结构体序列化...")
    private var nestedResult by observable("点击按钮测试嵌套结构体序列化...")
    private var listResult by observable("点击按钮测试列表序列化...")
    private var mapResult by observable("点击按钮测试 Map 序列化...")

    /**
     * 1. 基础类型序列化测试
     */
    private fun testBasicSerialization() {
        try {
            // 序列化
            val os = JceOutputStream()
            os.write(42.toByte(), 0)      // tag=0: Byte
            os.write(12345.toShort(), 1)  // tag=1: Short
            os.write(123456789, 2)        // tag=2: Int
            os.write(9876543210L, 3)      // tag=3: Long
            os.write(3.14f, 4)            // tag=4: Float
            os.write(2.718281828, 5)      // tag=5: Double
            os.write("Hello, JCE!", 6)    // tag=6: String
            os.write(true, 7)             // tag=7: Boolean

            val bytes = os.toByteArray()

            // 反序列化
            val input = JceInputStream(bytes)
            val byteVal = input.read(0.toByte(), 0, true)
            val shortVal = input.read(0.toShort(), 1, true)
            val intVal = input.read(0, 2, true)
            val longVal = input.read(0L, 3, true)
            val floatVal = input.read(0f, 4, true)
            val doubleVal = input.read(0.0, 5, true)
            val stringVal = input.readString(6, true)
            val boolVal = input.read(false, 7, true)

            basicResult = """
                ✅ 基础类型序列化成功！

                序列化数据大小: ${bytes.size} 字节
                二进制数据(Hex): ${bytes.toHexString()}

                反序列化结果:
                • Byte (tag=0): $byteVal
                • Short (tag=1): $shortVal
                • Int (tag=2): $intVal
                • Long (tag=3): $longVal
                • Float (tag=4): $floatVal
                • Double (tag=5): $doubleVal
                • String (tag=6): $stringVal
                • Boolean (tag=7): $boolVal

                说明:
                • JCE 使用 tag 标识字段
                • 支持所有基础数据类型
                • 使用紧凑的二进制编码
            """.trimIndent()
        } catch (e: Exception) {
            basicResult = "❌ 测试失败: ${e.message}"
        }
    }

    /**
     * 2. JceStruct 结构体序列化测试
     */
    private fun testStructSerialization() {
        try {
            // 创建用户结构体
            val user = JceUser().apply {
                userId = 10086
                userName = "张三"
                age = 28
                email = "zhangsan@example.com"
                score = 95.5f
            }

            // 序列化 - toByteArray() 内部调用 writeTo()
            val bytes = user.toByteArray()

            // 反序列化 - 直接使用 readFrom()
            // 注意: toByteArray() 只序列化结构体内部字段，没有包装结构体头
            // 所以反序列化时直接调用 readFrom()，而不是 read(JceStruct, tag, isRequire)
            val decodedUser = JceUser()
            decodedUser.readFrom(JceInputStream(bytes))

            structResult = """
                ✅ JceStruct 结构体序列化成功！

                原始数据:
                • 用户ID: ${user.userId}
                • 用户名: ${user.userName}
                • 年龄: ${user.age}
                • 邮箱: ${user.email}
                • 分数: ${user.score}

                序列化后: ${bytes.size} 字节
                二进制数据: ${bytes.toHexString()}

                反序列化结果:
                • 用户ID: ${decodedUser.userId}
                • 用户名: ${decodedUser.userName}
                • 年龄: ${decodedUser.age}
                • 邮箱: ${decodedUser.email}
                • 分数: ${decodedUser.score}

                说明:
                • 继承 JceStruct 抽象类
                • 实现 writeTo() 和 readFrom() 方法
                • toByteArray() 序列化，readFrom() 反序列化
                • read(JceStruct, tag, isRequire) 用于嵌套结构体
            """.trimIndent()
        } catch (e: Exception) {
            structResult = "❌ 测试失败: ${e.message}"
        }
    }

    /**
     * 3. 嵌套结构体序列化测试
     */
    private fun testNestedSerialization() {
        try {
            // 创建地址
            val address = JceAddress().apply {
                province = "广东省"
                city = "深圳市"
                district = "南山区"
                street = "科技园路1号"
                zipCode = "518000"
            }

            // 创建带地址的用户
            val userWithAddress = JceUserWithAddress().apply {
                userId = 10010
                userName = "李四"
                this.address = address
                phone = "13800138000"
            }

            // 序列化
            val bytes = userWithAddress.toByteArray()

            // 反序列化
            val decoded = JceUserWithAddress()
            decoded.readFrom(JceInputStream(bytes))

            nestedResult = """
                ✅ 嵌套结构体序列化成功！

                原始数据:
                • 用户ID: ${userWithAddress.userId}
                • 用户名: ${userWithAddress.userName}
                • 电话: ${userWithAddress.phone}
                • 地址:
                  - 省份: ${address.province}
                  - 城市: ${address.city}
                  - 区县: ${address.district}
                  - 街道: ${address.street}
                  - 邮编: ${address.zipCode}

                序列化后: ${bytes.size} 字节

                反序列化结果:
                • 用户ID: ${decoded.userId}
                • 用户名: ${decoded.userName}
                • 电话: ${decoded.phone}
                • 地址:
                  - 省份: ${decoded.address?.province}
                  - 城市: ${decoded.address?.city}
                  - 区县: ${decoded.address?.district}
                  - 街道: ${decoded.address?.street}
                  - 邮编: ${decoded.address?.zipCode}

                说明:
                • JCE 支持嵌套结构体
                • 嵌套结构体使用 STRUCT_BEGIN/END 标记
            """.trimIndent()
        } catch (e: Exception) {
            nestedResult = "❌ 测试失败: ${e.message}"
        }
    }

    /**
     * 4. 列表序列化测试
     */
    private fun testListSerialization() {
        try {
            // 创建整数列表
            val intList = listOf(1, 2, 3, 4, 5)
            // 创建字符串列表
            val stringList = listOf("Kotlin", "JCE", "Kuikly")
            // 创建字节数组
            val byteArray = byteArrayOf(0x48, 0x65, 0x6C, 0x6C, 0x6F) // "Hello"

            // 序列化
            val os = JceOutputStream()
            os.write(intList, 0)
            os.write(stringList, 1)
            os.write(byteArray, 2)

            val bytes = os.toByteArray()

            // 反序列化
            val input = JceInputStream(bytes)
            val decodedIntList = input.readArray(listOf(0), "Int", 0, true)
            val decodedStringList = input.readArray(listOf(""), "", 1, true)
            val decodedByteArray = input.read(null as ByteArray?, 2, true)

            listResult = """
                ✅ 列表序列化成功！

                原始数据:
                • 整数列表: $intList
                • 字符串列表: $stringList
                • 字节数组: ${byteArray.toHexString()}

                序列化后: ${bytes.size} 字节

                反序列化结果:
                • 整数列表: $decodedIntList
                • 字符串列表: $decodedStringList
                • 字节数组: ${decodedByteArray?.toHexString()}
                • 字节数组(字符串): ${decodedByteArray?.decodeToString()}

                说明:
                • 支持 List<T> 泛型列表
                • 支持 ByteArray 字节数组
                • ByteArray 使用 SIMPLE_LIST 优化存储
            """.trimIndent()
        } catch (e: Exception) {
            listResult = "❌ 测试失败: ${e.message}"
        }
    }

    /**
     * 5. Map 序列化测试
     */
    private fun testMapSerialization() {
        try {
            // 创建 Map
            val stringMap = mapOf(
                "name" to "王五",
                "city" to "北京",
                "job" to "工程师"
            )
            val intMap = mapOf(
                1 to 100,
                2 to 200,
                3 to 300
            )

            // 序列化
            val os = JceOutputStream()
            os.write(stringMap, 0)
            os.write(intMap, 1)

            val bytes = os.toByteArray()

            // 反序列化
            val input = JceInputStream(bytes)
            val decodedStringMap = input.readStringMap(0, true)
            val decodedIntMap = input.readMap(mapOf(0 to 0), 1, true)

            mapResult = """
                ✅ Map 序列化成功！

                原始数据:
                • 字符串Map: $stringMap
                • 整数Map: $intMap

                序列化后: ${bytes.size} 字节

                反序列化结果:
                • 字符串Map: $decodedStringMap
                • 整数Map: $decodedIntMap

                说明:
                • 支持 Map<K, V> 泛型 Map
                • 支持多种键值类型组合
                • Map 内部按 key-value 交替存储
            """.trimIndent()
        } catch (e: Exception) {
            mapResult = "❌ 测试失败: ${e.message}"
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
                        backgroundColor(Color(0xFF009688))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text("JCE 序列化演示")
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
                        backgroundColor(Color(0xFFE0F2F1))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text("""
                                JCE (Jce Codec Engine) 是腾讯的二进制序列化协议：

                                • 高效紧凑的二进制编码
                                • 支持基础类型、结构体、列表、Map
                                • 使用 tag 标识字段，支持版本兼容
                                • 广泛应用于 RPC 通信和数据存储
                                • 类似 Protocol Buffers 的设计理念

                                点击下方按钮测试各项功能
                            """.trimIndent())
                            fontSize(14f)
                            color(Color(0xFF424242))
                            lineHeight(22f)
                        }
                    }
                }

                // 1. 基础类型测试
                ctx.createTestSection(
                    this,
                    title = "1. 基础类型序列化",
                    buttonText = "测试基础类型",
                    resultProvider = { ctx.basicResult },
                    backgroundColor = Color(0xFFE8F5E9),
                    buttonWidth = ctx.pagerData.pageViewWidth
                ) {
                    ctx.testBasicSerialization()
                }

                // 2. 结构体测试
                ctx.createTestSection(
                    this,
                    title = "2. JceStruct 结构体",
                    buttonText = "测试结构体序列化",
                    resultProvider = { ctx.structResult },
                    backgroundColor = Color(0xFFE3F2FD),
                    buttonWidth = ctx.pagerData.pageViewWidth
                ) {
                    ctx.testStructSerialization()
                }

                // 3. 嵌套结构体测试
                ctx.createTestSection(
                    this,
                    title = "3. 嵌套结构体",
                    buttonText = "测试嵌套结构体",
                    resultProvider = { ctx.nestedResult },
                    backgroundColor = Color(0xFFFFF3E0),
                    buttonWidth = ctx.pagerData.pageViewWidth
                ) {
                    ctx.testNestedSerialization()
                }

                // 4. 列表测试
                ctx.createTestSection(
                    this,
                    title = "4. 列表序列化",
                    buttonText = "测试列表序列化",
                    resultProvider = { ctx.listResult },
                    backgroundColor = Color(0xFFFCE4EC),
                    buttonWidth = ctx.pagerData.pageViewWidth
                ) {
                    ctx.testListSerialization()
                }

                // 5. Map 测试
                ctx.createTestSection(
                    this,
                    title = "5. Map 序列化",
                    buttonText = "测试 Map 序列化",
                    resultProvider = { ctx.mapResult },
                    backgroundColor = Color(0xFFEDE7F6),
                    buttonWidth = ctx.pagerData.pageViewWidth
                ) {
                    ctx.testMapSerialization()
                }

                // API 参考
                View {
                    attr {
                        margin(16f)
                        marginBottom(8f)
                        padding(12f)
                        backgroundColor(Color(0xFF009688))
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
                                核心类说明：

                                JceOutputStream - 序列化输出流
                                • write(value, tag) - 写入各种类型
                                • toByteArray() - 获取序列化字节数组

                                JceInputStream - 反序列化输入流
                                • read(default, tag, isRequired)
                                • readString(tag, isRequired)
                                • readMap(template, tag, isRequired)
                                • readArray(template, type, tag, isRequired)

                                JceStruct - 结构体基类
                                • writeTo(os) - 序列化到输出流
                                • readFrom(is) - 从输入流反序列化
                                • toByteArray() - 序列化为字节数组
                                • newInit() - 创建新实例

                                使用示例：
                                class MyStruct : JceStruct() {
                                    var id: Int = 0
                                    var name: String = ""
                                    
                                    override fun writeTo(os: JceOutputStream) {
                                        os.write(id, 0)
                                        os.write(name, 1)
                                    }
                                    
                                    override fun readFrom(is: JceInputStream) {
                                        id = is.read(id, 0, true)
                                        name = is.readString(1, true) ?: ""
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
                    backgroundColor(Color(0xFF009688))
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
                        size(buttonWidth - 56f, 44f)
                        borderRadius(8f)
                        marginBottom(12f)
                        backgroundColor(Color(0xFF009688))
                        highlightBackgroundColor(Color(0xFF00796B))
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

// ========== 辅助扩展函数 ==========

/**
 * ByteArray 转十六进制字符串 (Kotlin Multiplatform 兼容)
 */
private fun ByteArray.toHexString(): String {
    val hexChars = "0123456789ABCDEF"
    val result = StringBuilder(size * 2)
    for (byte in this) {
        val i = byte.toInt() and 0xFF
        result.append(hexChars[i shr 4])
        result.append(hexChars[i and 0x0F])
    }
    return result.toString()
}

// ========== JCE 数据结构定义 ==========

/**
 * 用户 JCE 结构体
 */
class JceUser : JceStruct() {
    var userId: Int = 0
    var userName: String = ""
    var age: Int = 0
    var email: String = ""
    var score: Float = 0f

    override fun writeTo(os: JceOutputStream) {
        os.write(userId, 0)
        os.write(userName, 1)
        os.write(age, 2)
        os.write(email, 3)
        os.write(score, 4)
    }

    override fun readFrom(`is`: JceInputStream) {
        userId = `is`.read(userId, 0, true)
        userName = `is`.readString(1, true) ?: ""
        age = `is`.read(age, 2, true)
        email = `is`.readString(3, true) ?: ""
        score = `is`.read(score, 4, true)
    }

    override fun newInit(): JceStruct {
        return JceUser()
    }
}

/**
 * 地址 JCE 结构体
 */
class JceAddress : JceStruct() {
    var province: String = ""
    var city: String = ""
    var district: String = ""
    var street: String = ""
    var zipCode: String = ""

    override fun writeTo(os: JceOutputStream) {
        os.write(province, 0)
        os.write(city, 1)
        os.write(district, 2)
        os.write(street, 3)
        os.write(zipCode, 4)
    }

    override fun readFrom(`is`: JceInputStream) {
        province = `is`.readString(0, true) ?: ""
        city = `is`.readString(1, true) ?: ""
        district = `is`.readString(2, true) ?: ""
        street = `is`.readString(3, true) ?: ""
        zipCode = `is`.readString(4, true) ?: ""
    }

    override fun newInit(): JceStruct {
        return JceAddress()
    }
}

/**
 * 带地址的用户 JCE 结构体 - 演示嵌套结构
 */
class JceUserWithAddress : JceStruct() {
    var userId: Int = 0
    var userName: String = ""
    var address: JceAddress? = null
    var phone: String = ""

    override fun writeTo(os: JceOutputStream) {
        os.write(userId, 0)
        os.write(userName, 1)
        address?.let { os.write(it, 2) }
        os.write(phone, 3)
    }

    override fun readFrom(`is`: JceInputStream) {
        userId = `is`.read(userId, 0, true)
        userName = `is`.readString(1, true) ?: ""
        address = `is`.read(JceAddress(), 2, false) as? JceAddress
        phone = `is`.readString(3, true) ?: ""
    }

    override fun newInit(): JceStruct {
        return JceUserWithAddress()
    }
}
