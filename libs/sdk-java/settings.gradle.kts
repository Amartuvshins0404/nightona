rootProject.name = "sdk-java"

includeBuild("../api-client-java") {
    name = "api-client"
    dependencySubstitution {
        substitute(module("io.nightona:api-client")).using(project(":"))
    }
}

includeBuild("../toolbox-api-client-java") {
    name = "toolbox-api-client"
    dependencySubstitution {
        substitute(module("io.nightona:toolbox-api-client")).using(project(":"))
    }
}
