plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.example.kuiklylibgallery"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.kuiklylibgallery"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":shared"))

    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.appcompat:appcompat:1.3.1")

    implementation("com.squareup.picasso:picasso:2.71828")

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // kLottieView - Lottie 动画组件
    implementation("com.tencent.kuiklybase:kLottieViewAndroid:1.0.0")
    implementation("com.airbnb.android:lottie:6.4.0")

    // MMKV - KV 存储组件
    implementation("com.tencent:mmkv:2.0.1")

    // kuiklybase-bridge - 统一插件路由组件
    implementation("com.tencent.kuiklybase:bridge:1.0.1-2.0.21")
}