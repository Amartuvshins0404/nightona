rootProject.name = "file-operations"

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

includeBuild("../../../libs/sdk-java") {
    dependencySubstitution {
        substitute(module("io.nightona:sdk-java")).using(project(":"))
    }
}
