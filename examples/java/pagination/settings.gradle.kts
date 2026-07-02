rootProject.name = "pagination"

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
