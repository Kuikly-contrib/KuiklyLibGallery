package com.example.kuiklylibgallery

import com.example.kuiklylibgallery.base.BasePager
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

/**
 * kotlinx-datetime 库使用示例页面
 * 展示日期时间的各种操作
 */
@Page("DateTimeDemoTestPage")
internal class DateTimeDemoPage : BasePager() {

    // 响应式状态
    private var currentTime by observable("")
    private var currentDate by observable("")
    private var currentTimestamp by observable("")
    private var utcTime by observable("")
    private var beijingTime by observable("")
    private var newYorkTime by observable("")
    private var tokyoTime by observable("")
    private var tomorrow by observable("")
    private var yesterday by observable("")
    private var nextWeek by observable("")
    private var daysDiff by observable("")
    private var isLeapYear by observable("")
    private var dayOfWeek by observable("")
    private var dayOfYear by observable("")
    private var weekOfYear by observable("")
    private var isoFormat by observable("")
    private var customFormat by observable("")

    private var isInitialized = false

    override fun created() {
        super.created()
    }

    /**
     * 更新所有日期时间信息
     */
    private fun updateAllDateTimeInfo() {
        val now = Clock.System.now()
        val localNow = now.toLocalDateTime(TimeZone.currentSystemDefault())
        
        // 1. 当前时间信息
        currentTime = "${localNow.hour.toString().padStart(2, '0')}:${localNow.minute.toString().padStart(2, '0')}:${localNow.second.toString().padStart(2, '0')}"
        currentDate = "${localNow.year}-${localNow.monthNumber.toString().padStart(2, '0')}-${localNow.dayOfMonth.toString().padStart(2, '0')}"
        currentTimestamp = "${now.toEpochMilliseconds()}"
        
        // 2. 不同时区的时间
        utcTime = formatDateTime(now.toLocalDateTime(TimeZone.UTC))
        beijingTime = formatDateTime(now.toLocalDateTime(TimeZone.of("Asia/Shanghai")))
        newYorkTime = formatDateTime(now.toLocalDateTime(TimeZone.of("America/New_York")))
        tokyoTime = formatDateTime(now.toLocalDateTime(TimeZone.of("Asia/Tokyo")))
        
        // 3. 日期计算
        val todayDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
        tomorrow = formatDate(todayDate.plus(1, DateTimeUnit.DAY))
        yesterday = formatDate(todayDate.minus(1, DateTimeUnit.DAY))
        nextWeek = formatDate(todayDate.plus(7, DateTimeUnit.DAY))
        
        // 4. 日期差值计算
        val targetDate = LocalDate(2026, 12, 31)
        val daysUntilTarget = todayDate.daysUntil(targetDate)
        daysDiff = "距离 2026-12-31 还有 $daysUntilTarget 天"
        
        // 5. 日期属性
        isLeapYear = if (todayDate.year % 4 == 0 && (todayDate.year % 100 != 0 || todayDate.year % 400 == 0)) {
            "${todayDate.year} 是闰年 🎉"
        } else {
            "${todayDate.year} 不是闰年"
        }
        
        dayOfWeek = when (todayDate.dayOfWeek) {
            DayOfWeek.MONDAY -> "星期一"
            DayOfWeek.TUESDAY -> "星期二"
            DayOfWeek.WEDNESDAY -> "星期三"
            DayOfWeek.THURSDAY -> "星期四"
            DayOfWeek.FRIDAY -> "星期五"
            DayOfWeek.SATURDAY -> "星期六"
            DayOfWeek.SUNDAY -> "星期日"
            else -> "未知"
        }
        
        dayOfYear = "今年的第 ${todayDate.dayOfYear} 天"
        
        // 6. ISO 格式
        isoFormat = now.toString()
        customFormat = "${localNow.year}年${localNow.monthNumber}月${localNow.dayOfMonth}日 ${localNow.hour}时${localNow.minute}分"
    }

    /**
     * 格式化日期时间
     */
    private fun formatDateTime(dateTime: LocalDateTime): String {
        return "${dateTime.year}-${dateTime.monthNumber.toString().padStart(2, '0')}-${dateTime.dayOfMonth.toString().padStart(2, '0')} " +
               "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}:${dateTime.second.toString().padStart(2, '0')}"
    }

