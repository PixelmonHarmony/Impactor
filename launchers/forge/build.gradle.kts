import extensions.writeVersion

plugins {
    id("impactor.launcher-conventions")
    id("impactor.publishing-conventions")
}

loom {
    forge {
        runs {
            val client = maybeCreate("client")
            client.vmArgs("-Dmixin.debug.export=true")

            val server = maybeCreate("server")
            server.vmArgs("-Dmixin.debug.export=true")
        }

        mixinConfig("mixins.impactor.forge.json")
    }
}

dependencies {
    forge("net.minecraftforge:forge:${rootProject.property("minecraft")}-${rootProject.property("forge")}")

    implementation(project(":minecraft:impl"))
    modImplementation("ca.landonjw.gooeylibs:forge:3.0.0-1.20.1-SNAPSHOT@jar")

    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")

    include("io.leangen.geantyref:geantyref:1.3.13")

    implementation("net.impactdev.impactor.commands:common:5.3.1+1.20.1-SNAPSHOT")
    include(modImplementation("net.impactdev.impactor.commands:forge:5.3.1+1.20.1-SNAPSHOT") {
        exclude("net.impactdev.impactor.api", "config")
        exclude("net.impactdev.impactor.api", "core")
        exclude("net.impactdev.impactor.api", "items")
        exclude("net.impactdev.impactor.api", "players")
        exclude("net.impactdev.impactor.api", "plugins")
        exclude("net.impactdev.impactor.api", "storage")
    })

    listOf(
        libs.cloudCore,
        libs.cloudBrigadier,
        libs.cloudServices,
        libs.cloudAnnotations,
        libs.cloudConfirmations,
        libs.cloudProcessorsCommon,
        libs.cloudMinecraftExtras,
        ).forEach { include(it) }

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks {
    shadowJar {
        val mapped = "loom_mappings_1_20_1_layered_hash_40359_v2_forge_1_20_1_47_0_3_forge"
        dependencies {
            include(dependency("$mapped.ca.landonjw.gooeylibs:forge:.*"))
            include(dependency("net.impactdev.impactor.commands:common:.*"))

            exclude("ca/landonjw/gooeylibs2/forge/GooeyLibs.class")
            exclude("**/PlatformMethods.class")
            exclude("**/client-extra.jar")
        }

        val prefix = "net.impactdev.impactor.relocations"
        listOf(
            "ca.landonjw.gooeylibs2",
            "okio",
            "okhttp"
        ).forEach { relocate(it, "$prefix.$it") }
    }

    processResources {
        inputs.property("version", writeVersion(true))

        filesMatching("META-INF/mods.toml") {
            expand("version" to writeVersion(true))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            artifact(tasks.remapProductionJar)

            groupId = "net.impactdev.impactor.launchers"
            artifactId = "forge"
            version = writeVersion(true)
        }
    }
}

modrinth {
    loaders.set(listOf("forge"))
}