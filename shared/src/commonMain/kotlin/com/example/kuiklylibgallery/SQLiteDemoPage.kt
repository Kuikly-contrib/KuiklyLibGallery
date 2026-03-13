package com.example.kuiklylibgallery

import com.example.kuiklylibgallery.base.BasePager
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.layout.FlexAlign
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.layout.FlexJustifyContent
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kmm.component.VBSQLite.*
import com.tencent.kuikly.core.log.KLog

/**
 * VBSQLite Demo 页面
 * 演示跨平台 SQLite 数据库的基本操作：建表、增删改查、事务
 */
@Page("SQLiteDemoPage", supportInLocal = true)
internal class SQLiteDemoPage : BasePager() {

    // 响应式状态 - 各操作区域的日志输出
    private var initStatus by observable("等待初始化...")
    private var insertResult by observable("")
    private var queryResult by observable("")
    private var updateResult by observable("")
    private var deleteResult by observable("")
    private var transactionResult by observable("")
    private var logOutput by observable("点击按钮开始操作...")

    private var isInitialized = false
    private var db: IConnection? = null

    override fun created() {
        super.created()
    }

    // 日志实现
    private val logImpl = object : ILog {
        override fun d(tag: String, content: String) {
            KLog.d(tag, content)
        }
        override fun i(tag: String, content: String) {
            KLog.i(tag, content)
        }
        override fun e(tag: String, content: String, throwable: Throwable) {
            KLog.e(tag, "$content, ${throwable.message}")
        }
    }

    /**
     * 初始化数据库
     */
    private fun initDatabase() {
        try {
            // 从宿主传递的 pageData.params 中获取数据库目录（鸿蒙端）
            val databaseDir = pagerData.params.optString("databaseDir").takeIf { it.isNotEmpty() }
            KLog.d("SQLiteDemoPage", "databaseDir: $databaseDir")

            // 初始化日志模块 + 数据库目录
            SqliteInitTask.init(logImpl,databaseDir)

            // 创建数据库配置
            val config = DefaultConfiguration(
                name = "vbsqlite_demo.db",
                path = defaultDatabasePath(),
                version = 1,
                journalMode = JournalMode.WAL,
                synchronousMode = SynchronousMode.NORMAL,
                busyTimeout = 3000,
                create = { connection ->
                    // 建表：用户表
                    connection.exec("""
                        CREATE TABLE IF NOT EXISTS user (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT NOT NULL,
                            age INTEGER NOT NULL,
                            email TEXT,
                            created_at TEXT NOT NULL
                        )
                    """)
                },
                upgrade = { connection, oldVersion, newVersion ->
                    if (oldVersion < 2) {
                        connection.exec("ALTER TABLE user ADD COLUMN phone TEXT")
                    }
                }
            )

            // 获取数据库连接
            db = VBDatabaseConnection(config)
            initStatus = "✅ 数据库初始化成功!\n数据库: vbsqlite_demo.db\n模式: WAL"
            logOutput = "数据库已就绪\n路径: ${defaultDatabasePath()}\n名称: vbsqlite_demo.db\n日志模式: WAL | 同步模式: NORMAL"
        } catch (e: Exception) {
            initStatus = "❌ 数据库初始化失败: ${e.message}"
            logOutput = "初始化出错: ${e.message}"
        }
    }

    /**
     * 插入数据演示
     */
    private fun demonstrateInsert() {
        val conn = db ?: run {
            logOutput = "⚠️ 请先初始化数据库"
            return
        }
        try {
            val logSb = StringBuilder()

            // 先清空旧数据
            val deleteSql = "DELETE FROM user"
            conn.exec(deleteSql)
            logSb.appendLine("[执行] $deleteSql")

            // 使用 executeInsert 插入数据
            val insertSql = "INSERT INTO user (name, age, email, created_at) VALUES (?, ?, ?, ?)"

            val params1 = arrayOf("张三", 25, "zhangsan@example.com", "2026-03-06")
            conn.executeInsert(insertSql, params1)
            logSb.appendLine("[插入] $insertSql")
            logSb.appendLine("  参数: ${params1.joinToString(", ")}")

            val params2 = arrayOf("李四", 30, "lisi@example.com", "2026-03-06")
            conn.executeInsert(insertSql, params2)
            logSb.appendLine("[插入] $insertSql")
            logSb.appendLine("  参数: ${params2.joinToString(", ")}")

            val params3 = arrayOf<Any?>("王五", 28, null, "2026-03-06")
            conn.executeInsert(insertSql, params3)
            logSb.appendLine("[插入] $insertSql")
            logSb.appendLine("  参数: ${params3.joinToString(", ") { it?.toString() ?: "NULL" }}")

            insertResult = """
                ✅ 插入成功！已插入 3 条数据：
                • 张三, 25岁, zhangsan@example.com
                • 李四, 30岁, lisi@example.com
                • 王五, 28岁, (无邮箱)
            """.trimIndent()
            logOutput = logSb.toString().trimEnd()
        } catch (e: Exception) {
            insertResult = "❌ 插入失败: ${e.message}"
            logOutput = "插入出错: ${e.message}"
        }
    }

