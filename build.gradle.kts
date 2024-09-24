plugins {
    kotlin("jvm") version "2.0.20"
    application
}

group = "org.hildan.puzzle"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("org.hildan.puzzle.MainKt")
}
