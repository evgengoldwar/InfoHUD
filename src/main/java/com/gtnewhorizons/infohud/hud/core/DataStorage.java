package com.gtnewhorizons.infohud.hud.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;

public class DataStorage {

    public static double tps = -1;
    public static double mspt = -1;
    public static int serverMemUsed = -1;
    public static int serverMemAllocated = -1;
    public static int serverMemMax = -1;
    public static long worldSeed = -1;
    public static final Set<UUID> seedSubscribers = new HashSet<>();
    public static final Set<UUID> tpsSubscribers = new HashSet<>();
    public static final Set<UUID> memSubscribers = new HashSet<>();
    private static final Map<UUID, PlayerStats> playerStatsMap = new HashMap<>();

    private static class PlayerStats {

        long sessionStartNano;
        int ping;

        PlayerStats() {
            this.sessionStartNano = System.nanoTime();
            this.ping = 0;
        }
    }

    public static void getClientTPS() {
        try {
            long[] tickTimes = Minecraft.getMinecraft()
                .getIntegratedServer().tickTimeArray;
            mspt = round(MathHelper.average(tickTimes) * 1.0E-6D);
            tps = round(1000.0D / mspt);

            if (tps > 20.0) {
                tps = 20.0;
            }
        } catch (NumberFormatException ignored) {}
    }

    public static String getTPSColor() {
        return (tps < 20) ? "§c" : "§a";
    }

    public static String getMSPTColor() {
        return (mspt < 40) ? "§a" : (mspt < 45) ? "§e" : (mspt < 50) ? "§6" : "§c";
    }

    private static double round(double value) {
        return (new BigDecimal(value)).setScale(2, RoundingMode.HALF_UP)
            .doubleValue();
    }

    public static void subscribeTPS(EntityPlayerMP player) {
        tpsSubscribers.add(player.getUniqueID());
    }

    public static void subscribeMem(EntityPlayerMP player) {
        memSubscribers.add(player.getUniqueID());
    }

    public static void subscribeSeed(EntityPlayerMP player) {
        seedSubscribers.add(player.getUniqueID());
    }

    public static void unsubscribeAll(EntityPlayerMP player) {
        UUID uuid = player.getUniqueID();
        tpsSubscribers.remove(uuid);
        memSubscribers.remove(uuid);
        seedSubscribers.remove(uuid);
    }

    public static void initPlayer(UUID uuid) {
        playerStatsMap.put(uuid, new PlayerStats());
    }

    public static void removePlayer(UUID uuid) {
        playerStatsMap.remove(uuid);
        tpsSubscribers.remove(uuid);
        memSubscribers.remove(uuid);
        seedSubscribers.remove(uuid);
    }

    public static long getPlayerSessionStart(UUID uuid) {
        PlayerStats stats = playerStatsMap.get(uuid);
        return stats != null ? stats.sessionStartNano : System.nanoTime();
    }

    public static int getPlayerPing(UUID uuid) {
        PlayerStats stats = playerStatsMap.get(uuid);
        return stats != null ? stats.ping : 0;
    }

    public static void setPlayerPing(UUID uuid, int ping) {
        PlayerStats stats = playerStatsMap.get(uuid);
        if (stats != null) {
            stats.ping = ping;
        } else {
            PlayerStats newStats = new PlayerStats();
            newStats.ping = ping;
            playerStatsMap.put(uuid, newStats);
        }
    }

    public static void reset() {
        tps = -1;
        mspt = -1;
        serverMemAllocated = -1;
        serverMemMax = -1;
        serverMemUsed = -1;
        worldSeed = -1;
    }
}
