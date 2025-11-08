import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.compose") version "1.6.0"
}

group = "com.tomer"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("com.hierynomus:smbj:0.14.0")
    implementation("com.h2database:h2:2.3.232")
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "SambaBackup"
            packageVersion = "1.0.0"
        }
    }
}

val mainClass = "MainKt" // replace it!

tasks {
    register("fatJar", Jar::class.java) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes("Main-Class" to mainClass)
        }

        // Exclude signature files from dependencies
        from(configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it
            else zipTree(it).matching {
                exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
            }
        })

        val sourcesMain = sourceSets.main.get()
        from(sourcesMain.output)
    }
}