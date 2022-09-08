package net.glomc.multipaper.plugins.mpdebugger;

import net.glomc.multipaper.plugins.mpdebugger.commands.MPTPSCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.plugin.java.JavaPlugin;

public final class MPDebugger extends JavaPlugin {

    public static final Component PLUGIN_PREFIX = Component.text("MPDebugger").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD);

    @Override
    public void onEnable() {
        // Plugin startup logic
        try {
            Class.forName("puregero.multipaper.MultiPaper");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("This plugin is intended to be ran on MultiPaper platform only", e);
        }
        getServer().getCommandMap().register("mpdebugger", new MPTPSCommand("mptps"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
