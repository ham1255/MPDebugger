package net.glomc.multipaper.plugins.mpdebugger.commands;

import net.glomc.multipaper.plugins.mpdebugger.MPDebugger;
import net.glomc.multipaper.plugins.mpdebugger.guis.ListServersGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;


public class ListServersCommand extends Command {

    private final Plugin plugin;

    public ListServersCommand(@NotNull String name, Plugin plugin) {
        super(name);
        this.plugin = plugin;
        setPermission("mpdebugger.command.mptps");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player) {
            new ListServersGui(this.plugin).openGui(player);
        } else {
            sender.sendMessage(MPDebugger.PLUGIN_PREFIX.append(Component.text(": this command is only for the player to run.").color(NamedTextColor.RED)));
        }
        return false;
    }
}
