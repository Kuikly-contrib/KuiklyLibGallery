package com.example.kuiklylibgallery

import com.example.kuiklylibgallery.base.BasePager
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import kotlinx.datetime.Clock
import com.kuikly.thirdparty.kmp.lib.mmkv.defaultMMKV
import com.kuikly.thirdparty.kmp.lib.mmkv.mmkvWithID
import com.kuikly.thirdparty.kmp.lib.mmkv.MMKV_KMP

/**
 * MMKV 库使用示例页面
 * 展示 MMKV 的各种 KV 存储操作
 */
@Page("MMKVDemoPage")
internal class MMKVDemoPage : BasePager() {

    // 响应式状态
    private var initStatus by observable("等待初始化...")
    private var writeDemo by observable("")
    private var readDemo by observable("")
    private var deleteDemo by observable("")
    private var queryDemo by observable("")
    private var customIdDemo by observable("")
    private var encryptedDemo by observable("")
    private var batchDemo by observable("")
    private var performanceDemo by observable("")

    private var isInitialized = false
    private var mmkv: MMKV_KMP? = null

    override fun created() {
        super.created()
    }

    /**
     * 初始化 MMKV 并演示所有功能
     */
    private fun demonstrateMMKVFeatures() {
        // 1. 初始化 MMKV
        initializeMMKV()

        // 2. 写入数据演示
        try {
            demonstrateWrite()
        } catch (e: Exception) {
            writeDemo = "写入演示失败: ${e.message}"
        }

        // 3. 读取数据演示
        try {
            demonstrateRead()
        } catch (e: Exception) {
            readDemo = "读取演示失败: ${e.message}"
        }

        // 4. 删除数据演示
        try {
            demonstrateDelete()
        } catch (e: Exception) {
            deleteDemo = "删除演示失败: ${e.message}"
        }

        // 5. 查询操作演示
        try {
            demonstrateQuery()
        } catch (e: Exception) {
            queryDemo = "查询演示失败: ${e.message}"
        }

        // 6. 自定义 ID 实例演示
        demonstrateCustomId()

        // 7. 加密实例演示
        demonstrateEncrypted()

        // 8. 批量操作演示
        try {
            demonstrateBatch()
        } catch (e: Exception) {
            batchDemo = "批量操作演示失败: ${e.message}"
        }

        // 9. 性能演示
        try {
            demonstratePerformance()
        } catch (e: Exception) {
            performanceDemo = "性能演示失败: ${e.message}"
        }
    }

    /**
     * 初始化 MMKV
     */
    private fun initializeMMKV() {
        try {
            mmkv = defaultMMKV()
            val id = mmkv?.mmapID() ?: "unknown"
            initStatus = """
                MMKV 初始化成功!
                实例 ID: $id
            """.trimIndent()
        } catch (e: Exception) {
            initStatus = "MMKV 初始化失败: ${e.message}"
        }
    }

    /**
     * 演示写入操作
     */
    private fun demonstrateWrite() {
        val kv = mmkv ?: return

        // 写入各种类型的数据
        kv.set("string_key", "Hello MMKV!")
        kv.set("bool_key", true)
        kv.set("int_key", 12345)
        kv.set("long_key", 9876543210L)
        kv.set("float_key", 3.14f)
        kv.set("double_key", 2.718281828)
        kv.set("bytes_key", byteArrayOf(1, 2, 3, 4, 5))
        kv.set("stringset_key", setOf("Apple", "Banana", "Cherry"))

        writeDemo = """
            写入成功的数据：
            
            String: "Hello MMKV!"
            Boolean: true
            Int: 12345
            Long: 9876543210
            Float: 3.14
            Double: 2.718281828
            ByteArray: [1, 2, 3, 4, 5]
            StringSet: ["Apple", "Banana", "Cherry"]
        """.trimIndent()
    }

