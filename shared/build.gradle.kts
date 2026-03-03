import com.tencent.kuikly.gradle.config.KuiklyConfig

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization") version "2.0.21"
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("maven-publish")
    id("com.tencent.kuikly-open.kuikly")
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
        extraSpecAttributes["resources"] = "['src/commonMain/assets/**']"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.tencent.kuikly-open:core:${Version.getKuiklyVersion()}")
                implementation("com.tencent.kuikly-open:core-annotations:${Version.getKuiklyVersion()}")
                // Collection 依赖
                implementation("androidx.collection:collection:1.4.0-KBA-001")
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
                implementation("com.tencent.kuiklyx-open:coroutines:1.5.0-2.0.21")
                // lifecycle 依赖
                implementation("androidx.lifecycle:lifecycle-common:2.8.0-KBA-011")
                implementation("androidx.lifecycle:lifecycle-runtime:2.8.0-KBA-011")
                implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.0-KBA-011")
                // 网络库
                implementation("com.tencent.kuiklybase:network:0.0.4")
                // jce
                implementation("com.tencent.kuiklybase:jce:1.7.12-2.0.21")
                // kLottieView - Lottie 动画组件
                implementation("com.tencent.kuiklybase:kLottieView:1.0.0")
                // mmkvKotlin - 跨端 KV 存储组件 (排除其自带的 mmkv 传递依赖)
                implementation("com.tencent.kuiklybase:mmkvKotlin:1.1.2")
                // kuiklyx-bridge - 统一插件路由组件
                implementation("com.tencent.kuiklybase:shared_bridge:1.0.1-2.0.21")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
val androidMain by getting {
            dependencies {
                api("com.tencent.kuikly-open:core-render-android:${Version.getKuiklyVersion()}")
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

// 配置 KSP
ksp {
    allWarningsAsErrors = false
}

android {
    namespace = "com.example.kuiklylibgallery.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
        targetSdk = 34
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

// Kuikly 插件配置
configure<KuiklyConfig> {



}

// 删除 kspCommonMainKotlinMetadata 生成的 KuiklyCoreEntry.kt，因为它包含平台特定代码
// 只保留各平台目录下的版本
tasks.configureEach {
    if (name == "kspCommonMainKotlinMetadata") {
        doLast {
            val problematicFile = file("build/generated/ksp/metadata/commonMain/kotlin/KuiklyCoreEntry.kt")
            if (problematicFile.exists()) {
                problematicFile.delete()
                logger.lifecycle("Deleted problematic KuiklyCoreEntry.kt from commonMain (after KSP)")
            }
        }
    }
    // 在所有编译任务开始前也检查删除（双重保险）
    if (name.startsWith("compileKotlin")) {
        doFirst {
            val problematicFile = file("build/generated/ksp/metadata/commonMain/kotlin/KuiklyCoreEntry.kt")
            if (problematicFile.exists()) {
                problematicFile.delete()
                logger.lifecycle("Deleted problematic KuiklyCoreEntry.kt from commonMain (before compile)")
            }
        }
    }
}