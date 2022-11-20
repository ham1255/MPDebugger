package net.glomc.multipaper.plugins.mpdebugger.guis;

import co.technove.flare.libs.com.google.common.primitives.Ints;
import net.glomc.utils.gui.AbstractGui;
import net.glomc.utils.gui.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import puregero.multipaper.ExternalPlayer;
import puregero.multipaper.ExternalServer;
import puregero.multipaper.MultiPaper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static net.glomc.multipaper.plugins.mpdebugger.utils.TpsColoringUtils.getTpsColor;

public class ListServersGui extends AbstractGui {

    private final AtomicBoolean locked = new AtomicBoolean(false);

    private final DecimalFormat formatter = new DecimalFormat("#0.00");

    private final AtomicInteger page = new AtomicInteger(1);

    private final AtomicBoolean showDeadServers = new AtomicBoolean(false);

    public ListServersGui(Plugin plugin) {
        super(Component.text("MultiPaper servers"), 6, plugin);
        unregisterListenerOnClose(true);
        registerListener();
        render();
    }

    protected void render() {
        if (locked.get()) {
            return;
        }
        locked.set(true);
        clearRender();

        renderExternalServers(1);

        locked.set(false);
    }


    private void renderExternalServers(int page) {
        page = Ints.constrainToRange(page, 1, Integer.MAX_VALUE);
        final int max = (45 * page) - 1;
        final int min = max - 45;

        int i = 0;
        for (ExternalServer server : MultiPaper.getConnection().getServersMap().values()) {
            if (!(min <= 0 && 0 < max)) {
                i++;
                continue;
            }
            populate(createExternalServerItemStack(server));
            i++;
        }


    }


    private void clearRender() {
        // we will clear from row 1 to row 5
        for (int i = 0; i < 44; i++) {
            removeItem(i);
        }
    }

    private void populate(ItemStack itemStack) {
        for (int i = 0; i < 44; i++) {
            ItemStack checkStack = this.inventory.getItem(i);
            if (checkStack != null) continue;
            insertItem(i, itemStack);
            break;
        }
    }

    @Override
    public void onClick(InventoryClickEvent inventoryClickEvent) {
        inventoryClickEvent.getWhoClicked().sendMessage(Component.text(inventoryClickEvent.getSlot()));
        if (locked.get()) {
            return;
        }
        // render method must be called async not on the main thread.

    }

    private final static Component aliveStatus = Component.text("Status: ").color(NamedTextColor.YELLOW).append(Component.text("Alive").color(NamedTextColor.GREEN));

    private final static Component deadStatus = Component.text("Status: ").color(NamedTextColor.YELLOW).append(Component.text("Dead").color(NamedTextColor.RED));

    private final static Component emptyLore = Component.text("");

    private ItemStack createExternalServerItemStack(ExternalServer externalServer) {
        ItemBuilder itemBuilder = new ItemBuilder().setMaterial(Material.QUARTZ_BLOCK);
        itemBuilder.setName(Component.text("Server: " + externalServer.getName()).color(NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false));

        if (!externalServer.isAlive()) {
            return itemBuilder.setMaterial(Material.BLACK_TERRACOTTA).setLore(emptyLore, deadStatus, emptyLore,
                    Component.text("Last alive: " + externalServer.getLastAlive()).color(NamedTextColor.YELLOW)).build();
        } else {
            Component tpsLore = Component.text("TPS: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.YELLOW)
                    .append(Component.text(formatter.format(Math.min(externalServer.getTps(), 20))).color(getTpsColor(externalServer.getTps())));
            Component msptLore = Component.text("MSPT: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.YELLOW)
                    .append(Component.text(externalServer.getAverageTickTime() + "ms").color(getTpsColor(externalServer.getTps())));


            Component playersLore;


            if (externalServer.isMe()) {
                playersLore = Component.text("Players: " + MinecraftServer.getServer().getPlayerList().localPlayers.size()).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false);
                itemBuilder.setEnchanted(true);
            } else {
                int players = 0;
                for (ServerPlayer player : MinecraftServer.getServer().getPlayerList().players) {
                    if (player instanceof ExternalPlayer && ((ExternalPlayer) player).externalServerConnection == externalServer.getConnection()) {
                        players ++;
                    }
                }
                playersLore = Component.text("Players: " + players).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false);
            }

            return itemBuilder.setLore(emptyLore, aliveStatus, emptyLore, tpsLore, emptyLore, msptLore, emptyLore, playersLore).build();

        }

    }

}