    /**
     * 演示读取操作
     */
    private fun demonstrateRead() {
        val kv = mmkv ?: return

        val stringVal = kv.getString("string_key")
        val boolVal = kv.getBoolean("bool_key")
        val intVal = kv.getInt("int_key")
        val longVal = kv.getLong("long_key")
        val floatVal = kv.getFloat("float_key")
        val doubleVal = kv.getDouble("double_key")
        val bytesVal = kv.getByteArray("bytes_key")
        val stringSetVal = kv.getStringSet("stringset_key")

        // 测试默认值
        val defaultString = kv.getString("not_exist", "默认值")
        val defaultInt = kv.getInt("not_exist", -1)

        readDemo = """
            读取的数据：
            
            String: "$stringVal"
            Boolean: $boolVal
            Int: $intVal
            Long: $longVal
            Float: $floatVal
            Double: $doubleVal
            ByteArray: ${bytesVal?.joinToString() ?: "null"}
            StringSet: $stringSetVal
            
            默认值测试：
            String (不存在): "$defaultString"
            Int (不存在): $defaultInt
        """.trimIndent()
    }

    /**
     * 演示删除操作
     */
    private fun demonstrateDelete() {
        val kv = mmkv ?: return

        // 先写入测试数据
        kv.set("delete_test1", "will be deleted")
        kv.set("delete_test2", "will be deleted")

        // 验证写入
        val beforeTest1 = kv.containsKey("delete_test1")
        val beforeTest2 = kv.containsKey("delete_test2")

        // 删除单个 Key
        kv.removeValueForKey("delete_test1")
        val afterDeleteTest1 = kv.containsKey("delete_test1")

        // 再删除另一个
        kv.removeValueForKey("delete_test2")
        val afterDeleteTest2 = kv.containsKey("delete_test2")

        deleteDemo = """
            删除操作演示：
            
            删除前：
            delete_test1 存在: $beforeTest1
            delete_test2 存在: $beforeTest2
            
            删除 delete_test1 后：
            delete_test1 存在: $afterDeleteTest1
            
            删除 delete_test2 后：
            delete_test2 存在: $afterDeleteTest2
        """.trimIndent()
    }

    /**
     * 演示查询操作
     */
    private fun demonstrateQuery() {
        val kv = mmkv ?: return

        val exists = kv.containsKey("string_key")
        val notExists = kv.containsKey("not_exist_key")
        val allKeys = kv.allKeys()
        val mmapId = kv.mmapID()

        queryDemo = """
            查询操作演示：
            
            string_key 存在: $exists
            not_exist_key 存在: $notExists
            
            实例 ID: $mmapId
            Keys 数量: ${allKeys.size}
            
            所有 Keys:
            ${allKeys.take(10).joinToString("\n            • ", "• ")}
            ${if (allKeys.size > 10) "... 还有 ${allKeys.size - 10} 个" else ""}
        """.trimIndent()
    }

    /**
     * 演示自定义 ID 实例
     */
    private fun demonstrateCustomId() {
        try {
            val customKv = mmkvWithID("user_data")
            customKv.set("userId", 10001)
            customKv.set("userName", "张三")
            customKv.set("loginTime", Clock.System.now().toEpochMilliseconds())

            val userId = customKv.getInt("userId")
            val userName = customKv.getString("userName")
            val loginTime = customKv.getLong("loginTime")

            customIdDemo = """
                自定义 ID 实例演示：
                
                实例 ID: ${customKv.mmapID()}
                
                写入并读取的数据：
                userId: $userId
                userName: $userName
                loginTime: $loginTime
            """.trimIndent()
        } catch (e: Exception) {
            customIdDemo = "自定义 ID 实例失败: ${e.message}"
        }
    }

