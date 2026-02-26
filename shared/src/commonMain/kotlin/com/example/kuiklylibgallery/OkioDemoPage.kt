package com.example.kuiklylibgallery

import com.example.kuiklylibgallery.base.BasePager
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import okio.*
import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.decodeHex
import okio.ByteString.Companion.encodeUtf8

/**
 * Okio 库使用示例页面
 * 展示 Okio 的各种 I/O 操作
 */
@Page("OkioDemoPage")
internal class OkioDemoPage : BasePager() {

    // 响应式状态
    private var bufferDemo by observable("")
    private var byteStringDemo by observable("")
    private var utf8Demo by observable("")
    private var base64Demo by observable("")
    private var hexDemo by observable("")
    private var md5Demo by observable("")
    private var sha256Demo by observable("")
    private var gzipDemo by observable("")
    private var bufferSizeDemo by observable("")
    private var readWriteDemo by observable("")

    private var isInitialized = false

    override fun created() {
        super.created()
    }

    /**
     * 演示所有 Okio 功能
     */
    private fun demonstrateOkioFeatures() {
        try {
            // 1. Buffer 基本操作
            demonstrateBuffer()
            
            // 2. ByteString 操作
            demonstrateByteString()
            
            // 3. 编码转换
            demonstrateEncoding()
            
            // 4. 哈希计算
            demonstrateHashing()
            
            // 5. 压缩操作
            demonstrateCompression()
            
            // 6. 读写操作
            demonstrateReadWrite()
            
        } catch (e: Exception) {
            bufferDemo = "错误: ${e.message}"
        }
    }

    /**
     * 演示 Buffer 操作
     */
    private fun demonstrateBuffer() {
        val buffer = Buffer()
        
        // 写入不同类型的数据
        buffer.writeUtf8("Hello Okio!")
        buffer.writeByte(0x0A) // 换行符
        buffer.writeInt(12345)
        buffer.writeLong(9876543210L)
        
        bufferSizeDemo = "Buffer 大小: ${buffer.size} 字节"
        
        // 读取数据
        val text = buffer.readUtf8Line()
        val intValue = buffer.readInt()
        val longValue = buffer.readLong()
        
        bufferDemo = """
            写入文本: Hello Okio!
            读取文本: $text
            写入整数: 12345
            读取整数: $intValue
            写入长整数: 9876543210
            读取长整数: $longValue
        """.trimIndent()
    }

    /**
     * 演示 ByteString 操作
     */
    private fun demonstrateByteString() {
        val text = "Okio ByteString Demo"
        val byteString = text.encodeUtf8()
        
        byteStringDemo = """
            原始文本: $text
            ByteString: $byteString
            大小: ${byteString.size} 字节
            转回文本: ${byteString.utf8()}
        """.trimIndent()
    }

    /**
     * 演示编码转换
     */
    private fun demonstrateEncoding() {
        val text = "Hello Okio! 你好世界!"
        val byteString = text.encodeUtf8()
        
        // UTF-8
        utf8Demo = """
            原始文本: $text
            UTF-8 字节数: ${byteString.size}
            UTF-8 解码: ${byteString.utf8()}
        """.trimIndent()
        
        // Base64
        val base64 = byteString.base64()
        val base64Decoded = base64.decodeBase64()
        base64Demo = """
            Base64 编码: $base64
            Base64 解码: ${base64Decoded?.utf8()}
        """.trimIndent()
        
        // Hex
        val hex = byteString.hex()
        val hexDecoded = hex.decodeHex()
        hexDemo = """
            Hex 编码: $hex
            Hex 解码: ${hexDecoded.utf8()}
        """.trimIndent()
    }

    /**
     * 演示哈希计算
     */
    private fun demonstrateHashing() {
        val text = "Hello Okio!"
        val byteString = text.encodeUtf8()
        
        // MD5
        val md5 = byteString.md5()
        md5Demo = """
            原始文本: $text
            MD5: ${md5.hex()}
        """.trimIndent()
        
        // SHA-256
        val sha256 = byteString.sha256()
        sha256Demo = """
            原始文本: $text
            SHA-256: ${sha256.hex()}
        """.trimIndent()
    }

    /**
     * 演示压缩操作
     */
    private fun demonstrateCompression() {
        val text = "Hello Okio! ".repeat(10) // 重复文本以便压缩
        val buffer = Buffer()
        buffer.writeUtf8(text)
        
        val originalSize = buffer.size
        
        // GZIP 压缩
        val compressedBuffer = Buffer()
        val gzipSink = GzipSink(compressedBuffer).buffer()
        gzipSink.writeUtf8(text)
        gzipSink.close()
        
        val compressedSize = compressedBuffer.size
        
        // GZIP 解压
        val decompressedBuffer = Buffer()
        val gzipSource = GzipSource(compressedBuffer).buffer()
        decompressedBuffer.writeAll(gzipSource)
        gzipSource.close()
        
        val decompressed = decompressedBuffer.readUtf8()
        
        gzipDemo = """
            原始大小: $originalSize 字节
            压缩后: $compressedSize 字节
            解压成功: ${decompressed == text}
        """.trimIndent()
    }

