package com.gtnewhorizons.infohud.hud.core.infolines;

import net.minecraft.stats.StatList;
import net.minecraft.util.EnumChatFormatting;

import com.gtnewhorizons.infohud.configs.HudConfig;
import com.gtnewhorizons.infohud.hud.core.InfoLine;

public class InfoWorldTime extends InfoLine {

    private final boolean isServer;

    public InfoWorldTime(int order, boolean server) {
        super(order);
        isServer = server;
    }

    @Override
    public String getLineString() {
        if (isServer) {
            long totalTicks = getWorld().getTotalWorldTime();
            long worldTime = getWorld().getWorldTime();

            long totalSeconds = totalTicks / 20;
            long totalMinutes = (totalSeconds % 3600) / 60;
            long totalHours = totalSeconds / 3600;

            long adjustedTime = (worldTime + 6_000) % 24_000;
            long hours = adjustedTime / 1_000;
            long minutes = ((adjustedTime % 1000) * 60) / 1000;

            boolean isNight = (worldTime % 24000) >= 13000;
            String nightSuffix = isNight
                ? " (" + EnumChatFormatting.DARK_GRAY + "Night" + EnumChatFormatting.RESET + ")"
                : "";

            String formattedTotalHours = String.format("%,d", totalHours);

            if (totalHours > 0) {
                return tr(
                    "info_worldtime.2",
                    formattedTotalHours,
                    String.format("%02d:%02d", hours, minutes),
                    nightSuffix);
            } else if (totalMinutes > 0) {
                return tr("info_worldtime.1", totalMinutes, String.format("%02d:%02d", hours, minutes), nightSuffix);
            } else {
                return tr("info_worldtime.0", totalSeconds, String.format("%02d:%02d", hours, minutes), nightSuffix);
            }
        } else {
            int playTicks = getPlayer().getStatFileWriter()
                .writeStat(StatList.minutesPlayedStat);
            long playSeconds = playTicks / 20;
            long playMinutes = (playSeconds % 3600) / 60;
            long playHours = playSeconds / 3600;

            String formattedTotalHours = String.format("%,d", playHours);

            if (playHours > 0) {
                return tr("info_playtime.2", formattedTotalHours);
            } else if (playMinutes > 0) {
                return tr("info_playtime.1", playMinutes);
            } else {
                return tr("info_playtime.0", playSeconds);
            }
        }
    }

    @Override
    public String getItemName() {
        return isServer ? HudConfig.hudItems.WorldTimeItem : HudConfig.hudItems.PlayTimeItem;
    }

    @Override
    public boolean canRender() {
        if (isServer) {
            return HudConfig.hudEnabled.WorldTimeEnable;
        } else {
            return HudConfig.hudEnabled.PlayTimeEnable && !mc.isSingleplayer();
        }
    }
}
