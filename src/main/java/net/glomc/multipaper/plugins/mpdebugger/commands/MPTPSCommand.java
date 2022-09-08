package net.glomc.multipaper.plugins.mpdebugger.commands;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_19_R1.scheduler.CraftScheduler;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import puregero.multipaper.ExternalPlayer;
import puregero.multipaper.ExternalServer;
import puregero.multipaper.MultiPaper;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class MPTPSCommand extends Command implements Runnable {
    private final HashMap<Player, BossBar> debugEnabled = new HashMap<>();
    private BukkitTask task = null;

    private final DecimalFormat formatter = new DecimalFormat("#0.00");

    public MPTPSCommand(String command) {
        super(command);
        setPermission("mpdebugger.command.mptps");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (!testPermission(sender)) return false;

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
            return false;
        }

        if (debugEnabled.containsKey(player)) {
            player.hideBossBar(debugEnabled.remove(player));
            player.sendMessage("MultiPaper TPS bossbar disabled");
            return false;
        }

        BossBar bossBar = BossBar.bossBar(Component.text("loading..."), 0.0f, BossBar.Color.GREEN, BossBar.Overlay.NOTCHED_20);
        debugEnabled.put(player, bossBar);
        player.showBossBar(bossBar);

        sender.sendMessage("MultiPaper TPS bossbar enabled");

        if (task == null) {
            run();
        }

        return true;
    }

    @Override
    public void run() {
        task = null;

        for (Map.Entry<Player, BossBar> playerBossBarEntry : new HashMap<>(this.debugEnabled).entrySet()) {
            Player player = playerBossBarEntry.getKey();
            if (!player.isOnline()) {
                this.debugEnabled.remove(player.getPlayer());
                continue;
            }

            run(player, playerBossBarEntry.getValue());
        }

        if (!debugEnabled.isEmpty()) {
            task = ((CraftScheduler) Bukkit.getScheduler()).scheduleInternalTask(this, 5, "MultiPaper tps BossBar");
        }
    }

    private void run(Player player, BossBar bossBar) {
        Chunk chunk = player.getChunk();

        if (chunk != null) {
            ExternalServer owner = ((CraftChunk) chunk).getHandle().externalOwner;
            final boolean isOwnerNull = owner == null;

            int localPlayers = 0;
            int externalPlayers = 0;
            boolean noLocalLoop = false;
            if (!isOwnerNull && owner.isMe()) {
                localPlayers = MinecraftServer.getServer().getPlayerList().localPlayers.size();
                noLocalLoop = true;
            }
            for (ServerPlayer serverPlayer : MinecraftServer.getServer().getPlayerList().players) {
                if (isOwnerNull) {
                    break;
                }
                if (!noLocalLoop && (owner.isMe() && MultiPaper.isRealPlayer(serverPlayer)
                        || serverPlayer instanceof ExternalPlayer externalPlayer && externalPlayer.externalServerConnection == owner.getConnection())) {
                    localPlayers++;
                }
                if (MultiPaper.isExternalPlayer(serverPlayer) &&
                        serverPlayer.getBukkitEntity().getChunk() instanceof CraftChunk craftChunk
                        && craftChunk.getHandle().externalOwner == owner) {
                    externalPlayers++;
                }

            }
            final int totalPlayers = MinecraftServer.getServer().getPlayerList().players.size();

            Component component = Component.text("Server: ").color(NamedTextColor.YELLOW)
                    .append(Component.text((isOwnerNull ? "null" : owner.getName())).color(isOwnerNull ? NamedTextColor.WHITE : (owner.isMe() ? NamedTextColor.AQUA : NamedTextColor.RED)))
                    .append(Component.text(" TPS: ").color(NamedTextColor.YELLOW).append(Component.text((formatter.format(isOwnerNull ? 0.0 : (Math.min(owner.getTps(), 20.00))))).color(getTpsColor(!isOwnerNull ? owner.getTps() : null))))
                    .append(Component.text(" MSPT: ").color(NamedTextColor.YELLOW).append(Component.text((isOwnerNull ? 0 : owner.getAverageTickTime()) + "ms").color(getTpsColor(!isOwnerNull ? owner.getTps() : null))))
                    .append(Component.text(" Local players: ").color(NamedTextColor.YELLOW).append(Component.text(localPlayers).color(NamedTextColor.WHITE)))
                    .append(Component.text(" External players: ").color(NamedTextColor.YELLOW).append(Component.text(externalPlayers).color(NamedTextColor.WHITE)))
                    .append(Component.text(" Total players: ").color(NamedTextColor.YELLOW).append(Component.text(totalPlayers).color(NamedTextColor.WHITE)));

            bossBar.name(component).color(getTpsBossBarColor(!isOwnerNull ? owner.getTps() : null)).progress(getBarTpsProgress(!isOwnerNull ? owner.getTps() : 0.0));
        }
    }


    private float getBarTpsProgress(Double tps) {
        return (float) Math.max(Math.min(tps / 20.0, 1.0), 0.0);
    }

    private BossBar.Color getTpsBossBarColor(Double tps) {
        if (tps == null) {
            return BossBar.Color.WHITE;
        } else if (tps >= 19.9) {
            return BossBar.Color.GREEN;
        } else if (tps >= 17.5) {
            return BossBar.Color.YELLOW;
        } else {
            return BossBar.Color.RED;
        }
    }

    private TextColor getTpsColor(Double tps) {
        if (tps == null) {
            return NamedTextColor.DARK_GRAY;
        } else if (tps >= 19.9) {
            return NamedTextColor.GREEN;
        } else if (tps >= 17.5) {
            return NamedTextColor.YELLOW;
        } else {
            return NamedTextColor.RED;
        }
    }
}