    /**
     * 格式化日期
     */
    private fun formatDate(date: LocalDate): String {
        return "${date.year}-${date.monthNumber.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}"
    }

    override fun body(): ViewBuilder {
        val ctx = this
        
        // 首次初始化数据
        if (!isInitialized) {
            updateAllDateTimeInfo()
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
                ctx.createSectionTitle("kotlinx-datetime 使用示例", Color(0xFF6200EE)).invoke(this)

                // 1. 当前时间信息
                ctx.createSectionTitle("当前时间", Color(0xFF03DAC5)).invoke(this)
                ctx.createInfoCard("当前时间", ctx.currentTime, Color(0xFFE3F2FD)).invoke(this)
                ctx.createInfoCard("当前日期", ctx.currentDate, Color(0xFFE3F2FD)).invoke(this)
                ctx.createInfoCard("时间戳", ctx.currentTimestamp, Color(0xFFE3F2FD)).invoke(this)

                // 2. 时区转换
                ctx.createSectionTitle("不同时区", Color(0xFF03DAC5)).invoke(this)
                ctx.createInfoCard("UTC 时间", ctx.utcTime, Color(0xFFFFF9C4)).invoke(this)
                ctx.createInfoCard("北京时间", ctx.beijingTime, Color(0xFFFFF9C4)).invoke(this)
                ctx.createInfoCard("纽约时间", ctx.newYorkTime, Color(0xFFFFF9C4)).invoke(this)
                ctx.createInfoCard("东京时间", ctx.tokyoTime, Color(0xFFFFF9C4)).invoke(this)

                // 3. 日期计算
                ctx.createSectionTitle("日期计算", Color(0xFF03DAC5)).invoke(this)
                ctx.createInfoCard("明天", ctx.tomorrow, Color(0xFFE8F5E9)).invoke(this)
                ctx.createInfoCard("昨天", ctx.yesterday, Color(0xFFE8F5E9)).invoke(this)
                ctx.createInfoCard("下周今天", ctx.nextWeek, Color(0xFFE8F5E9)).invoke(this)
                ctx.createInfoCard("日期差值", ctx.daysDiff, Color(0xFFE8F5E9)).invoke(this)

                // 4. 日期属性
                ctx.createSectionTitle("日期属性", Color(0xFF03DAC5)).invoke(this)
                ctx.createInfoCard("闰年判断", ctx.isLeapYear, Color(0xFFFCE4EC)).invoke(this)
                ctx.createInfoCard("星期几", ctx.dayOfWeek, Color(0xFFFCE4EC)).invoke(this)
                ctx.createInfoCard("年内天数", ctx.dayOfYear, Color(0xFFFCE4EC)).invoke(this)

                // 5. 格式化
                ctx.createSectionTitle("格式化", Color(0xFF03DAC5)).invoke(this)
                ctx.createInfoCard("ISO 8601 格式", ctx.isoFormat, Color(0xFFF3E5F5)).invoke(this)
                ctx.createInfoCard("自定义格式", ctx.customFormat, Color(0xFFF3E5F5)).invoke(this)

                // 刷新按钮
                View {
                    attr {
                        margin(16f)
                        padding(16f)
                        backgroundColor(Color(0xFF6200EE))
                        borderRadius(12f)
                        allCenter()
                    }
                    event {
                        click {
                            ctx.updateAllDateTimeInfo()
                        }
                    }
                    Text {
                        attr {
                            text("刷新时间")
                            fontSize(18f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                }

                // API 说明
                ctx.createSectionTitle("API 说明", Color(0xFF03DAC5)).invoke(this)
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
                                
                                • Clock.System.now() - 获取当前时刻
                                • TimeZone.currentSystemDefault() - 系统时区
                                • Instant.toLocalDateTime() - 转换为本地时间
                                • LocalDate.plus/minus() - 日期加减
                                • LocalDate.daysUntil() - 计算日期差
                                • DayOfWeek - 星期枚举
                                • LocalDate.dayOfYear - 年内第几天
                                
                                支持的时区：
                                • UTC, Asia/Shanghai, America/New_York
                                • Asia/Tokyo, Europe/London 等
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
                
                // 标签
                Text {
                    attr {
                        text(label)
                        fontSize(14f)
                        fontWeightBold()
                        color(Color(0xFF666666))
                    }
                }
                
                // 值
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
}
