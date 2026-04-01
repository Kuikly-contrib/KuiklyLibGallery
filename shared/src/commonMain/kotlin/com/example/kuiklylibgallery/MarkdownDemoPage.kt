package com.example.kuiklylibgallery

import com.example.kuiklylibgallery.base.BasePager
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.*
import com.tencent.kuiklybase.KuiklyMarkdown
import com.tencent.kuiklybase.config.MarkdownConfig
import com.tencent.kuiklybase.config.MarkdownColors
import com.tencent.kuikly.core.log.KLog

/**
 * KuiklyMarkdown Demo 页面
 * 演示 Markdown 渲染组件的各种语法支持和自定义配置
 */
@Page("MarkdownDemoPage", supportInLocal = true)
internal class MarkdownDemoPage : BasePager() {

    private var logOutput by observable("Markdown 渲染组件演示")

    // 演示用的 Markdown 内容
    private val markdownContent = """
# KuiklyMarkdown 演示

这是一个基于 Kuikly 跨端框架的 **Markdown 渲染组件** 演示页面。

---

## 文本样式

这是一段普通文本。支持 **粗体**、*斜体*、~~删除线~~ 和 `行内代码` 等样式。

也可以组合使用：***粗斜体*** 文本。

---

## 标题层级

### 三级标题
#### 四级标题
##### 五级标题
###### 六级标题

---

## 链接与图片

这是一个 [Kuikly 官网链接](https://kuikly.tencent.com)。

图片示例：

![Kotlin Logo](https://kotlinlang.org/docs/images/kotlin-logo.png)

---

## 代码块

行内代码：`println("Hello, Kuikly!")`

Kotlin 代码块：

```kotlin
fun main() {
    val greeting = "Hello, KuiklyMarkdown!"
    println(greeting)
    
    val numbers = listOf(1, 2, 3, 4, 5)
    numbers.filter { it % 2 == 0 }
           .map { it * it }
           .forEach { println(it) }
}
```

Python 代码块：

```python
def fibonacci(n):
    if n <= 1:
        return n
    return fibonacci(n - 1) + fibonacci(n - 2)

for i in range(10):
    print(f"F({i}) = {fibonacci(i)}")
```

---

## 列表

### 无序列表

- 支持 Android 平台
- 支持 iOS 平台
- 支持 HarmonyOS 平台
  - 鸿蒙 Next
  - 鸿蒙经典版
- 支持 Web 平台

### 有序列表

1. 添加依赖到 `build.gradle.kts`
2. 在页面中导入组件
3. 调用 `KuiklyMarkdown()` 方法
4. 自定义配置（可选）

---

## 引用

> Kuikly 是一个高性能的跨端 UI 框架，使用 Kotlin Multiplatform 构建。

> **嵌套引用示例：**
> 
> > 这是一段嵌套的引用文本。
> > 支持多层嵌套。

---

## 表格

| 特性 | Android | iOS | HarmonyOS |
|------|:-------:|:---:|:---------:|
| 标题渲染 | ✅ | ✅ | ✅ |
| 代码高亮 | ✅ | ✅ | ✅ |
| 表格支持 | ✅ | ✅ | ✅ |
| 图片渲染 | ✅ | ✅ | ✅ |

---

## 复选框

- [x] 基础文本渲染
- [x] 代码语法高亮
- [x] 表格支持
- [ ] 自定义主题
- [ ] 动画效果

---

## 分割线

上面是一条分割线 `---`，下面再来一条：

---

*感谢使用 KuiklyMarkdown！*
    """.trimIndent()

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr {
                backgroundColor(Color.WHITE)
            }

            // 导航栏
            RouterNavBar {
                attr {
                    title = "KuiklyMarkdown"
                }
            }

            // 主内容区域
            Scroller {
                attr {
                    flex(1f)
                }

                View {
                    attr {
                        flexDirection(FlexDirection.COLUMN)
                        padding(16f)
                    }

                    // 状态提示
                    View {
                        attr {
                            backgroundColor(Color(0xFFF0F7FF))
                            borderRadius(8f)
                            padding(12f)
                            marginBottom(16f)
                        }
                        Text {
                            attr {
                                text(ctx.logOutput)
                                fontSize(14f)
                                color(Color(0xFF1A73E8))
                            }
                        }
                    }

                    // Markdown 渲染区域
                    KuiklyMarkdown(
                        content = ctx.markdownContent,
                        config = MarkdownConfig(
                            colors = MarkdownColors(
                                linkColor = 0xFF1A73E8,
                                blockQuoteBar = 0xFF7B8CFA,
                                blockQuoteBackground = 0xFFF4F5FA,
                            ),
                            codeHighlightEnabled = true,
                            codeHighlightDarkTheme = false,
                            onLinkClick = { url ->
                                KLog.i("MarkdownDemo", "链接被点击: $url")
                                ctx.logOutput = "链接被点击: $url"
                            },
                        ),
                    )

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
}
