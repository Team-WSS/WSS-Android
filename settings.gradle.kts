rootProject.name = "Websoso"

pluginManagement {
    includeBuild("build-logic")
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
        maven("https://jitpack.io")
        maven("https://devrepo.kakao.com/nexus/content/groups/public/")
    }
}

include(":app")

include(
    ":core:resource",
    ":core:common",
    ":core:designsystem",
    ":core:auth",
    ":core:auth-kakao",
    ":core:network",
    ":core:datastore",
    ":core:database",
)

include(
    ":data:account",
    ":data:library",
)

include(
    ":domain:library",
)

include(
    ":feature:signin",
    ":feature:library",
)
include(":data:feed")
