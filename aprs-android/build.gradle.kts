plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdkVersion(31)
    ndkVersion = "21.3.6528147"

    defaultConfig {
        minSdkVersion(14)
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/src/main/schema")
            }
        }
        setTestInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
    }
    sourceSets {
        // Adds exported schema location as test app assets.
        getByName("androidTest").assets.srcDirs("$projectDir/src/main/schema")
    }


    lintOptions {
        tasks.lint {
            enabled = false
        }
    }
    externalNativeBuild {
        cmake {
            version = "3.10.2"
            path = File("${projectDir}/src/main/cpp/CMakeLists.txt")
        }
    }
    packagingOptions {
        exclude("META-INF/kotlinx-coroutines-core.kotlin_module")
    }
}

dependencies {
    implementation(projects.kotlinExtensions)
    api(libraries.coroutines.core)

    androidTestImplementation(libraries.androidx.test.runner)
    androidTestImplementation(libraries.androidx.test.core)

    api(libraries.karps.parser)
    api(libraries.karps.client)
    api(libraries.karps.structures)

    api(libraries.kimchi.logger)

    implementation(libraries.dagger.core)
    kapt(libraries.dagger.compiler)

    implementation(ThreeTenBp.noTzDb)

    implementation(libraries.androidx.room.runtime)
    implementation(libraries.androidx.room.ktx)
    androidTestImplementation(libraries.androidx.room.testing)
    kapt(libraries.androidx.room.compiler)
}
