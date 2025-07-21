import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform") version libs.versions.kotlin.get()
    id("com.android.library") version libs.versions.agp.get()
    id("convention.publication")
}

group = libs.versions.libGroup.get()
version = libs.versions.libVersion.get()

repositories {
    maven("https://mirrors.tencent.com/nexus/repository/maven-public/")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
    google()
    gradlePluginPortal()
    mavenCentral()
}

kotlin {
    // 发布android target
    androidTarget {
        // 发布安卓需要额外配置这两个变体
        publishLibraryVariants("debug", "release")
    }

    jvm {
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }
    js {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("partialJsonParser")
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "partialJsonParser.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    macosArm64()
    macosX64()
    linuxArm64()
    linuxX64()
    mingwX64()
    iosArm64()
    iosX64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

android {
    namespace = "com.funnysaltyfish.partialjsonparser"

    defaultConfig {
        compileSdk = 35
        minSdk = 17
    }
}