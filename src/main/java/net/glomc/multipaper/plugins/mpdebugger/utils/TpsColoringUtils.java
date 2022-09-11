package net.glomc.multipaper.plugins.mpdebugger.utils;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class TpsColoringUtils {

    public static BossBar.Color getTpsBossBarColor(Double tps) {
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


    public static TextColor getTpsColor(Double tps) {
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
