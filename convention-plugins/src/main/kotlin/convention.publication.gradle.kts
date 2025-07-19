import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import java.util.Properties

plugins {
    id("com.vanniktech.maven.publish")
}

// Stub secrets to let the project sync and build without the publication values set up
ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.secretKeyRingFile"] = null
ext["signing.key"] = null
ext["mavenCentralUsername"] = null
ext["mavenCentralPassword"] = null

// Grabbing secrets from local.properties file or from environment variables, which could be used on CI
val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply {
            load(it)
        }
    }.onEach { (name, value) ->
        ext[name.toString()] = value
    }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
    ext["signing.key"] = System.getenv("GPG_KEY_CONTENTS")
    ext["mavenCentralUsername"] = System.getenv("MAVEN_CENTRAL_USERNAME")
    ext["mavenCentralPassword"] = System.getenv("MAVEN_CENTRAL_PASSWORD")
}

fun getExtraString(name: String) = ext[name]?.toString()

mavenPublishing {
    // Configure publishing to Maven Central
    publishToMavenCentral()

    // Configure signing
    signAllPublications()

    // Configure what to publish - for Kotlin Multiplatform projects
    configure(KotlinMultiplatform(
        javadocJar = JavadocJar.Empty(),
        sourcesJar = true,
        androidVariantsToPublish = listOf("debug", "release")
    ))

    // Configure publication coordinates and metadata
    coordinates(
        groupId = "io.github.funnysaltyfish",
        artifactId = project.name,
        version = project.version.toString()
    )

    // Configure POM metadata
    pom {
        name.set("partial-json-parser-kmp")
        description.set("Parse incomplete JSON (like what ChatGPT generates in stream mode) in Kotlin, obtain as much as possible information fastly.")
        url.set("https://github.com/FunnySaltyFish/partial-json-parser-kmp")

        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://opensource.org/licenses/Apache-2.0")
            }
        }

        developers {
            developer {
                id.set("FunnySaltyFish")
                name.set("FunnySaltyFish")
                email.set("funnysaltyfish@foxmail.com")
            }
        }

        scm {
            url.set("https://github.com/FunnySaltyFish/partial-json-parser-kmp")
            connection.set("scm:git:git://github.com/FunnySaltyFish/partial-json-parser-kmp.git")
            developerConnection.set("scm:git:ssh://git@github.com/FunnySaltyFish/partial-json-parser-kmp.git")
        }
    }
}

// 输出调试信息
val mavenCentralUsername = getExtraString("mavenCentralUsername")
val mavenCentralPassword = getExtraString("mavenCentralPassword")
val signingKeyId = getExtraString("signing.keyId")
val signingPassword = getExtraString("signing.password")
val signingKey = getExtraString("signing.key")

println("Signing Key ID: $signingKeyId, Signing Password: ${signingPassword?.length}, Signing Key: ${signingKey?.length}")
println("mavenCentral Username: $mavenCentralUsername, mavenCentral Password: ${mavenCentralPassword?.length}")