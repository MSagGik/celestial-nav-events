import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    `maven-publish`
}

group = "io.github.msaggik"
version = libs.versions.versionName.get()

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(19)
}

tasks.test {
    enabled = false
}

tasks.register("releaseBuild") {
    dependsOn("build")
    doFirst {
        println("Running release build (tests are skipped).")
    }
    doLast {
        println("✅ Release build complete (tests skipped = ${!tasks.test.get().enabled}).")
    }
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val dokkaJavadoc = tasks.named<DokkaTask>("dokkaJavadoc")

tasks.register<Jar>("javadocJar") {
    dependsOn(dokkaJavadoc)
    archiveClassifier.set("javadoc")
    from(dokkaJavadoc.get().outputDirectory)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            groupId = project.group.toString()
            artifactId = "celestial-nav-events"
            version = project.version.toString()

            pom {
                name.set("celestial-nav-events")
                description.set("Library for calculation of Sun and Moon astronomical events.")
                url.set("https://github.com/MSagGik/celestial-nav-events")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }

                developers {
                    developer {
                        id.set("MSagGik")
                        email.set("dev.saggik@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/MSagGik/celestial-nav-events.git")
                    developerConnection.set("scm:git:ssh://github.com:MSagGik/celestial-nav-events.git")
                    url.set("https://github.com/MSagGik/celestial-nav-events")
                }
            }
        }
    }
}