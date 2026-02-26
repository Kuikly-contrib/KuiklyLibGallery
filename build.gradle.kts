plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("7.4.2").apply(false)
    id("com.android.library").version("7.4.2").apply(false)
    kotlin("android").version("2.0.21").apply(false)
    kotlin("multiplatform").version("2.0.21").apply(false)
    id("com.google.devtools.ksp").version("2.0.21-1.0.25").apply(false)
    id("com.tencent.kuiklybase.resource.generator").version("0.0.1").apply(false)

}

buildscript {
    dependencies {
        classpath(BuildPlugin.kuikly)
    }
}