    /**
     * 查询数据演示
     */
    private fun demonstrateQuery() {
        val conn = db ?: run {
            logOutput = "⚠️ 请先初始化数据库"
            return
        }
        try {
            val logSb = StringBuilder()
            val querySql = "SELECT id, name, age, email FROM user"
            logSb.appendLine("[查询] $querySql")

            val cursor = conn.query(querySql)
            val sb = StringBuilder()
            var count = 0
            try {
                while (cursor.next()) {
                    count++
                    // 使用 getColumnCount / getColumnName 动态遍历列
                    val row = mutableMapOf<String, Any?>()
                    for (columnIndex in 0 until cursor.getColumnCount()) {
                        val columnName = cursor.getColumnName(columnIndex) ?: continue
                        row[columnName] = when (cursor.getColumnType(columnIndex)) {
                            1 -> cursor.getInt(columnIndex)   // INTEGER
                            2 -> cursor.getFloat(columnIndex) // FLOAT
                            3 -> cursor.getString(columnIndex) // STRING
                            else -> null
                        }
                    }
                    val id = row["id"] ?: ""
                    val name = row["name"] ?: ""
                    val age = row["age"] ?: ""
                    val email = row["email"] ?: "(空)"
                    sb.appendLine("  #$id  $name | ${age}岁 | $email")
                    logSb.appendLine("  行$count: id=$id, name=$name, age=$age, email=$email")
                }
            } finally {
                cursor.close()
            }

            logSb.appendLine("[结果] 共查询到 $count 条记录")

            queryResult = if (count > 0) {
                "✅ 查询到 ${count} 条记录：\n$sb"
            } else {
                "⚠️ 暂无数据，请先插入数据"
            }
            logOutput = logSb.toString().trimEnd()
        } catch (e: Exception) {
            queryResult = "❌ 查询失败: ${e.message}"
            logOutput = "查询出错: ${e.message}"
        }
    }

    /**
     * 更新数据演示
     */
    private fun demonstrateUpdate() {
        val conn = db ?: run {
            logOutput = "⚠️ 请先初始化数据库"
            return
        }
        try {
            val logSb = StringBuilder()

            // 使用 executeUpdateDelete 更新张三的年龄
            val updateSql = "UPDATE user SET age = ?, email = ? WHERE name = ?"
            val updateParams = arrayOf(26, "zhangsan_new@example.com", "张三")
            conn.executeUpdateDelete(updateSql, updateParams)
            logSb.appendLine("[更新] $updateSql")
            logSb.appendLine("  参数: ${updateParams.joinToString(", ")}")

            // 查询更新后的结果
            val verifySql = "SELECT name, age, email FROM user WHERE name = ?"
            val verifyParams = arrayOf("张三")
            logSb.appendLine("[验证] $verifySql")
            logSb.appendLine("  参数: ${verifyParams.joinToString(", ")}")

            val cursor = conn.query(verifySql, verifyParams)
            val sb = StringBuilder()
            try {
                if (cursor.next()) {
                    val name = cursor.getString(0)
                    val age = cursor.getInt(1)
                    val email = cursor.getString(2)
                    sb.appendLine("更新后 → $name | ${age}岁 | $email")
                    logSb.appendLine("  结果: name=$name, age=$age, email=$email")
                }
            } finally {
                cursor.close()
            }

            updateResult = """
                ✅ 更新成功！
                张三: age 25→26, email 已更新
                $sb
            """.trimIndent()
            logOutput = logSb.toString().trimEnd()
        } catch (e: Exception) {
            updateResult = "❌ 更新失败: ${e.message}"
            logOutput = "更新出错: ${e.message}"
        }
    }

