plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21-KBA-010"
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("maven-publish")
    id("com.tencent.kuiklybase.jsonmate") version "1.4.3"
}

val KEY_PAGE_NAME = "pageName"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        publishLibraryVariants("release")
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            freeCompilerArgs = freeCompilerArgs + getCommonCompilerArgs()
            isStatic = true
            license = "MIT"
        }
    }

    ohosArm64 {
        binaries.sharedLib {
        }
        // mmkvKotlin 鸿蒙编译配置
        compilations.forEach {
            it.kotlinOptions.freeCompilerArgs += when {
                org.jetbrains.kotlin.konan.target.HostManager.hostIsMac -> listOf("-linker-options", "-lmmkv_c_wrapper -L${rootProject.projectDir}/ohosApp/entry/libs/arm64-v8a/")
                else -> throw RuntimeException("暂不支持")
            }
        }
    }

    // ohosArm64 专用依赖配置
    val ohosArm64Main by sourceSets.getting {
        dependencies {
            // kLottieView - Lottie 动画组件
            implementation("com.tencent.kuiklybase:kLottieView:1.0.0-ohos")
            // kuiklyx-bridge - 统一插件路由组件
            implementation("com.tencent.kuiklybase:shared_bridge-ohosarm64:1.0.1-2.0.21-KBA-010")
            // kuiklyx-viewmodel
            implementation("com.tencent.kuiklybase:viewmodel-ohosarm64:1.0.5-2.0.21-KBA-010")
            // kuiklyx-redux
            implementation("com.tencent.kuiklybase:redux-ohosarm64:1.0.5-2.0.21-KBA-010")
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.tencent.kuikly-open:core:${Version.getKuiklyOhosVersion()}")
                implementation("com.tencent.kuikly-open:core-annotations:${Version.getKuiklyOhosVersion()}")
                // Collection 依赖
                //implementation("androidx.collection:collection:1.4.0-KBA-001")
                // Serialization 依赖
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.1-KBA-003")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1-KBA-003")
                // AtomicFU 依赖
                implementation("org.jetbrains.kotlinx:atomicfu:0.23.2-KBA-001")
                // datetime 依赖
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0-RC.2-KBA-003")
                // okio 依赖
                implementation("com.squareup.okio:okio:3.9.10-KBA-001")
                // coroutine 依赖
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core"){
                    version{ strictly("1.8.0-KBA-002") }
                }
                // kuiklyx 协程库 用于切换到kuikly线程
                implementation("com.tencent.kuiklyx-open:coroutines:1.5.0-2.0.21-ohos")
                // lifecycle 依赖
                implementation("androidx.lifecycle:lifecycle-common:2.8.0-KBA-011")
                implementation("androidx.lifecycle:lifecycle-runtime:2.8.0-KBA-011")
                implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.0-KBA-011")
                // 网络库
                implementation("com.tencent.kuiklybase:network:0.0.4")
                // KmmResource 依赖
                implementation("com.tencent.kuiklybase:resource-core:0.0.1")
                implementation("com.tencent.kuiklybase:resource-compose:0.0.1")
                // jce
                implementation("com.tencent.kuiklybase:jce:1.7.13-2.0.21-KBA-010")
                // mmkvKotlin - 跨端 KV 存储组件
                implementation("com.tencent.kuiklybase:mmkvKotlin:1.1.2")
            }
        }
        val androidMain by getting {
            dependencies {
                api("com.tencent.kuikly-open:core-render-android:${Version.getKuiklyOhosVersion()}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

group = "com.example.kuiklylibgallery"
version = System.getenv("kuiklyBizVersion") ?: "1.0.0"

publishing {
    repositories {
        maven {
            credentials {
                username = System.getenv("mavenUserName") ?: ""
                password = System.getenv("mavenPassword") ?: ""
            }
            rootProject.properties["mavenUrl"]?.toString()?.let { url = uri(it) }
        }
    }
}

ksp {
    arg(KEY_PAGE_NAME, getPageName())
}

dependencies {
    compileOnly("com.tencent.kuikly-open:core-ksp:${Version.getKuiklyOhosVersion()}") {
        add("kspAndroid", this)
        add("kspIosArm64", this)
        add("kspIosX64", this)
        add("kspIosSimulatorArm64", this)
        add("kspOhosArm64", this)
    }
}

android {
    namespace = "com.example.kuiklylibgallery.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
        targetSdk = 30
    }
    sourceSets {
        named("main") {
            assets.srcDirs("src/commonMain/assets")
        }
    }
}

fun getPageName(): String {
    return (project.properties[KEY_PAGE_NAME] as? String) ?: ""
}

fun getCommonCompilerArgs(): List<String> {
    return listOf(
        "-Xallocator=std"
    )
}

fun getLinkerArgs(): List<String> {
    return listOf()
}