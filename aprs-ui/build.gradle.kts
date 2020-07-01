plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android-extensions")
    kotlin("kapt")
}

android {
    compileSdkVersion(29)
    ndkVersion = "21.3.6528147"
    defaultConfig {
        minSdkVersion(19)
        targetSdkVersion(29)
        versionCode = if(project.hasProperty("versionCode")) project.property("versionCode").toString().toInt() else 1
        versionName = if(project.hasProperty("versionName")) project.property("versionName").toString() else "SNAPSHOT"
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
        }
    }
    lintOptions {
        tasks.lint {
            enabled = false
        }
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
    androidExtensions {
        isExperimental = true
    }
}

dependencies {
    implementation(project(":aprs-android"))

    implementation(kotlin("stdlib"))
    implementation(Coroutines.android)

    implementation(AndroidX.AppCompat.core)
    implementation(AndroidX.RecyclerView.core)

    implementation(Google.Material.core)

    implementation(Groupie.core)
    implementation(Groupie.extensions)
}
