import com.android.build.gradle.BaseExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    }
}

subprojects {
    repositories {
        google()
        mavenCentral()
    }

    val isApp = name == "app"

    apply(plugin = if (isApp) "com.android.application" else "com.android.library")

    extensions.configure<BaseExtension> {
        compileSdkVersion(31)

        defaultConfig {
            if (isApp) {
                applicationId = "com.github.kr328.intent.qq"
            }

            minSdk = 26
            targetSdk = 31

            versionName = "2.0"
            versionCode = 200

            if (!isApp) {
                consumerProguardFiles("consumer-rules.pro")
            }
        }

        if (isApp) {
            packagingOptions {
                resources {
                    excludes.add("DebugProbesKt.bin")
                }
            }
        }

        buildTypes {
            named("release") {
                isMinifyEnabled = isApp
                isShrinkResources = isApp
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
    }
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}