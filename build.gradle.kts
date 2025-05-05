plugins {
    kotlin("jvm") version "2.1.20"
    id("org.jetbrains.compose") version ("1.7.3")
    id("org.jetbrains.kotlin.plugin.compose") version ("2.1.20")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}
val voyager_vresion = "1.0.1"

dependencies {
    implementation(compose.desktop.currentOs)

    implementation("com.fazecast:jSerialComm:2.10.4") // для работы с COM-портами

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.darkrockstudios:mpfilepicker:1.0.0")

    // Корутины
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3") // Main диспетчер для Swing/Desktop

    implementation("org.jetbrains.compose.runtime:runtime:1.5.12")
    implementation("org.jetbrains.compose.runtime:runtime-saveable:1.5.12")
    implementation("org.jetbrains.compose.material:material:1.5.12")

    //Иконки
    implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.5.12")

    //Переходы экарнов
    implementation("cafe.adriel.voyager:voyager-navigator:$voyager_vresion")
    implementation("cafe.adriel.voyager:voyager-core:$voyager_vresion")
    implementation("cafe.adriel.voyager:voyager-screenmodel:$voyager_vresion")
    implementation("cafe.adriel.voyager:voyager-navigator-desktop:$voyager_vresion")
    implementation("cafe.adriel.voyager:voyager-bottom-sheet-navigator-desktop:$voyager_vresion")
    implementation("cafe.adriel.voyager:voyager-transitions-desktop:$voyager_vresion")
    implementation("cafe.adriel.voyager:voyager-tab-navigator-desktop:$voyager_vresion")

    // Koin Core
    implementation("io.insert-koin:koin-core:4.0.4")

    // Koin для Compose Desktop
    implementation("io.insert-koin:koin-compose:4.0.4")

    testImplementation(kotlin("test"))
}
compose.desktop {
    application {
        mainClass = "MainKt"
    }
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}