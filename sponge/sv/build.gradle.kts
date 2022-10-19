plugins {
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "0.12.0-SNAPSHOT"
}

architectury {
    platformSetupLoomIde()
}

dependencies {
    val loom = project.extensions.getByName<net.fabricmc.loom.api.LoomGradleExtensionAPI>("loom")
    loom.silentMojangMappingsLicense()

    minecraft("com.mojang:minecraft:${rootProject.property("minecraft")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric-loader")}")

    implementation(project(":sponge:common"))
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    jar {
        manifest {
            attributes["MixinConfigs"] = "mixins.impactor.sponge.json"
        }
    }

    shadowJar {
        val minecraft = rootProject.property("minecraft")
        val sponge = rootProject.property("sponge")

        archiveBaseName.set("Impactor-SpongeVanilla")
        archiveClassifier.set("")
        archiveVersion.set("$minecraft-$sponge-${rootProject.version}")

        dependencies {
            include(project(":api"))
            include(project(":common"))
            include(project(":game"))
            include(project(":sponge:common"))
            include(dependency("net.kyori:event-api:.*"))
            include(dependency("net.impactdev:json:.*"))
//            include(dependency("io.github.classgraph:classgraph:.*"))
            include(dependency("com.github.ben-manes.caffeine:caffeine:.*"))
        }

//        relocate ("io.github.classgraph", "net.impactdev.impactor.relocations.classgraph")
//        relocate ("nonapi.io.github.classgraph", "net.impactdev.impactor.relocations.classgraph.nonapi")
        relocate ("net.kyori.event", "net.impactdev.impactor.relocations.kyori.event")
        relocate ("com.github.benmanes.caffeine", "net.impactdev.impactor.relocations.caffeine")
    }
}