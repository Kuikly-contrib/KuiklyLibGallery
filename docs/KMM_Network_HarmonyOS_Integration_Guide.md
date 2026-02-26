# KMM Network 鸿蒙端接入使用指南

[TOC]

## 简介

KMM Network 是基于 Kotlin Multiplatform 技术构建的跨平台网络请求解决方案，支持 Android、iOS 及 HarmonyOS 三大移动端平台。

### 技术实现

| 平台 | 底层实现 |
|------|----------|
| Android | Ktor |
| iOS | Ktor |
| HarmonyOS | libcurl（通过 pbcurlwrapper 封装） |

> **说明**：鸿蒙端底层使用开源库 libcurl 作为网络请求引擎，Android/iOS 端目前暂时使用 Ktor 实现，后续会统一使用 libcurl。

---

## 快速开始

### 接入概览

```
┌─────────────────────────────────────────────────────────────────┐
│  Step 1: 添加 Kotlin 依赖                                        │
│  Step 2: 声明网络权限                                            │
│  Step 3: 添加 Native 库文件 (.so)                                │
│  Step 4: 配置 CMakeLists.txt                                    │
│  Step 5: 初始化网络模块                                          │
└─────────────────────────────────────────────────────────────────┘
```

---

## 详细接入步骤

### Step 1: 添加 Kotlin 依赖

在模块 `build.gradle.kts`（KMM 共享模块）和 `build.ohos.gradle.kts` 中添加依赖：

```kotlin
// Maven 仓库配置
repositories {
    maven {
        url = uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/")
    }
}

// 添加依赖
dependencies {
    implementation("com.tencent.kuiklybase:network:0.0.4")
}
```

---

### Step 2: 声明网络权限

在鸿蒙工程的 `entry/src/main/module.json5` 中添加网络权限声明：

```json5
{
  "module": {
    // ... 其他配置 ...
    
    "requestPermissions": [
      {
        "name": "ohos.permission.INTERNET",
        "reason": "$string:internet_permission_reason",
        "usedScene": {
          "abilities": ["EntryAbility"],
          "when": "inuse"
        }
      },
      {
        "name": "ohos.permission.GET_NETWORK_INFO",
        "reason": "$string:network_info_permission_reason",
        "usedScene": {
          "abilities": ["EntryAbility"],
          "when": "inuse"
        }
      },
      {
        "name": "ohos.permission.GET_WIFI_INFO",
        "reason": "$string:wifi_info_permission_reason",
        "usedScene": {
          "abilities": ["EntryAbility"],
          "when": "inuse"
        }
      }
    ]
  }
}
```

同时在 `entry/src/main/resources/base/element/string.json` 中添加权限说明字符串：

```json
{
  "string": [
    {
      "name": "internet_permission_reason",
      "value": "应用需要访问网络以获取数据"
    },
    {
      "name": "network_info_permission_reason",
      "value": "应用需要获取网络状态信息"
    },
    {
      "name": "wifi_info_permission_reason",
      "value": "应用需要获取 WiFi 信息"
    }
  ]
}
```

---

### Step 3: 添加 Native 库文件

鸿蒙端需要额外的 Native 库来支持网络请求功能。

#### 3.1 下载库文件

下载以下两个 `.so` 文件：

