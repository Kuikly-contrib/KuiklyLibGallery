pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/")
        }
    }
}

dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://mirrors.tencent.com/repository/maven/kuikly-open/")
        }
        maven {
            url = uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/")
        }
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

rootProject.name = "KuiklyLibGallery"
include(":androidApp")
include(":shared")
//include(":shared-dynamic")
include(":h5App")
include(":miniApp")