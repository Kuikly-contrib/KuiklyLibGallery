package com.example.kuiklylibgallery


import androidx.collection.MutableIntList
import androidx.collection.MutableObjectList
import androidx.collection.mutableIntListOf
import androidx.collection.mutableObjectListOf
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View

/**
 * 测试 collection 1.4.0-KBA-001 依赖库的简单页面
 */
@Page("CollectionTestPage")
internal class CollectionTestPage : Pager() {

    // 测试结果
    var testResult: String by observable("")

    override fun createEvent(): ComposeEvent = ComposeEvent()

    override fun body(): ViewBuilder = {
        attr {
            backgroundColor(Color.WHITE)
            flexDirectionColumn()
            padding(20f)
        }

        // 标题
        Text {
            attr {
                text("Collection 1.4.0-KBA-001 测试页面")
                fontSize(20f)
                fontWeightBold()
                color(Color.BLACK)
                marginBottom(20f)
            }
        }

        // 运行测试
        val result = this@CollectionTestPage.runCollectionTest()
        View {
            attr {
                backgroundColor(Color(0xFFE3F2FD))
                padding(16f)
                borderRadius(8f)
                marginBottom(16f)
            }
            Text {
                attr {
                    text(result)
                    fontSize(14f)
                    color(Color.BLACK)
                }
            }
        }

        // 说明
        Text {
            attr {
                text("如果上面显示测试结果，说明 collection 1.4.0-KBA-001 依赖库加载成功！")
                fontSize(12f)
                color(Color.GRAY)
                marginTop(10f)
            }
        }
    }

    /**
     * 运行 collection 库的测试
     */
    private fun runCollectionTest(): String {
        val results = StringBuilder()
        results.appendLine("=== Collection 库测试 ===\n")

        try {
            // 测试 MutableIntList
            results.appendLine("1. 测试 MutableIntList:")
            val intList: MutableIntList = mutableIntListOf(1, 2, 3, 4, 5)
            intList.add(6)
            results.appendLine("   创建 IntList: $intList")
            results.appendLine("   大小: ${intList.size}")
            results.appendLine("   第一个元素: ${intList[0]}")
            results.appendLine("   ✅ MutableIntList 测试通过\n")

            // 测试 MutableObjectList
            results.appendLine("2. 测试 MutableObjectList:")
            val objectList: MutableObjectList<String> = mutableObjectListOf("Hello", "World", "Kuikly")
            objectList.add("Test")
            results.appendLine("   创建 ObjectList: $objectList")
            results.appendLine("   大小: ${objectList.size}")
            results.appendLine("   包含 'Hello': ${objectList.contains("Hello")}")
            results.appendLine("   ✅ MutableObjectList 测试通过\n")

            results.appendLine("=== 所有测试通过! ===")
            results.appendLine("\n依赖库版本: collection 1.4.0-KBA-001")

        } catch (e: Exception) {
            results.appendLine("❌ 测试失败: ${e.message}")
        }

        return results.toString()
    }
}
