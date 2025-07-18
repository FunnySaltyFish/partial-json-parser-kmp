import java.util.Properties

plugins {
    `maven-publish`
    signing
}

// Stub secrets to let the project sync and build without the publication values set up
ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.secretKeyRingFile"] = null
ext["signing.key"] = null
ext["ossrhUsername"] = null
ext["ossrhPassword"] = null

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
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

fun getExtraString(name: String) = ext[name]?.toString()

publishing {
    // Configure maven central repository
    repositories {
        maven {
            name = "sonatype"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = getExtraString("ossrhUsername")
                password = getExtraString("ossrhPassword")
            }
        }
    }

    // Configure all publications
    publications.withType<MavenPublication> {
        // Stub javadoc.jar artifact
        artifact(javadocJar.get())

        // Provide artifacts information requited by Maven Central
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
}

// Signing artifacts. Signing.* extra properties values will be used
signing {
    val signingKeyId = getExtraString("signing.keyId")
    val signingPassword = getExtraString("signing.password")
    val signingSecretKeyRingFile = getExtraString("signing.secretKeyRingFile")
    val signingKey = getExtraString("signing.key")

    if (signingKey != null && signingKeyId != null && signingPassword != null) {
        // Use in-memory signing (for CI/CD)
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    } else if (signingKeyId != null && signingPassword != null && signingSecretKeyRingFile != null) {
        // Use key ring file (for local development)
        // This will use the default GPG configuration
    }

    sign(publishing.publications)
}