    /**
     * 演示加密实例
     */
    private fun demonstrateEncrypted() {
        try {
            val encryptedKv = defaultMMKV("my_secret_key_123")
            encryptedKv.set("sensitive_data", "这是加密存储的数据")
            encryptedKv.set("password", "p@ssw0rd123")

            val sensitiveData = encryptedKv.getString("sensitive_data")
            val password = encryptedKv.getString("password")

            encryptedDemo = """
                加密实例演示：
                
                使用密钥: "my_secret_key_123"
                
                写入并读取的加密数据：
                sensitive_data: $sensitiveData
                password: $password
                
                注意: 数据在磁盘上是加密存储的
            """.trimIndent()
        } catch (e: Exception) {
            encryptedDemo = "加密实例失败: ${e.message}"
        }
    }

    /**
     * 演示批量操作
     */
    private fun demonstrateBatch() {
        val kv = mmkv ?: return

        // 批量写入
        val startWrite = Clock.System.now().toEpochMilliseconds()
        for (i in 1..100) {
            kv.set("batch_key_$i", "value_$i")
        }
        val writeTime = Clock.System.now().toEpochMilliseconds() - startWrite

        // 批量读取
        val startRead = Clock.System.now().toEpochMilliseconds()
        val values = mutableListOf<String>()
        for (i in 1..100) {
            values.add(kv.getString("batch_key_$i"))
        }
        val readTime = Clock.System.now().toEpochMilliseconds() - startRead

        // 批量删除
        val keysToDelete = (1..100).map { "batch_key_$it" }
        val startDelete = Clock.System.now().toEpochMilliseconds()
        kv.removeValuesForKeys(keysToDelete)
        val deleteTime = Clock.System.now().toEpochMilliseconds() - startDelete

        batchDemo = """
            批量操作演示 (100条数据)：
            
            批量写入耗时: ${writeTime}ms
            批量读取耗时: ${readTime}ms
            批量删除耗时: ${deleteTime}ms
            
            读取的前5条数据:
            ${values.take(5).mapIndexed { i, v -> "batch_key_${i+1}: $v" }.joinToString("\n            ")}
        """.trimIndent()
    }

    /**
     * 演示性能测试
     */
    private fun demonstratePerformance() {
        val kv = mmkv ?: return

        // 同步写入测试
        kv.set("sync_test", "sync_value")
        kv.sync()

        // 异步写入测试
        kv.set("async_test", "async_value")
        kv.async()

        // 空间回收
        kv.trim()

        // 清空测试数据（可选）
        // kv.clearAll()

        performanceDemo = """
            性能相关操作：
            
            sync() - 同步写入磁盘
            async() - 异步写入磁盘
            trim() - 回收空间
            clearAll() - 清空所有数据
            
            当前数据条数: ${kv.count}
            
            MMKV 特点：
            • mmap 内存映射，读写速度快
            • 自动增量更新
            • 支持多进程访问
            • 数据实时持久化
        """.trimIndent()
    }