    /**
     * 演示读写操作
     */
    private fun demonstrateReadWrite() {
        val buffer = Buffer()
        
        // 写入各种数据类型
        buffer.writeUtf8("Okio 读写演示\n")
        buffer.writeDecimalLong(123456789L)
        buffer.writeUtf8("\n")
        buffer.writeHexadecimalUnsignedLong(0xABCDEF)
        buffer.writeUtf8("\n")
        
        // 读取数据
        val line1 = buffer.readUtf8Line()
        val line2 = buffer.readUtf8Line()
        val line3 = buffer.readUtf8Line()
        
        readWriteDemo = """
            第一行: $line1
            第二行: $line2
            第三行: $line3
            
            支持的操作:
            • writeUtf8/readUtf8
            • writeByte/readByte
            • writeInt/readInt
            • writeLong/readLong
            • writeDecimalLong
            • writeHexadecimalUnsignedLong
        """.trimIndent()
    }

    override fun body(): ViewBuilder {
        val ctx = this
        
        // 首次初始化数据
        if (!isInitialized) {
            demonstrateOkioFeatures()
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
                ctx.createSectionTitle("Okio 使用示例", Color(0xFF00BCD4)).invoke(this)

                // 1. Buffer 操作
                ctx.createSectionTitle("Buffer 操作", Color(0xFF03DAC5)).invoke(this)
                ctx.createInfoCard("Buffer 大小", ctx.bufferSizeDemo, Color(0xFFE0F7FA)).invoke(this)
                ctx.createCodeCard("Buffer 读写", ctx.bufferDemo, Color(0xFFE0F7FA)).invoke(this)

                // 2. ByteString 操作
                ctx.createSectionTitle("ByteString 操作", Color(0xFF03DAC5)).invoke(this)
                ctx.createCodeCard("ByteString", ctx.byteStringDemo, Color(0xFFE1F5FE)).invoke(this)

                // 3. 编码转换
                ctx.createSectionTitle("编码转换", Color(0xFF03DAC5)).invoke(this)
                ctx.createCodeCard("UTF-8", ctx.utf8Demo, Color(0xFFFFF9C4)).invoke(this)
                ctx.createCodeCard("Base64", ctx.base64Demo, Color(0xFFFFF9C4)).invoke(this)
                ctx.createCodeCard("Hex", ctx.hexDemo, Color(0xFFFFF9C4)).invoke(this)

                // 4. 哈希计算
                ctx.createSectionTitle("哈希计算", Color(0xFF03DAC5)).invoke(this)
                ctx.createCodeCard("MD5", ctx.md5Demo, Color(0xFFE8F5E9)).invoke(this)
                ctx.createCodeCard("SHA-256", ctx.sha256Demo, Color(0xFFE8F5E9)).invoke(this)

                // 5. 压缩操作
                ctx.createSectionTitle("GZIP 压缩", Color(0xFF03DAC5)).invoke(this)
                ctx.createCodeCard("压缩演示", ctx.gzipDemo, Color(0xFFFCE4EC)).invoke(this)

                // 6. 读写操作
                ctx.createSectionTitle("读写操作", Color(0xFF03DAC5)).invoke(this)
                ctx.createCodeCard("多种数据类型", ctx.readWriteDemo, Color(0xFFF3E5F5)).invoke(this)

                // 刷新按钮
                View {
                    attr {
                        margin(16f)
                        padding(16f)
                        backgroundColor(Color(0xFF00BCD4))
                        borderRadius(12f)
                        allCenter()
                    }
                    event {
                        click {
                            ctx.demonstrateOkioFeatures()
                        }
                    }
                    Text {
                        attr {
                            text("重新演示")
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
                                
                                Buffer 操作：
                                • writeUtf8() - 写入 UTF-8 文本
                                • readUtf8() - 读取 UTF-8 文本
                                • writeInt/Long/Byte - 写入数值
                                • readInt/Long/Byte - 读取数值
                                
                                ByteString 操作：
                                • encodeUtf8() - 字符串转 ByteString
                                • utf8() - ByteString 转字符串
                                • base64() - Base64 编码
                                • hex() - 十六进制编码
                                • md5() - MD5 哈希
                                • sha256() - SHA-256 哈希
                                
                                压缩操作：
                                • GzipSink - GZIP 压缩
                                • GzipSource - GZIP 解压
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