| 库文件 | 说明 | 下载链接 |
|--------|------|----------|
| `libpbcurlwrapper.so` | curl 网络请求封装库 | [下载](https://drive.weixin.qq.com/s?k=AJEAIQdfAAoOl8vBYTAbQAJgZ2AA8) |
| `libopenssl.so` | SSL/TLS 支持库 | [下载](https://drive.weixin.qq.com/s?k=AJEAIQdfAAohnXGmhhAbQAJgZ2AA8) |

#### 3.2 放置库文件

将下载的 `.so` 文件放置到项目的以下目录：

```
ohosApp/
└── entry/
    └── libs/
        └── arm64-v8a/
            ├── libpbcurlwrapper.so   ← 放在这里
            └── libopenssl.so         ← 放在这里
```

> **注意**：如果 `libs/arm64-v8a/` 目录不存在，请手动创建。

#### 3.3 目录结构确认

完成后，你的项目结构应该类似：

```
ohosApp/
├── entry/
│   ├── libs/
│   │   └── arm64-v8a/
│   │       ├── libpbcurlwrapper.so   ✅
│   │       └── libopenssl.so         ✅
│   ├── src/
│   │   └── main/
│   │       ├── cpp/
│   │       │   └── CMakeLists.txt    ← 下一步配置
│   │       └── module.json5          ← 已配置权限
│   └── oh-package.json5
└── build-profile.json5
```

---

### Step 4: 配置 CMakeLists.txt

在 `ohosApp/entry/src/main/cpp/CMakeLists.txt` 文件中添加库的链接配置。

#### 4.1 添加库定义

在 CMakeLists.txt 文件**末尾**（`target_link_libraries` 之前）添加以下内容：

```cmake
# ========== KMM Network Native 库配置 ==========

# pbcurlwrapper - curl 网络请求封装库
add_library(pbcurlwrapper SHARED IMPORTED)
set_target_properties(pbcurlwrapper
    PROPERTIES
    IMPORTED_LOCATION ${NATIVERENDER_ROOT_PATH}/../../../libs/${OHOS_ARCH}/libpbcurlwrapper.so
)

# openssl - SSL/TLS 支持库
add_library(openssl SHARED IMPORTED)
set_target_properties(openssl
    PROPERTIES
    IMPORTED_LOCATION ${NATIVERENDER_ROOT_PATH}/../../../libs/${OHOS_ARCH}/libopenssl.so
)
```

#### 4.2 修改链接配置

找到 `target_link_libraries` 行，在末尾添加 `pbcurlwrapper openssl`：

**修改前**：
```cmake
target_link_libraries(entry PUBLIC libace_napi.z.so libhilog_ndk.z.so kuikly_shared kuikly_render)
```

**修改后**：
```cmake
target_link_libraries(entry PUBLIC libace_napi.z.so libhilog_ndk.z.so kuikly_shared kuikly_render pbcurlwrapper openssl)
```

#### 4.3 完整 CMakeLists.txt 示例

以下是一个完整的 CMakeLists.txt 配置示例供参考：

```cmake
cmake_minimum_required(VERSION 3.13)
project(entry)

set(NATIVERENDER_ROOT_PATH ${CMAKE_CURRENT_SOURCE_DIR})

# 添加你的其他源文件
add_library(entry SHARED
    ${NATIVERENDER_ROOT_PATH}/napi_init.cpp
    # ... 其他源文件
)

# 包含头文件目录
target_include_directories(entry PUBLIC
    ${NATIVERENDER_ROOT_PATH}
)

# ========== Kuikly 相关配置 ==========
# （这部分通常由 Kuikly 插件自动生成）

# ========== KMM Network Native 库配置 ==========

# pbcurlwrapper - curl 网络请求封装库
add_library(pbcurlwrapper SHARED IMPORTED)
set_target_properties(pbcurlwrapper
    PROPERTIES
    IMPORTED_LOCATION ${NATIVERENDER_ROOT_PATH}/../../../libs/${OHOS_ARCH}/libpbcurlwrapper.so
)

# openssl - SSL/TLS 支持库
add_library(openssl SHARED IMPORTED)
set_target_properties(openssl
    PROPERTIES
    IMPORTED_LOCATION ${NATIVERENDER_ROOT_PATH}/../../../libs/${OHOS_ARCH}/libopenssl.so
)

# ========== 链接库 ==========
target_link_libraries(entry PUBLIC
    libace_napi.z.so
    libhilog_ndk.z.so
    kuikly_shared
    kuikly_render
    pbcurlwrapper    # KMM Network
    openssl          # KMM Network
)
```

---

### Step 5: 初始化网络模块

在应用启动时初始化网络模块。建议在 `EntryAbility` 或应用初始化代码中调用。

#### Kotlin 代码示例

```kotlin
import com.pitertech.network.IVBPBLog
import com.pitertech.network.VBTransportInitConfig
import com.pitertech.network.VBTransportInitHelper

// 创建日志实现
val logImpl = object : IVBPBLog {
    override fun d(tag: String?, content: String?) {
        println("[$tag] DEBUG: $content")
    }

    override fun i(tag: String?, content: String?) {
        println("[$tag] INFO: $content")
    }

    override fun e(tag: String?, content: String?, throwable: Throwable?) {
        println("[$tag] ERROR: $content")
        throwable?.printStackTrace()
    }
}

// 初始化配置
val config = VBTransportInitConfig().apply {
    this.logImpl = logImpl
}

// 执行初始化
VBTransportInitHelper.init(config)
```

#### 在 Kuikly 项目中的初始化位置

如果你使用的是 Kuikly 框架，可以在 `BasePager` 或应用启动的第一个页面中初始化：

```kotlin
class MyApp : BasePager() {
    
    init {
        // 在应用第一次加载时初始化
        initNetwork()
    }
    
    private fun initNetwork() {
        val logImpl = object : IVBPBLog {
            override fun d(tag: String?, content: String?) { /* ... */ }
            override fun i(tag: String?, content: String?) { /* ... */ }
            override fun e(tag: String?, content: String?, throwable: Throwable?) { /* ... */ }
        }
        
        val config = VBTransportInitConfig()
        config.logImpl = logImpl
        VBTransportInitHelper.init(config)
    }
}
```

---

## 使用示例

初始化完成后，你可以使用 KMM Network 进行网络请求。

### GET 请求

```kotlin
import com.pitertech.network.VBTransportService

VBTransportService.get(
    url = "https://api.example.com/users",
    params = mapOf("page" to "1", "limit" to "10"),
    headers = mapOf("Authorization" to "Bearer token"),
    callback = { response, error ->
        if (error == null) {
            println("请求成功: $response")
        } else {
            println("请求失败: ${error.message}")
        }
    }
)
```

### POST 请求

```kotlin
import com.pitertech.network.VBTransportService

VBTransportService.post(
    url = "https://api.example.com/users",
    body = """{"name": "张三", "email": "zhangsan@example.com"}""",
    headers = mapOf(
        "Content-Type" to "application/json",
        "Authorization" to "Bearer token"
    ),
    callback = { response, error ->
        if (error == null) {
            println("请求成功: $response")
        } else {
            println("请求失败: ${error.message}")
        }
    }
)
```

### 更多示例

更多网络请求示例，请参考 `network/src/commonMain/service/VBTransportServiceTest.kt` 文件。

---

## 常见问题

### Q1: 编译时找不到 .so 文件

**错误信息**：
```
CMake Error: Cannot find source file: .../libs/arm64-v8a/libpbcurlwrapper.so
```

**解决方案**：
1. 确认 `.so` 文件已正确放置到 `entry/libs/arm64-v8a/` 目录
2. 确认文件名正确（区分大小写）
3. 检查 CMakeLists.txt 中的路径配置是否正确

### Q2: 运行时崩溃 - 找不到符号

**错误信息**：
```
java.lang.UnsatisfiedLinkError: dlopen failed: library "libpbcurlwrapper.so" not found
```

**解决方案**：
1. 确认 `.so` 文件架构正确（需要 arm64-v8a）
2. 确认 `target_link_libraries` 中已添加 `pbcurlwrapper openssl`
3. 清理并重新构建项目

### Q3: SSL/HTTPS 请求失败

**错误信息**：
```
SSL certificate problem: unable to get local issuer certificate
```

**解决方案**：
1. 确认 `libopenssl.so` 已正确放置
2. 确认 `openssl` 已添加到 `target_link_libraries`
3. 检查设备系统时间是否正确

### Q4: 模拟器运行失败

**说明**：
目前仅提供 `arm64-v8a` 架构的 Native 库，不支持 x86/x86_64 模拟器。请使用真机或 ARM64 模拟器进行测试。

---

## 注意事项

1. **架构支持**：目前仅支持 `arm64-v8a` 架构，真机调试需使用 ARM64 设备
2. **版本匹配**：请确保 `.so` 文件版本与 `network` 依赖版本匹配
3. **初始化时机**：网络模块需在使用前初始化，建议在应用启动时执行
4. **权限声明**：务必在 `module.json5` 中声明网络相关权限

---

## 接入检查清单

在完成接入后，请使用以下清单确认所有步骤：

- [ ] 已添加 `com.tencent.kuiklybase:network:0.0.4` 依赖
- [ ] 已在 `module.json5` 中声明网络权限
- [ ] 已下载 `libpbcurlwrapper.so` 和 `libopenssl.so`
- [ ] `.so` 文件已放置到 `entry/libs/arm64-v8a/` 目录
- [ ] 已在 CMakeLists.txt 中添加库定义（`add_library`）
- [ ] 已在 CMakeLists.txt 中修改 `target_link_libraries`
- [ ] 已在应用启动时调用 `VBTransportInitHelper.init()`
- [ ] 项目能够成功编译和运行

---

## 更新日志

请参阅 [ChangeLog](./changelog.md) 了解版本更新历史。

---

## 技术支持

如遇到接入问题，请通过以下方式获取帮助：
- 提交 GitHub Issue
- 联系技术支持邮箱

---

*文档最后更新：2026-02-04*
