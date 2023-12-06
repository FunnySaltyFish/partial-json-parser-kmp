plugins {
    kotlin("multiplatform") version "1.9.0"
    id("com.android.library") version "8.1.4"
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
    //发布android target
    androidTarget {
        //发布安卓需要额外配置这两个变体
        publishAllLibraryVariants()
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
    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
        hostOs == "Linux" && isArm64 -> linuxArm64("native")
        hostOs == "Linux" && !isArm64 -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }


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
        val nativeMain by getting
        val nativeTest by getting
    }

    sourceSets["commonTest"].dependencies {
        implementation(kotlin("test"))
    }

    // https://kotlinlang.org/docs/multiplatform-publish-lib.html#avoid-duplicate-publications
//    val publicationsFromMainHost =
//        listOf(jvm(), js(), androidTarget()).map { it.name } + "kotlinMultiplatform"
//    publishing {
//        publications {
//            matching { it.name in publicationsFromMainHost }.all {
//                val targetPublication = this@all
//                tasks.withType<AbstractPublishToMaven>()
//                    .matching { it.publication == targetPublication }
//                    .configureEach { onlyIf { findProperty("isMainHost") == "true" } }
//            }
//        }
//    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

android {
    namespace = Config.packageName

    defaultConfig {
        compileSdk = 34
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

