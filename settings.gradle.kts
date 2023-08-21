rootProject.name = "shoestore"
include("store-app")
include("store-core")
include("store-details")
include("store-payment")

// shared libs

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("./libs.versions.toml"))
        }
    }
}
