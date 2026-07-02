plugins {
    application
}

group = "io.nightona.examples"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("io.nightona:sdk-java")
}

application {
    mainClass.set("io.nightona.examples.FileOperations")
}