    /**
     * 删除数据演示
     */
    private fun demonstrateDelete() {
        val conn = db ?: run {
            logOutput = "⚠️ 请先初始化数据库"
            return
        }
        try {
            val logSb = StringBuilder()

            // 使用 executeUpdateDelete 删除王五
            val deleteSql = "DELETE FROM user WHERE name = ?"
            val deleteParams = arrayOf("王五")
            conn.executeUpdateDelete(deleteSql, deleteParams)
            logSb.appendLine("[删除] $deleteSql")
            logSb.appendLine("  参数: ${deleteParams.joinToString(", ")}")

            // 查询剩余数据
            val countSql = "SELECT COUNT(*) FROM user"
            logSb.appendLine("[验证] $countSql")
            val cursor = conn.query(countSql)
            var remaining = 0
            try {
                if (cursor.next()) {
                    remaining = cursor.getInt(0)
                }
            } finally {
                cursor.close()
            }
            logSb.appendLine("  剩余记录数: $remaining")

            deleteResult = """
                ✅ 删除成功！
                已删除: 王五
                剩余记录数: $remaining
            """.trimIndent()
            logOutput = logSb.toString().trimEnd()
        } catch (e: Exception) {
            deleteResult = "❌ 删除失败: ${e.message}"
            logOutput = "删除出错: ${e.message}"
        }
    }

    /**
     * 事务操作演示
     */
    private fun demonstrateTransaction() {
        val conn = db ?: run {
            logOutput = "⚠️ 请先初始化数据库"
            return
        }
        try {
            val logSb = StringBuilder()

            // 先清空再批量插入
            val clearSql = "DELETE FROM user"
            conn.exec(clearSql)
            logSb.appendLine("[执行] $clearSql")

            val insertSql = "INSERT INTO user (name, age, email, created_at) VALUES (?, ?, ?, ?)"
            logSb.appendLine("[事务] BEGIN")

            val txnParams1 = arrayOf("赵六", 35, "zhaoliu@example.com", "2026-03-06")
            val txnParams2 = arrayOf("孙七", 22, "sunqi@example.com", "2026-03-06")
            val txnParams3 = arrayOf("周八", 40, "zhouba@example.com", "2026-03-06")

            conn.begin { txn ->
                txn.executeInsert(insertSql, txnParams1)
                txn.executeInsert(insertSql, txnParams2)
                txn.executeInsert(insertSql, txnParams3)
                STMT.COMMIT
            }

            logSb.appendLine("  [插入] $insertSql")
            logSb.appendLine("    参数: ${txnParams1.joinToString(", ")}")
            logSb.appendLine("  [插入] $insertSql")
            logSb.appendLine("    参数: ${txnParams2.joinToString(", ")}")
            logSb.appendLine("  [插入] $insertSql")
            logSb.appendLine("    参数: ${txnParams3.joinToString(", ")}")
            logSb.appendLine("[事务] COMMIT")

            // 查询事务插入结果
            val verifySql = "SELECT id, name, age FROM user"
            logSb.appendLine("[验证] $verifySql")
            val cursor = conn.query(verifySql)
            val sb = StringBuilder()
            var count = 0
            try {
                while (cursor.next()) {
                    count++
                    val id = cursor.getInt(0)
                    val name = cursor.getString(1)
                    val age = cursor.getInt(2)
                    sb.appendLine("  #$id $name | ${age}岁")
                    logSb.appendLine("  行$count: id=$id, name=$name, age=$age")
                }
            } finally {
                cursor.close()
            }
            logSb.appendLine("[结果] 事务提交成功，共插入 $count 条")

            transactionResult = """
                ✅ 事务执行成功！
                批量插入 3 条记录（原子操作）：
                $sb
            """.trimIndent()
            logOutput = logSb.toString().trimEnd()
        } catch (e: Exception) {
            transactionResult = "❌ 事务失败（已自动回滚）: ${e.message}"
            logOutput = "事务出错: ${e.message}"
        }
    }

