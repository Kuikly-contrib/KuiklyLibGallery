# KuiklyLibGallery

[KuiklyUI](https://github.com/Tencent-TDS/KuiklyUI) 第三方组件示例集合 —— 一站式了解 Kuikly 生态中各类第三方库的使用方式。

## 简介

本项目是一个基于 [KuiklyUI](https://github.com/Tencent-TDS/KuiklyUI) 框架构建的第三方组件 Demo 集合，涵盖了序列化、网络请求、状态管理、动画、存储等多个常用领域的库示例。每个示例页面均提供了可交互的演示和详细的 API 说明，帮助开发者快速上手。

## 支持平台

| 平台 | 状态 |
|------|------|
| Android | ✅ |
| iOS | ✅ |
| HarmonyOS (Ohos) | ✅ |

## 组件示例列表

| 组件名称 | 库 / 依赖 | 说明 | 源码 |
|---------|-----------|------|------|
| **AtomicFu** | `kotlinx-atomicfu:0.23.2-KBA-001` | 原子操作与并发控制，演示 `atomic`、`incrementAndGet`、`compareAndSet` 等 API | [AtomicfuTestPage.kt](shared/src/commonMain/kotlin/com/example/kuiklylibgallery/AtomicfuTestPage.kt) |
| **Collection** | `androidx.collection:1.4.0-KBA-001` | 高性能集合类库，演示 `MutableIntList`、`MutableObjectList` 等用法 | [CollectionTestPage.kt](shared/src/commonMain/kotlin/com/example/kuiklylibgallery/CollectionTestPage.kt) |
| **DateTime** | `kotlinx-datetime:0.6.0-RC.2-KBA-003` | 日期时间处理，演示时区转换、日期计算、格式化等功能 | [DateTimeDemoPage.kt](shared/src/commonMain/kotlin/com/example/kuiklylibgallery/DateTimeDemoPage.kt) |
| **Serialization** | `kotlinx-serialization-json:1.7.1-KBA-003` | Kotlin 序列化，演示对象 / 列表的 JSON 序列化与反序列化 | [SerializationDemo.kt](shared/src/commonMain/kotlin/com/example/kuiklylibgallery/SerializationDemo.kt) |
| **Okio** | `okio:3.9.10-KBA-001` | 高效 I/O 操作库，演示 Buffer、ByteString、编码转换、哈希、GZIP 压缩等 | [OkioDemoPage.kt](shared/src/commonMain/kotlin/com/example/kuiklylibgallery/OkioDemoPage.kt) |
| **Coroutines** | `kotlinx-coroutines-core:1.8.0-KBA-002` | Kotlin 协程异步编程，演示 Kuikly 内建协程 (`lifecycleScope`) 和 `suspendCoroutine` 挂起函数 | [CoroutineDemoPage.kt](shared/src/commonMain/kotlin/com/example/kuiklylibgallery/CoroutineDemoPage.kt) |
| **Lifecycle** | `androidx.lifecycle:2.8.0-KBA-011` | ViewModel 与生命周期管理，演示 `ViewModel`、`ViewModelStore`、`ViewModelProvider` 等 API | [LifecycleDemoPage.kt](shared/src/commonMain/kotlin/com/example/kuiklylibgallery/LifecycleDemoPage.kt) |
| **Network (Ktor)** | `com.tencent.kuiklybase:network:0.0.4` | 跨端网络请求库（底层 Android/iOS 使用 Ktor，HarmonyOS 使用 libcurl），演示 GET / POST / 自定义 Header / 错误处理 | [NetworkDemoPage.kt](shared/src/commonMain/kotlin/com/example/kuiklylibgallery/NetworkDemoPage.kt) |
| **JsonMate** | `com.tencent.kuiklybase:jsonmate:1.4.3` | 基于 KSP 的 JSON 反序列化代码生成，演示 `@FromJSONObject`、`@JSONField`、嵌套对象与列表解析 | [JsonMateDemoPage.kt](shared/src/commonMain/kotlin/com/example/kuiklylibgallery/JsonMateDemoPage.kt) |
| **JCE** | `com.tencent.kuiklybase:jce:1.7.12-2.0.21` | 腾讯高效二进制序列化协议，演示基础类型、JceStruct 结构体、嵌套结构体、List / Map 序列化 | [JceDemoPage.kt](shared/src/commonMain/kotlin/com/example/kuiklylibgallery/JceDemoPage.kt) |
| **Lottie** | `com.tencent.kuiklybase:kLottieView:1.0.0` | 跨端 Lottie 动画组件，演示动画加载、播放控制（play / pause / resume）、进度控制、循环开关 | [LottieDemoPage.kt](shared/src/commonMain/kotlin/com/example/kuiklylibgallery/LottieDemoPage.kt) |
| **MMKV** | `com.tencent.kuiklybase:mmkvKotlin:1.1.2` | 高性能 KV 存储组件，演示多类型读写、删除、查询、自定义 ID 实例、加密存储、批量操作 | [MMKVDemoPage.kt](shared/src/commonMain/kotlin/com/example/kuiklylibgallery/MMKVDemoPage.kt) |
| **Bridge** | `com.tencent.kuiklybase:shared_bridge:1.0.1-2.0.21` | kuiklyx-bridge 统一插件路由，演示通过 `Bridge.getPlugin` 调用原生侧插件方法 | [BridgeDemoPage.kt](shared/src/commonMain/kotlin/com/example/kuiklylibgallery/bridge/BridgeDemoPage.kt) |
| **ViewModel** | `com.tencent.kuiklybase:viewmodel:1.0.5-2.0.21` | kuiklyx-viewmodel 生命周期管理，演示 `viewModelStore` 委托、生命周期回调（onResumed / onPaused / onCleared）、单例验证 | [ViewModelDemoPage.kt](shared/src/commonMain/kotlin/com/example/kuiklylibgallery/ViewModelDemoPage.kt) |
| **Redux** | `com.tencent.kuiklybase:redux:1.0.5-2.0.21` | kuiklyx-redux 全局状态管理，演示 State / Action / Reducer / Store / UseSelector 单向数据流 | [ReduxDemoPage.kt](shared/src/commonMain/kotlin/com/example/kuiklylibgallery/ReduxDemoPage.kt) |
| **ImageAdapter** | Kuikly Core 内建 | Image 适配器基准测试，验证 base64 / assets / http / gif 等多种图片源的加载、capInsets 拉伸、Canvas drawImage | [ImageAdapterBenchmarks.kt](shared/src/commonMain/kotlin/com/example/kuiklylibgallery/ImageAdapterBenchmarks.kt) |

## 项目结构

```
KuiklyLibGallery/
├── shared/                          # KMP 跨端共享代码
│   ├── build.gradle.kts             # Android / iOS 构建配置与依赖
│   ├── build.ohos.gradle.kts        # HarmonyOS 构建配置与依赖
│   └── src/commonMain/
│       ├── kotlin/.../
│       │   ├── RouterPage.kt        # 首页路由（组件入口列表）
│       │   ├── base/                # 基础类（BasePager、BridgeModule 等）
│       │   ├── bridge/              # Bridge 相关示例
│       │   └── *DemoPage.kt         # 各组件示例页面
│       └── assets/                  # 共享资源文件（Lottie JSON、图片等）
├── androidApp/                      # Android 宿主工程
├── iosApp/                          # iOS 宿主工程
├── ohosApp/                         # HarmonyOS 宿主工程
├── docs/                            # 接入文档
│   ├── Lottie_Integration_Guide.md  # kLottieView 跨端接入指南
│   └── KMM_Network_HarmonyOS_Integration_Guide.md  # 网络库鸿蒙端接入指南
└── build.gradle.kts                 # 根构建配置
```

## 环境要求

- **Kotlin**: 2.0.21
- **Android Studio**: 推荐 2024.2.1+（Gradle JDK 需设为 JDK 17）
- **Xcode**: 用于 iOS 构建
- **DevEco Studio**: 5.1.0+（用于 HarmonyOS 构建）
- **JDK**: 17

## 快速开始

### 运行 Android App

1. 使用 Android Studio 打开项目根目录，同步 Gradle
2. 选择 `androidApp` 配置，运行

### 运行 iOS App

1. 进入 `iosApp` 目录，执行 `pod install --repo-update`
2. 使用 Android Studio 同步项目，选择 `iOSApp` 配置运行；或在 Xcode 中打开 `iosApp/iosApp.xcworkspace` 运行

### 运行 HarmonyOS App

1. 在项目根目录执行鸿蒙构建脚本：
   ```bash
   cd ohosApp && ./runOhosApp.sh
   ```
2. 使用 DevEco Studio 打开 `ohosApp` 目录，同步并运行

## 接入文档

针对部分组件提供了详细的跨端接入指南：

- [kLottieView 跨端接入文档](docs/Lottie_Integration_Guide.md) — Lottie 动画组件 Android / iOS / HarmonyOS 三端接入
- [KMM Network 鸿蒙端接入指南](docs/KMM_Network_HarmonyOS_Integration_Guide.md) — 网络库在 HarmonyOS 端的配置与使用

## 相关链接

- [KuiklyUI GitHub](https://github.com/Tencent-TDS/KuiklyUI) — Kuikly 框架源码
- [Kuikly 官网](https://framework.tds.qq.com/) — 官方文档与介绍

## License

本项目仅用于 KuiklyUI 第三方组件的学习和演示用途。
