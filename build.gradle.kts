plugins {
    id("fabric-loom") version "1.10.5"
    `maven-publish`
}

version = property("mod_version") as String
group = property("maven_group") as String

base {
    archivesName.set(property("archives_base_name") as String)
}

repositories {
    mavenCentral()
}

val minecraftVersion = property("minecraft_version") as String
val cobblemonVersion = property("cobblemon_version") as String
val cobblemonJar = layout.projectDirectory.file(
    "libs/Cobblemon-fabric-$cobblemonVersion+$minecraftVersion.jar"
)
val unitTestRuntime by configurations.creating

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    modImplementation(files(cobblemonJar))
    modCompileOnly("net.fabricmc:fabric-language-kotlin:1.13.6+kotlin.2.2.20")
    modLocalRuntime("net.fabricmc:fabric-language-kotlin:1.13.6+kotlin.2.2.20")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    unitTestRuntime(platform("org.junit:junit-bom:5.11.4"))
    unitTestRuntime("org.junit.jupiter:junit-jupiter")
    unitTestRuntime("org.junit.platform:junit-platform-launcher")
}

val verifyCobblemonDependency by tasks.registering {
    group = "verification"
    description = "Checks that the verified Cobblemon Fabric dependency is present."
    inputs.file(cobblemonJar)

    doLast {
        if (!cobblemonJar.asFile.isFile) {
            throw GradleException(
                "Missing ${cobblemonJar.asFile}. Run scripts/sync-dependencies.ps1 first."
            )
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    dependsOn(verifyCobblemonDependency)
    options.release.set(21)
    options.encoding = "UTF-8"
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", minecraftVersion)
    inputs.property("cobblemon_version", cobblemonVersion)

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to minecraftVersion,
            "cobblemon_version" to cobblemonVersion
        )
    }
}

tasks.test {
    useJUnitPlatform()
    classpath = files(
        sourceSets["test"].output,
        sourceSets["main"].output,
        unitTestRuntime
    )
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
}

loom {
    runs {
        named("client") {
            client()
            configName = "Cobblemon Chromatics Client"
            runDir = "run/client"
        }
        named("server") {
            server()
            configName = "Cobblemon Chromatics Server"
            runDir = "run/server"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = property("archives_base_name") as String
            from(components["java"])
        }
    }
}
