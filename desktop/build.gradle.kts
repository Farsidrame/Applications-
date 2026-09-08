plugins {
    kotlin("multiplatform") version "2.0.20"
    id("org.jetbrains.compose") version "1.6.11"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20"
}

kotlin {
    jvm("jvm") {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.components.resources)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.1")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.example.desktop.MainKt"

        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )
            packageName = "PharmaDirect"
            packageVersion = "2.4.0"
            description = "Plateforme de commande et gestion de médicaments en ligne sécurisée"
            copyright = "© 2026 PharmaDirect Sénégal. Tous droits réservés."
            vendor = "PharmaDirect"

            windows {
                menuGroup = "PharmaDirect"
                upgradeUuid = "a13f35dc-e723-444f-83fd-2ac46450a0b0"
                iconFile.set(project.file("src/jvmMain/resources/icon.ico"))
            }

            macOS {
                bundleID = "com.pharmadirect.desktop"
                iconFile.set(project.file("src/jvmMain/resources/icon.icns"))
            }

            linux {
                shortcut = true
                iconFile.set(project.file("src/jvmMain/resources/icon.png"))
            }
        }
    }
}