    override fun body(): ViewBuilder {
        val ctx = this

        // 首次初始化
        if (!isInitialized) {
            initDatabase()
            isInitialized = true
        }

        return {
            attr {
                backgroundColor(Color(0xFFF5F7FA))
            }

            List {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                }

                // 导航栏
                RouterNavBar {
                    attr {
                        title = "VBSQLite Demo"
                    }
                }

                View {
                    attr {
                        flexDirection(FlexDirection.COLUMN)
                        padding(16f)
                    }

                    // 标题
                    Text {
                        attr {
                            text("VBSQLite 跨平台数据库演示")
                            fontSize(22f)
                            fontWeightBold()
                            color(Color(0xFF1A1A1A))
                            marginBottom(8f)
                        }
                    }

                    Text {
                        attr {
                            text("支持 Android / iOS / HarmonyOS 三端统一 API")
                            fontSize(14f)
                            color(Color(0xFF666666))
                            marginBottom(20f)
                        }
                    }

                    // 初始化状态卡片
                    ctx.createInfoCard(this, "📦 数据库状态", ctx.initStatus, Color(0xFFE3F2FD))

                    // 操作按钮区域
                    // 插入数据
                    ctx.createActionButton(
                        this,
                        title = "插入数据",
                        description = "INSERT 3 条用户记录",
                        gradientColors = listOf(0xFF43E97B, 0xFF38F9D7)
                    ) {
                        ctx.demonstrateInsert()
                    }
                    ctx.createResultCard(this, ctx.insertResult)

                    // 查询数据
                    ctx.createActionButton(
                        this,
                        title = "查询数据",
                        description = "SELECT 查询所有用户",
                        gradientColors = listOf(0xFF4FACFE, 0xFF00F2FE)
                    ) {
                        ctx.demonstrateQuery()
                    }
                    ctx.createResultCard(this, ctx.queryResult)

                    // 更新数据
                    ctx.createActionButton(
                        this,
                        title = "更新数据",
                        description = "UPDATE 修改张三信息",
                        gradientColors = listOf(0xFFFA709A, 0xFFFEE140)
                    ) {
                        ctx.demonstrateUpdate()
                    }
                    ctx.createResultCard(this, ctx.updateResult)

                    // 删除数据
                    ctx.createActionButton(
                        this,
                        title = "删除数据",
                        description = "DELETE 删除王五记录",
                        gradientColors = listOf(0xFFFF6B6B, 0xFFFF8E8E)
                    ) {
                        ctx.demonstrateDelete()
                    }
                    ctx.createResultCard(this, ctx.deleteResult)

                    // 事务操作
                    ctx.createActionButton(
                        this,
                        title = "事务操作",
                        description = "BEGIN 批量插入（原子性）",
                        gradientColors = listOf(0xFF667EEA, 0xFF764BA2)
                    ) {
                        ctx.demonstrateTransaction()
                    }
                    ctx.createResultCard(this, ctx.transactionResult)

                    // 日志输出区域
                    View {
                        attr {
                            marginTop(24f)
                            width(pagerData.pageViewWidth - 32f)
                            borderRadius(12f)
                            backgroundColor(Color(0xFF2D2D2D))
                            padding(16f)
                        }

                        View {
                            attr {
                                flexDirection(FlexDirection.COLUMN)
                            }

                            Text {
                                attr {
                                    text("📋 操作日志")
                                    fontSize(14f)
                                    fontWeightBold()
                                    color(Color(0xFF4FACFE))
                                    marginBottom(8f)
                                }
                            }

                            Text {
                                attr {
                                    text(ctx.logOutput)
                                    fontSize(13f)
                                    color(Color(0xFFE0E0E0))
                                }
                            }
                        }
                    }

                    // API 说明卡片
                    View {
                        attr {
                            marginTop(20f)
                            width(pagerData.pageViewWidth - 32f)
                            borderRadius(12f)
                            backgroundColor(Color.WHITE)
                            padding(16f)
                        }

                        View {
                            attr {
                                flexDirection(FlexDirection.COLUMN)
                            }

                            Text {
                                attr {
                                    text("📖 VBSQLite API 一览")
                                    fontSize(16f)
                                    fontWeightBold()
                                    color(Color(0xFF1A1A1A))
                                    marginBottom(12f)
                                }
                            }

                            Text {
                                attr {
                                    text("""
                                        连接管理：
                                        • DefaultConfiguration(path, name, logger)
                                        • VBDatabaseConnection(config) - 获取连接
                                        • connection.close() - 关闭连接
                                        
                                        执行 SQL：
                                        • exec(sql, params) - 建表/DDL
                                        • executeInsert(sql, params) - 插入
                                        • executeUpdateDelete(sql, params) - 更新/删除
                                        • query(sql, params) - 查询返回游标
                                        
                                        游标操作：
                                        • cursor.next() - 移动到下一行
                                        • cursor.getColumnCount() - 获取列数
                                        • cursor.getColumnName(index) - 获取列名
                                        • cursor.getColumnType(index) - 获取列类型
                                        • cursor.getString/getInt/getFloat/getBlob...
                                        • cursor.close() - 关闭游标
                                        
                                        事务：
                                        • begin { STMT.COMMIT / STMT.ROLLBACK }
                                        • 异常自动回滚
                                    """.trimIndent())
                                    fontSize(13f)
                                    color(Color(0xFF424242))
                                    lineHeight(20f)
                                }
                            }
                        }
                    }

                    // 底部间距
                    View {
                        attr {
                            height(40f)
                        }
                    }
                }
            }
        }
    }

    /**
     * 创建信息展示卡片
     */
    private fun createInfoCard(
        container: ViewContainer<*, *>,
        title: String,
        content: String,
        bgColor: Color
    ) {
        with(container) {
            View {
                attr {
                    width(pagerData.pageViewWidth - 32f)
                    marginBottom(16f)
                    borderRadius(12f)
                    backgroundColor(bgColor)
                    padding(16f)
                }

                View {
                    attr {
                        flexDirection(FlexDirection.COLUMN)
                    }

                    Text {
                        attr {
                            text(title)
                            fontSize(15f)
                            fontWeightBold()
                            color(Color(0xFF1565C0))
                            marginBottom(8f)
                        }
                    }

                    Text {
                        attr {
                            text(content)
                            fontSize(13f)
                            color(Color(0xFF424242))
                            lineHeight(20f)
                        }
                    }
                }
            }
        }
    }

    /**
     * 创建操作按钮
     */
    private fun createActionButton(
        container: ViewContainer<*, *>,
        title: String,
        description: String,
        gradientColors: List<Long>,
        onClick: () -> Unit
    ) {
        with(container) {
            View {
                attr {
                    width(pagerData.pageViewWidth - 32f)
                    marginBottom(8f)
                    borderRadius(12f)
                }

                Button {
                    attr {
                        width(pagerData.pageViewWidth - 32f)
                        height(64f)
                        borderRadius(12f)
                        backgroundLinearGradient(
                            Direction.TO_RIGHT,
                            ColorStop(Color(gradientColors[0]), 0f),
                            ColorStop(Color(gradientColors[1]), 1f)
                        )
                    }

                    View {
                        attr {
                            flexDirection(FlexDirection.ROW)
                            alignItems(FlexAlign.CENTER)
                            justifyContent(FlexJustifyContent.SPACE_BETWEEN)
                            padding(16f)
                        }

                        View {
                            attr {
                                flexDirection(FlexDirection.COLUMN)
                            }

                            Text {
                                attr {
                                    text(title)
                                    fontSize(16f)
                                    fontWeightBold()
                                    color(Color.WHITE)
                                    marginBottom(2f)
                                }
                            }

                            Text {
                                attr {
                                    text(description)
                                    fontSize(12f)
                                    color(Color(0xCCFFFFFF))
                                }
                            }
                        }

                        Text {
                            attr {
                                text("▶")
                                fontSize(18f)
                                color(Color(0xCCFFFFFF))
                            }
                        }
                    }

                    event {
                        click {
                            onClick()
                        }
                    }
                }
            }
        }
    }

    /**
     * 创建结果展示卡片
     */
    private fun createResultCard(
        container: ViewContainer<*, *>,
        content: String
    ) {
        if (content.isEmpty()) return
        with(container) {
            View {
                attr {
                    width(pagerData.pageViewWidth - 32f)
                    marginBottom(16f)
                    borderRadius(8f)
                    backgroundColor(Color(0xFFF8F9FA))
                    padding(12f)
                }

                Text {
                    attr {
                        text(content)
                        fontSize(12f)
                        color(Color(0xFF333333))
                        lineHeight(18f)
                    }
                }
            }
        }
    }
}