    override fun body(): ViewBuilder {
        val ctx = this

        // 首次初始化数据
        if (!isInitialized) {
            demonstrateMMKVFeatures()
            isInitialized = true
        }

        return {
            attr {
                backgroundColor(Color.WHITE)
            }

            List {
                attr {
                    flex(1f)
                }

                // 标题
                ctx.createSectionTitle("MMKV 使用示例", Color(0xFF2196F3)).invoke(this)

                // 1. 初始化状态
                ctx.createSectionTitle("初始化状态", Color(0xFF03A9F4)).invoke(this)
                ctx.createInfoCard("MMKV 状态", ctx.initStatus, Color(0xFFE3F2FD)).invoke(this)

                // 2. 写入操作
                ctx.createSectionTitle("写入数据", Color(0xFF03A9F4)).invoke(this)
                ctx.createCodeCard("set() 方法", ctx.writeDemo, Color(0xFFE8F5E9)).invoke(this)

                // 3. 读取操作
                ctx.createSectionTitle("读取数据", Color(0xFF03A9F4)).invoke(this)
                ctx.createCodeCard("get() 方法", ctx.readDemo, Color(0xFFFFF9C4)).invoke(this)

                // 4. 删除操作
                ctx.createSectionTitle("删除数据", Color(0xFF03A9F4)).invoke(this)
                ctx.createCodeCard("removeValueForKey()", ctx.deleteDemo, Color(0xFFFFEBEE)).invoke(this)

                // 5. 查询操作
                ctx.createSectionTitle("查询操作", Color(0xFF03A9F4)).invoke(this)
                ctx.createCodeCard("查询 API", ctx.queryDemo, Color(0xFFF3E5F5)).invoke(this)

                // 6. 自定义 ID 实例
                ctx.createSectionTitle("自定义 ID 实例", Color(0xFF03A9F4)).invoke(this)
                ctx.createCodeCard("mmkvWithID()", ctx.customIdDemo, Color(0xFFE0F7FA)).invoke(this)

                // 7. 加密实例
                ctx.createSectionTitle("加密存储", Color(0xFF03A9F4)).invoke(this)
                ctx.createCodeCard("加密实例", ctx.encryptedDemo, Color(0xFFFCE4EC)).invoke(this)

                // 8. 批量操作
                ctx.createSectionTitle("批量操作", Color(0xFF03A9F4)).invoke(this)
                ctx.createCodeCard("性能测试", ctx.batchDemo, Color(0xFFE8EAF6)).invoke(this)

                // 9. 性能相关
                ctx.createSectionTitle("性能说明", Color(0xFF03A9F4)).invoke(this)
                ctx.createCodeCard("高级操作", ctx.performanceDemo, Color(0xFFF5F5F5)).invoke(this)

                // API 说明
                ctx.createSectionTitle("API 说明", Color(0xFF03A9F4)).invoke(this)
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
                                主要 API：
                                
                                创建实例：
                                • defaultMMKV() - 默认实例
                                • defaultMMKV(cryptKey) - 加密实例
                                • mmkvWithID(id) - 自定义ID实例
                                
                                写入数据：
                                • set(key, value) - 支持多种类型
                                
                                读取数据：
                                • getString(key, default)
                                • getBoolean(key, default)
                                • getInt/Long/Float/Double
                                • getByteArray(key)
                                • getStringSet(key)
                                
                                删除数据：
                                • removeValueForKey(key)
                                • removeValuesForKeys(keys)
                                • clearAll()
                                
                                查询操作：
                                • containsKey(key)
                                • allKeys()
                                • count
                                • mmapID()
                                
                                其他：
                                • sync() / async()
                                • trim()
                            """.trimIndent())
                            fontSize(14f)
                            color(Color(0xFF424242))
                            lineHeight(22f)
                        }
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

    /**
     * 创建信息卡片
     */
    private fun createInfoCard(label: String, value: String, bgColor: Color): ViewBuilder {
        return {
            View {
                attr {
                    marginLeft(16f)
                    marginRight(16f)
                    marginBottom(8f)
                    padding(12f)
                    backgroundColor(bgColor)
                    borderRadius(8f)
                }

                Text {
                    attr {
                        text(label)
                        fontSize(14f)
                        fontWeightBold()
                        color(Color(0xFF666666))
                    }
                }

                Text {
                    attr {
                        text(value)
                        fontSize(16f)
                        color(Color.BLACK)
                        marginTop(4f)
                    }
                }
            }
        }
    }

    /**
     * 创建代码展示卡片
     */
    private fun createCodeCard(label: String, code: String, bgColor: Color): ViewBuilder {
        return {
            View {
                attr {
                    marginLeft(16f)
                    marginRight(16f)
                    marginBottom(8f)
                    padding(12f)
                    backgroundColor(bgColor)
                    borderRadius(8f)
                }

                Text {
                    attr {
                        text(label)
                        fontSize(14f)
                        fontWeightBold()
                        color(Color(0xFF666666))
                        marginBottom(8f)
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
                            text(code)
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
