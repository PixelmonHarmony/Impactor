import extensions.writeVersion

plugins {
    id("impactor.launcher-conventions")
    id("impactor.publishing-conventions")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

architectury {
    platformSetupLoomIde()
    fabric()
}

repositories {
    maven("https://maven.nucleoid.xyz/") { name = "Nucleoid" }
}

configurations {
    all {
        resolutionStrategy {
            force("net.fabricmc.fabric-api:fabric-networking-api-v1:1.3.11+1802ada577")
            force("net.fabricmc.fabric-api:fabric-command-api-v2:2.2.13+1802ada577")
            force("net.fabricmc.fabric-api:fabric-lifecycle-events-v1:2.2.22+1802ada577")
            force("net.fabricmc.fabric-api:fabric-api-base:0.4.31+1802ada577")
        }
    }
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric-loader")}")
    listOf(
        "fabric-lifecycle-events-v1",
        "fabric-command-api-v2",
        "fabric-networking-api-v1",
    ).forEach { modImplementation(fabricApi.module(it, rootProject.property("fabric-api").toString())) }

    implementation(project(":minecraft:impl"))
    modImplementation("ca.landonjw.gooeylibs:fabric:3.0.0-1.20.1-SNAPSHOT@jar")
    include(modImplementation("net.impactdev.impactor.commands:fabric:5.2.5+1.20.1-SNAPSHOT") {
        exclude("net.impactdev.impactor.api", "config")
        exclude("net.impactdev.impactor.api", "core")
        exclude("net.impactdev.impactor.api", "items")
        exclude("net.impactdev.impactor.api", "players")
        exclude("net.impactdev.impactor.api", "plugins")
        exclude("net.impactdev.impactor.api", "storage")
    })

    listOf(
        libs.cloudFabric,
        libs.cloudAnnotations,
        libs.cloudMinecraftExtras,
        libs.cloudConfirmations,
        libs.cloudProcessorsCommon,
        libs.adventureFabric
    ).forEach { include(it) }

    include(modImplementation("eu.pb4:placeholder-api:2.1.3+1.20.1")!!)
    include("io.leangen.geantyref:geantyref:1.3.13")

    modRuntimeOnly("me.lucko:fabric-permissions-api:0.2-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks {
    processResources {
        inputs.property("version", writeVersion(true))

        filesMatching("fabric.mod.json") {
            expand("version" to writeVersion(true))
        }
    }

    shadowJar {
        val mapped = "loom_mappings_1_20_1_layered_hash_40359_v2"
        dependencies {
            include(dependency("net.impactdev.impactor.commands:common:.*"))

            include(dependency("org.apache.maven:maven-artifact:.*"))
            include(dependency("$mapped.ca.landonjw.gooeylibs:fabric:.*"))

            exclude("**/PlatformMethods.class")
        }

        val prefix = "net.impactdev.impactor.relocations"
        listOf(
            "org.apache.maven",
            "ca.landonjw.gooeylibs2",
            "okio",
            "okhttp"
        ).forEach { relocate(it, "$prefix.$it") }
    }
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            artifact(tasks.remapProductionJar)

            groupId = "net.impactdev.impactor.launchers"
            artifactId = "fabric"
            version = writeVersion(true)
        }
    }
}

modrinth {
    loaders.set(listOf("fabric"))
    dependencies {
        required.project("fabric-api")
        optional.project("placeholder-api")
    }
}

configurations.all {
    resolutionStrategy {
        force("net.fabricmc:fabric-loader:${rootProject.property("fabric-loader")}")
        force("com.google.code.gson:gson:2.10.1")
    }
}