package com.example.kuiklylibgallery


import com.example.kuiklylibgallery.base.BasePager
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.log.KLog
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import kotlinx.atomicfu.atomic

@Page("AtomicFuDSLTestPage")
internal class AtomicfuTestPage : BasePager() {

    private val atomicCounter = atomic(0)
    private var displayValue by observable(0)
    private var logText by observable("点击按钮测试 AtomicFU")

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

                // 当前值显示
                View {
                    attr {
                        margin(16f)
                        padding(16f)
                        backgroundColor(Color(0xFFF5F5F5))
                        borderRadius(8f)
                    }
                    Text {
                        attr {
                            text("当前值: ${ctx.displayValue}")
                            fontSize(20f)
                            fontWeightBold()
                            color(Color.BLACK)
                        }
                    }
                }

                // 按钮区域
                View {
                    attr {
                        // marginHorizontal(16f)
                        flexDirectionRow()
                    }

                    // +1 按钮
                    View {
                        attr {
                            margin(4f)
                            padding(12f)
                            backgroundColor(Color(0xFF2196F3))
                            borderRadius(4f)
                        }
                        event {
                            click {
                                val newVal = ctx.atomicCounter.incrementAndGet()
                                ctx.displayValue = newVal
                                ctx.logText = "incrementAndGet -> $newVal"
                                KLog.i("AtomicFU", ctx.logText)
                            }
                        }
                        Text {
                            attr {
                                text("+1")
                                fontSize(14f)
                                color(Color.WHITE)
                            }
                        }
                    }

                    // -1 按钮
                    View {
                        attr {
                            margin(4f)
                            padding(12f)
                            backgroundColor(Color(0xFF2196F3))
                            borderRadius(4f)
                        }
                        event {
                            click {
                                val newVal = ctx.atomicCounter.decrementAndGet()
                                ctx.displayValue = newVal
                                ctx.logText = "decrementAndGet -> $newVal"
                                KLog.i("AtomicFU", ctx.logText)
                            }
                        }
                        Text {
                            attr {
                                text("-1")
                                fontSize(14f)
                                color(Color.WHITE)
                            }
                        }
                    }

                    // +10 按钮
                    View {
                        attr {
                            margin(4f)
                            padding(12f)
                            backgroundColor(Color(0xFF2196F3))
                            borderRadius(4f)
                        }
                        event {
                            click {
                                val newVal = ctx.atomicCounter.addAndGet(10)
                                ctx.displayValue = newVal
                                ctx.logText = "addAndGet(10) -> $newVal"
                                KLog.i("AtomicFU", ctx.logText)
                            }
                        }
                        Text {
                            attr {
                                text("+10")
                                fontSize(14f)
                                color(Color.WHITE)
                            }
                        }
                    }

                    // CAS 按钮
                    View {
                        attr {
                            margin(4f)
                            padding(12f)
                            backgroundColor(Color(0xFF4CAF50))
                            borderRadius(4f)
                        }
                        event {
                            click {
                                val success = ctx.atomicCounter.compareAndSet(0, 100)
                                ctx.displayValue = ctx.atomicCounter.value
                                ctx.logText = "CAS(0->100): ${if (success) "成功" else "失败"}"
                                KLog.i("AtomicFU", ctx.logText)
                            }
                        }
                        Text {
                            attr {
                                text("CAS(0->100)")
                                fontSize(14f)
                                color(Color.WHITE)
                            }
                        }
                    }

                    // 重置按钮
                    View {
                        attr {
                            margin(4f)
                            padding(12f)
                            backgroundColor(Color(0xFFFF5722))
                            borderRadius(4f)
                        }
                        event {
                            click {
                                ctx.atomicCounter.value = 0
                                ctx.displayValue = 0
                                ctx.logText = "重置为 0"
                                KLog.i("AtomicFU", ctx.logText)
                            }
                        }
                        Text {
                            attr {
                                text("重置")
                                fontSize(14f)
                                color(Color.WHITE)
                            }
                        }
                    }
                }

                // 日志区域
                View {
                    attr {
                        margin(16f)
                        padding(12f)
                        backgroundColor(Color(0xFFE8E8E8))
                        borderRadius(4f)
                    }
                    Text {
                        attr {
                            text("日志: ${ctx.logText}")
                            fontSize(14f)
                            color(Color(0xFF666666))
                        }
                    }
                }
            }
        }
    }
}