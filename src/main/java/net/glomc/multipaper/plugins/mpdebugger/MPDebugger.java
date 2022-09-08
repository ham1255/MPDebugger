package net.glomc.multipaper.plugins.mpdebugger;

import net.glomc.multipaper.plugins.mpdebugger.commands.MPTPSCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class MPDebugger extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getCommandMap().register("mpdebugger", new MPTPSCommand("mptps"));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
