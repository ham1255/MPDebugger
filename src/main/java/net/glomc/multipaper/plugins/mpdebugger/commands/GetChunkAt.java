package net.glomc.multipaper.plugins.mpdebugger.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GetChunkAt extends Command {
    public GetChunkAt(@NotNull String name) {
        super(name);
        setPermission("mpdebugger.command.getchunkat");
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
            if (strings.length == 0) {
                player.sendMessage(Component.text("chunk x is not set | chunk z is not set"));
                return false;
            }
            int x = Integer.parseInt(strings[0]);
            int z = Integer.parseInt(strings[1]);
            player.sendMessage(Component.text("requesting chunk at " + x + " " + z));
            player.getWorld().getChunkAt(x, z);
        }
        return false;
    }
}
