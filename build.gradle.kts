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
    flatDir {
        dirs("libs")
    }
}

val voyager_vresion = "1.0.1"

dependencies {
    implementation(compose.desktop.currentOs)

    implementation("ch.qos.logback:logback-classic:1.4.6")
    implementation("ch.qos.logback:logback-core:1.4.6")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.2")
    implementation("io.github.microutils:kotlin-logging:1.8.3")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.9.1")
    implementation("org.apache.logging.log4j:log4j-api:2.9.1")
    implementation("org.apache.logging.log4j:log4j-core:2.9.1")
    implementation("com.fazecast:jSerialComm:2.9.2")
//    implementation("org.slf4j:slf4j-simple:2.0.9")

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

    // для kserialpooler:
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    // простой SLF4J‐бэкенд (чтобы логи не падали из‐за отсутствия binding)

    //Для запросов и ответов Modbus
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

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