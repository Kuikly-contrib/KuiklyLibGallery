# kLottieView 跨端接入使用文档

本文档详细介绍如何在 Kuikly 项目中接入跨端 Lottie 动画组件（kLottieView），覆盖 **Android、iOS、HarmonyOS** 三端。

## 目录

1. [概述](#概述)
2. [跨端层配置](#跨端层配置)
3. [Android 端配置](#android-端配置)
4. [iOS 端配置](#ios-端配置)
5. [HarmonyOS 端配置](#harmonyos-端配置)
6. [跨端代码使用](#跨端代码使用)
7. [API 参考](#api-参考)
8. [各平台特性差异](#各平台特性差异)
9. [资源放置规范](#资源放置规范)
10. [常见问题与解决方案](#常见问题与解决方案)
11. [项目结构参考](#项目结构参考)

---

## 概述

kLottieView 是 Kuikly 框架提供的**跨端 Lottie 动画组件**，基于各平台原生 Lottie 库实现：

| 平台 | 底层实现 |
|------|----------|
| **Android** | [airbnb/lottie-android](https://github.com/airbnb/lottie-android) |
| **iOS** | [airbnb/lottie-ios](https://github.com/airbnb/lottie-ios) |
| **HarmonyOS** | [@ohos/lottie](https://ohpm.openharmony.cn/#/cn/detail/@ohos%2Flottie) |

### 支持的特性

| 特性类型 | 支持项 | Android | iOS | HarmonyOS |
|---------|--------|:-------:|:---:|:---------:|
| **属性** | `src` | ✅ | ✅ | ✅ |
| **属性** | `imagePath` | ✅ | ✅ | ✅ |
| **属性** | `autoPlay` | ✅ | ✅ | ✅ |
| **属性** | `loop` | ✅ | ✅ | ✅ |
| **方法** | `play` | ✅ | ✅ | ✅ |
| **方法** | `pause` | ✅ | ✅ | ✅ |
| **方法** | `resume` | ✅ | ✅ | ✅ |
| **方法** | `progress` | ✅ | ✅ | ✅ |
| **事件** | `onAnimationUpdate` | ⚠️ | ⚠️ | ✅ |
| **事件** | `onAnimationRepeat` | ⚠️ | ⚠️ | ✅ |
| **事件** | `onAnimationComplete` | ⚠️ | ⚠️ | ✅ |
| **事件** | `onAnimationLoaded` | ⚠️ | ⚠️ | ✅ |
| **事件** | `onAnimationLoadFailed` | ⚠️ | ⚠️ | ✅ |

> ⚠️ 表示该平台支持该事件，但回调精度和时机可能与 HarmonyOS 端有差异（例如 `onAnimationUpdate` 的回调频率可能不同，`onAnimationRepeat` 在某些循环模式下可能不触发）。

---

## 跨端层配置

### Android/iOS 端 (build.gradle.kts)

在 `shared/build.gradle.kts` 的 `commonMain` 中添加 kLottieView 依赖：

```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                // ... 其他依赖
                
                // kLottieView - Lottie 动画组件
                implementation("com.tencent.kuiklybase:kLottieView:1.0.0")
            }
        }
    }
}
```

### HarmonyOS 端 (build.ohos.gradle.kts)

在 `shared/build.ohos.gradle.kts` 中为 `ohosArm64Main` 添加专用依赖：

```kotlin
kotlin {
    // ohosArm64 专用依赖配置
    val ohosArm64Main by sourceSets.getting {
        dependencies {
            // kLottieView - Lottie 动画组件 (仅 Ohos)
            implementation("com.tencent.kuiklybase:kLottieView:1.0.0-ohos")
        }
    }
}
```

---

## Android 端配置

### 1. 添加原生依赖

在 `androidApp/build.gradle.kts` 中添加：

```kotlin
dependencies {
    // kLottieView - Lottie 动画组件
    implementation("com.tencent.kuiklybase:kLottieViewAndroid:1.0.0")
    implementation("com.airbnb.android:lottie:6.4.0")
}
```

### 2. 组件注册

在 `KuiklyRenderActivity` 或 `KuiklyRenderViewBaseDelegatorDelegate` 实现类中注册：

```kotlin
import com.kuikly.lottieview_android.AndroidLottieView

class KuiklyRenderActivity : AppCompatActivity(), KuiklyRenderViewBaseDelegatorDelegate {

    override fun registerExternalRenderView(kuiklyRenderExport: IKuiklyRenderExport) {
        super.registerExternalRenderView(kuiklyRenderExport)
        with(kuiklyRenderExport) {
            // 注册 Lottie 动画组件
            renderViewExport(AndroidLottieView.COMPONENT_NAME, { context ->
                AndroidLottieView(context)
            })
        }
    }
    
    // ... 其他代码
}
```

### 3. 资源放置

Android 端的 Lottie JSON 文件放在 `assets` 目录下：

```
shared/
└── src/
    └── commonMain/
        └── assets/
            └── common/
                └── chatbot.json  # Lottie JSON 文件
```

> ⚠️ 需要确保 `build.gradle.kts` 中配置了 assets 资源目录：
> ```kotlin
> android {
>     sourceSets {
>         named("main") {
>             assets.srcDirs("src/commonMain/assets")
>         }
>     }
> }
> ```

---

## iOS 端配置

### 1. 添加原生依赖

在 `iosApp/Podfile` 中添加：

```ruby
target 'iosApp' do
  use_frameworks!
  platform :ios, '14.1'
  
  # ... 其他依赖
  
  # kLottieView - Lottie 动画组件
  pod 'KuiklyLottieView', :git => 'https://github.com/Kuikly-contrib/KuiklyLottieView.git', :tag => '1.0.0'
end
```

### 2. 安装依赖

```bash
cd iosApp
pod install
```

### 3. 组件注册

iOS 端通过 CocoaPods 集成 `KuiklyLottieView` 后，组件会**自动注册**，无需手动在原生代码中添加注册逻辑。

### 4. 资源放置

iOS 端的 Lottie JSON 文件通过 CocoaPods 自动打包：

在 `shared/build.gradle.kts` 的 cocoapods 配置中添加：

```kotlin
cocoapods {
    // ... 其他配置
    
    // 确保 assets 资源被打包
    extraSpecAttributes["resources"] = "['src/commonMain/assets/**']"
}
```

资源文件放置位置与 Android 相同：

```
shared/
└── src/
    └── commonMain/
        └── assets/
            └── common/
                └── chatbot.json
```

---

## HarmonyOS 端配置

### 1. 添加原生依赖

#### 1.1 添加 Lottie 库依赖

在 `ohosApp/entry/oh-package.json5` 中添加：

```json5
{
  "dependencies": {
    "@kuikly-open/render": "2.7.0",
    "@ohos/lottie": "2.0.11",
    "kLottieViewOhos": "file:../kLottieViewOhos",
    // ... 其他依赖
  }
}
```

#### 1.2 配置本地模块

在 `ohosApp/build-profile.json5` 的 `modules` 数组中添加：

```json5
{
  "modules": [
    // ... 其他模块
    {
      "name": "kLottieViewOhos",
      "srcPath": "./kLottieViewOhos",
      "targets": [
        {
          "name": "default",
          "applyToProducts": ["default"]
        }
      ]
    }
  ]
}
```

### 2. 安装依赖

```bash
cd ohosApp
/Applications/DevEco-Studio.app/Contents/tools/ohpm/bin/ohpm install --all
```

### 3. 组件注册

在 `KuiklyViewDelegate.ets` 中注册：

```typescript
import {
  IKuiklyViewDelegate,
  KRRenderViewExportCreator
} from '@kuikly-open/render';
import { KTLottieViewImpl } from 'kLottieViewOhos';

export class KuiklyViewDelegate extends IKuiklyViewDelegate {
  getCustomRenderViewCreatorRegisterMap(): Map<string, KRRenderViewExportCreator> {
    const map: Map<string, KRRenderViewExportCreator> = new Map();
    // 注册 LottieView 组件
    map.set(KTLottieViewImpl.VIEW_NAME, () => new KTLottieViewImpl());
    return map;
  }
}
```

### 4. 资源放置

⚠️ **HarmonyOS 端资源路径规则**

鸿蒙端的 Lottie JSON 文件必须放在 `resources/rawfile/` 目录下：

```
ohosApp/
└── entry/
    └── src/
        └── main/
            └── resources/
                └── rawfile/
                    └── common/          # ← 与 src 属性的路径对应
                        └── chatbot.json # ← Lottie JSON 文件
```

> **重要**：跨端代码中 `src("common/chatbot.json")` 的路径必须与 `rawfile` 目录下的相对路径一致。

---

## 跨端代码使用

### 基础用法

```kotlin
import com.kuikly.kuiklylottie.LottieView

LottieView {
    attr {
        size(300f, 300f)
        src("common/chatbot.json")  // 资源相对路径
        autoPlay(true)
        loop(true)
    }
}
```

### 完整用法（带事件）

```kotlin
import com.kuikly.kuiklylottie.LottieView

LottieView {
    attr {
        size(300f, 300f)
        src("common/chatbot.json")
        autoPlay(true)
        loop(true)
    }
    event {
        onAnimationLoaded {
            println("Lottie 动画加载完成")
        }
        onAnimationLoadFailed {
            println("Lottie 动画加载失败")
        }
        onAnimationUpdate { progress ->
            println("动画进度: $progress")
        }
        onAnimationRepeat {
            println("动画循环播放")
        }
        onAnimationComplete {
            println("动画播放完成")
        }
    }
}
```

### 控制播放

```kotlin
var lottieRef: LottieView? = null

LottieView {
    lottieRef = this
    attr {
        size(300f, 300f)
        src("common/chatbot.json")
        autoPlay(false)  // 关闭自动播放
        loop(false)
    }
}

// 播放
lottieRef?.play()

// 暂停
lottieRef?.pause()

// 恢复播放
lottieRef?.resume()

// 跳转到指定进度 (0.0 ~ 1.0)
lottieRef?.progress(0.5f)
```

---

## API 参考

### 属性 (Attributes)

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `src` | String | - | Lottie JSON 文件路径 |
| `imagePath` | String | - | 图片资源路径（用于包含图片的 Lottie 动画）|
| `autoPlay` | Boolean | `true` | 是否自动播放 |
| `loop` | Boolean | `true` | 是否循环播放 |

### 方法 (Methods)

| 方法名 | 参数 | 说明 |
|--------|------|------|
| `play()` | - | 从头开始播放 |
| `pause()` | - | 暂停播放 |
| `resume()` | - | 恢复播放 |
| `progress(ratio: Float)` | `ratio`: 0.0 ~ 1.0 | 跳转到指定进度 |

### 事件 (Events)

| 事件名 | 回调参数 | 说明 |
|--------|----------|------|
| `onAnimationLoaded` | - | 动画资源加载完成 |
| `onAnimationLoadFailed` | - | 动画资源加载失败 |
| `onAnimationUpdate` | `{ "value": Float }` | 动画播放进度更新（0.0 ~ 1.0）|
| `onAnimationRepeat` | - | 动画循环播放一次 |
| `onAnimationComplete` | - | 动画播放完成（非循环模式）|

---

## 各平台特性差异

### 功能对比

| 特性 | Android | iOS | HarmonyOS |
|------|---------|-----|-----------|
| **全部属性** | ✅ | ✅ | ✅ |
| **全部方法** | ✅ | ✅ | ✅ |
| **全部事件** | ⚠️ 部分 | ⚠️ 部分 | ✅ 完整 |
| **事件回调精度** | 中 | 中 | 高 |

### 底层库版本

| 平台 | 底层库 | 版本 |
|------|--------|------|
| Android | com.airbnb.android:lottie | 6.4.0 |
| iOS | lottie-ios (via KuiklyLottieView) | 4.x（CocoaPods 自动管理） |
| HarmonyOS | @ohos/lottie | 2.0.11 |

---

## 资源放置规范

### 总览

| 平台 | 资源目录 | 示例路径 |
|------|----------|----------|
| **Android** | `shared/src/commonMain/assets/` | `assets/common/chatbot.json` |
| **iOS** | `shared/src/commonMain/assets/` | `assets/common/chatbot.json` |
| **HarmonyOS** | `ohosApp/entry/src/main/resources/rawfile/` | `rawfile/common/chatbot.json` |

### Android & iOS（共用资源）

```
shared/
└── src/
    └── commonMain/
        └── assets/
            └── common/
                └── chatbot.json
```

### HarmonyOS（独立资源）

```
ohosApp/
└── entry/
    └── src/
        └── main/
            └── resources/
                └── rawfile/
                    └── common/
                        └── chatbot.json
```

> ⚠️ **注意**：HarmonyOS 端的 rawfile 资源**不能通过文件系统直接访问**，必须使用 `resourceManager.getRawFileContent()` API。kLottieViewOhos 模块已实现此逻辑。

---

## 构建命令

### Android/iOS 端

```bash
./gradlew :shared:build
```

### HarmonyOS 端

```bash
./gradlew :shared:build -c settings.ohos.gradle.kts
```

---

## 常见问题与解决方案

### 通用问题

#### 1. 动画不显示

**可能原因**：
- 资源文件路径不正确
- 未注册组件
- 依赖未正确添加

**排查步骤**：
1. 检查 `src` 属性的路径是否与实际文件路径一致
2. 确认组件已在原生端注册
3. 确认跨端层和原生层都添加了正确的依赖

---

### Android 端问题

#### 1. 找不到 AndroidLottieView 类

**解决方案**：确保添加了 kLottieViewAndroid 依赖：
```kotlin
implementation("com.tencent.kuiklybase:kLottieViewAndroid:1.0.0")
```

---

### iOS 端问题

#### 1. pod install 失败

**解决方案**：
```bash
cd iosApp
pod repo update
pod install
```

---

### HarmonyOS 端问题

#### 1. 动画不显示，日志显示 "file load failed"

**日志示例**：
```
dir:/data/storage/el1/bundle/entry/resources/resfile not exist
file load failed
```

**原因**：Lottie JSON 文件路径不正确，或使用了旧的文件系统 API。

**解决方案**：
1. 确认文件放置在 `resources/rawfile/` 目录下
2. 确认 `src` 属性的路径与 rawfile 目录下的相对路径一致
3. 使用最新版本的 kLottieViewOhos 模块

#### 2. 模块依赖错误："Inconsistent Dep Names"

**解决方案**：确保本地模块的 `oh-package.json5` 中 `name` 字段与 entry 的依赖名称一致：

```json5
// kLottieViewOhos/oh-package.json5
{
  "name": "kLottieViewOhos",  // ← 必须一致
}
```

#### 3. 模块找不到："File Not Found"

**解决方案**：
```bash
cd ohosApp
rm -rf .hvigor oh-package-lock.json5 entry/oh-package-lock.json5 oh_modules entry/oh_modules
/Applications/DevEco-Studio.app/Contents/tools/ohpm/bin/ohpm install --all
```

---

## 项目结构参考

```
KuiklyProject/
├── shared/
│   ├── build.gradle.kts           # ← Android/iOS 跨端依赖
│   ├── build.ohos.gradle.kts      # ← HarmonyOS 跨端依赖
│   └── src/
│       └── commonMain/
│           ├── kotlin/
│           │   └── LottieDemoPage.kt
│           └── assets/
│               └── common/
│                   └── chatbot.json   # ← Android/iOS 资源
│
├── androidApp/
│   ├── build.gradle.kts               # ← Android 原生依赖
│   └── src/
│       └── main/
│           └── java/.../
│               └── KuiklyRenderActivity.kt  # ← 组件注册
│
├── iosApp/
│   └── Podfile                        # ← iOS 原生依赖
│
├── ohosApp/
│   ├── build-profile.json5            # ← 配置本地模块
│   ├── entry/
│   │   ├── oh-package.json5           # ← HarmonyOS 依赖
│   │   └── src/
│   │       └── main/
│   │           ├── ets/
│   │           │   └── kuikly/
│   │           │       └── KuiklyViewDelegate.ets  # ← 组件注册
│   │           └── resources/
│   │               └── rawfile/
│   │                   └── common/
│   │                       └── chatbot.json  # ← HarmonyOS 资源
│   │
│   └── kLottieViewOhos/               # ← 本地模块
│       └── src/main/ets/kuikly/
│           └── KTLottieView.ets
│
├── settings.gradle.kts                # ← Android/iOS 构建配置
└── settings.ohos.gradle.kts           # ← HarmonyOS 构建配置
```

---

## 快速接入清单

### ✅ Android 端

- [ ] `shared/build.gradle.kts` 添加 `kLottieView:1.0.0` 依赖
- [ ] `androidApp/build.gradle.kts` 添加 `kLottieViewAndroid:1.0.0` 和 `lottie:6.4.0` 依赖
- [ ] 在 `KuiklyRenderActivity` 中注册 `AndroidLottieView`
- [ ] 将 Lottie JSON 文件放入 `shared/src/commonMain/assets/` 目录

### ✅ iOS 端

- [ ] `shared/build.gradle.kts` 添加 `kLottieView:1.0.0` 依赖
- [ ] `iosApp/Podfile` 添加 `pod 'KuiklyLottieView'`（需指定 `:git` 和 `:tag` 参数）
- [ ] 执行 `pod install`
- [ ] 确保 cocoapods 配置了 `extraSpecAttributes["resources"]`

### ✅ HarmonyOS 端

- [ ] `shared/build.ohos.gradle.kts` 添加 `kLottieView:1.0.0-ohos` 依赖
- [ ] `ohosApp/entry/oh-package.json5` 添加 `@ohos/lottie` 和 `kLottieViewOhos` 依赖
- [ ] `ohosApp/build-profile.json5` 配置 kLottieViewOhos 模块
- [ ] 在 `KuiklyViewDelegate.ets` 中注册 `KTLottieViewImpl`
- [ ] 将 Lottie JSON 文件放入 `resources/rawfile/` 目录
- [ ] 执行 `ohpm install --all`

---

## 版本历史

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| 1.0.0 | 2026-02-09 | 初始版本，支持三端接入 |

---

## 参考资料

- [Kuikly 官方文档](https://kuikly.io)
- [Lottie Android](https://github.com/airbnb/lottie-android)
- [Lottie iOS](https://github.com/airbnb/lottie-ios)
- [@ohos/lottie](https://ohpm.openharmony.cn/#/cn/detail/@ohos%2Flottie)
- [鸿蒙 ResourceManager API](https://developer.harmonyos.com/cn/docs/documentation/doc-references-V5/js-apis-resource-manager-V5)
