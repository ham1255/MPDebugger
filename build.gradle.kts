import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.8"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2" // Generates plugin.yml
    id ("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "1.0.7-MP-SNAPSHOT"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

group = "net.glomc.multipaper.plugins"
version = "1.0-SNAPSHOT"
description = "plugin that extends Multipaper debugging process."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    paperweightDevBundle("puregero.multipaper", "1.19.2-R0.1-SNAPSHOT")
    implementation("net.glomc.utils", "GuiUtils", "1.5.0-SNAPSHOT")
    compileOnly("puregero.multipaper", "MultiPaper-MasterMessagingProtocol", "1.19.2-R0.1-SNAPSHOT")
}

tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    runMojangMappedServer {

    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    shadowJar {
        // helper function by paper team :)
        fun reloc(pkg: String) = relocate(pkg, "net.glomc.multipaper.plugins.mpdebugger.shadedlibs.$pkg")
        reloc("net.glomc.utils.gui")
    }

}

// Configure plugin.yml generation
bukkit {
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    main = "net.glomc.multipaper.plugins.mpdebugger.MPDebugger"
    apiVersion = "1.19"
    authors = listOf("Ham1255")
}