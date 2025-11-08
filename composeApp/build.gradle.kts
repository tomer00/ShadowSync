import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
//            implementation(libs.androidx.lifecycle.viewmodelCompose)
//            implementation(libs.androidx.lifecycle.runtimeCompose)


            implementation(libs.slf4j)
            implementation(libs.samba.smbj)
            implementation(libs.h2.db)
            implementation(libs.exposed.core)
            implementation(libs.exposed.jdbc)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}


compose.desktop {
    application {
        mainClass = "com.tomer.backup.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.tomer.backup"
            packageVersion = "1.0.0"
        }
    }
}


val mainClass = "com.tomer.backup.MainKt" // The main class for your application

tasks {
    // Fat JAR creation task
    register("fatJar", Jar::class.java) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes(
                "Main-Class" to mainClass
            )
        }

        // Add runtimeClasspath dependencies, but exclude any manifest-related files
        from(
            configurations["jvmRuntimeClasspath"]
                .map {
                    if (it.isDirectory) it else zipTree(it).matching {
                        exclude(
                            "META-INF/*.SF",
                            "META-INF/*.DSA",
                            "META-INF/*.RSA",
                        ) // Exclude conflicting manifest files from dependencies
                    }
                }) {
            exclude("META-INF/MANIFEST.MF")
        }

        // Add compiled classes from the jvmMain source set
        val jvmMain = sourceSets["jvmMain"]
        from(jvmMain.output)
    }
}
