import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2" // Generates plugin.yml
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "2.0.0"

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
    paperweight.devBundle("puregero.multipaper", "1.19.3-R0.1-SNAPSHOT")
    implementation("net.glomc.utils", "GuiUtils", "1.5.0-SNAPSHOT")
    compileOnly("puregero.multipaper", "MultiPaper-MasterMessagingProtocol", "2.11.0-1.19.3")
}

tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    runServer {
        serverJar(File("/home/mohammed/MultiPaper/build/libs/MultiPaper-paperclip-1.19.3-R0.1-SNAPSHOT-reobf.jar"))
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