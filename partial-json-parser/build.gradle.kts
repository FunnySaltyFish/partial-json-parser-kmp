import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform") version "2.2.0"
    id("com.android.library") version "8.11.1"
    id("convention.publication")
}

group = Config.libGroup
version = Config.libVersion

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
        outputModuleName.set("composeApp")
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
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
    namespace = Config.packageName

    defaultConfig {
        compileSdk = 35
        minSdk = 17
    }
}

afterEvaluate {
    // 设置所有的 publish 任务 需要在 sign 之后
    // 我也不知道为什么需要手动这么写，但是 Gradle 一直报错，只好按着报错一点点常识
    // 最后写出了这一堆。。。
    val signTasks = tasks.filter { it.name.startsWith("sign") && it.name != "sign"}

    // project.logger.warn(signTasks.joinToString { it.name + ", " })
    tasks.configureEach {
        // project.logger.warn("task name: $name")
        if (!name.startsWith("publish")) return@configureEach
        if (name == "publish") return@configureEach

        signTasks.forEach { signTask ->
            this.dependsOn(signTask)
        }
    }
}

