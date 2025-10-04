pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Repository per Zebra EMDK
        maven {
            url = uri("https://zebratech.jfrog.io/artifactory/emdk-maven-release/")
        }
    }
}

rootProject.name = "ShoppingListScanner"
include(":app")