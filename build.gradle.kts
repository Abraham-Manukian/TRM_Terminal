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

dependencies {
    implementation(compose.desktop.currentOs)

    implementation("com.fazecast:jSerialComm:2.10.4") // для работы с COM-портами

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.darkrockstudios:mpfilepicker:1.0.0")

    implementation("org.jetbrains.compose.runtime:runtime:1.5.12")
    implementation("org.jetbrains.compose.runtime:runtime-saveable:1.5.12")
    implementation("org.jetbrains.compose.material:material:1.5.12")

    //Иконки
    implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.5.12")





    testImplementation(kotlin("test"))
}
compose.desktop {
    application {
        mainClass = "MainKt" // убедись, что твой main находится в этом файле
    }
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}