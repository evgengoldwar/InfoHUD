package com.gtnewhorizons.infohud.hud.tags;

import static com.gtnewhorizons.infohud.hud.tags.TagRegistry.register;

import com.gtnewhorizons.infohud.hud.core.DataStorage;

final class ServerTags {

    private ServerTags() {}

    static void registerTags() {
        String cat = "server";
        register(cat, "server_uptime", () -> DataStorage.serverUptime < 0 ? null
            : formatDuration((DataStorage.serverUptime + sinceReceived()) / 1000));
        register(cat, "server_time", () -> {
            if (DataStorage.serverTime < 0) return null;
            long seconds = (DataStorage.serverTime + sinceReceived()) / 1000;
            return String.format("%02d:%02d", seconds / 3600 % 24, seconds / 60 % 60);
        });
        register(cat, "tps_dim", () -> DataStorage.tpsDim < 0 ? null : String.valueOf(DataStorage.tpsDim));
        register(cat, "mspt_dim", () -> DataStorage.msptDim < 0 ? null : String.valueOf(DataStorage.msptDim));
    }

    private static long sinceReceived() {
        return Math.max(0, System.currentTimeMillis() - DataStorage.serverInfoReceived);
    }

    private static String formatDuration(long totalSeconds) {
        long days = totalSeconds / 86400;
        long hours = totalSeconds / 3600 % 24;
        long minutes = totalSeconds / 60 % 60;
        long seconds = totalSeconds % 60;

        if (days > 0) return days + "d " + hours + "h " + minutes + "m";
        if (hours > 0) return hours + "h " + minutes + "m";
        if (minutes > 0) return minutes + "m " + seconds + "s";
        return seconds + "s";
    }
}
