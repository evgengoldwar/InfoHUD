package com.gtnewhorizons.infohud.hud.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gtnewhorizons.infohud.configs.HudConfig;
import com.gtnewhorizons.infohud.hud.Hud;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoCountItem;
import com.gtnewhorizons.infohud.utils.Position;

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

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .create();
    private static final String FILE_NAME = "Positions.json";
    private static File dataFile;
    private static HudPositionsData cachedPositions = null;

    public static class HudPositionsData {

        public Map<String, LinePosition> linePositions = new HashMap<>();
        public int effectX = 5;
        public int effectY = 2;
        public int countItemX = -1;
        public int countItemY = -1;
        public float scale = 1.0f;
    }

    public static class LinePosition {

        public int x;
        public int y;
        public boolean isSet;

        public LinePosition(int x, int y) {
            this.x = x;
            this.y = y;
            this.isSet = true;
        }
    }

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

    public static void init() {
        File configDir = new File(Minecraft.getMinecraft().mcDataDir, "config/InfoHUD");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        dataFile = new File(configDir, FILE_NAME);
    }

    public static void loadPositions() {
        try {
            if (!dataFile.exists()) {
                createDefaultPositions();
                return;
            }

            HudPositionsData data;
            try (Reader reader = new FileReader(dataFile)) {
                data = GSON.fromJson(reader, HudPositionsData.class);
            }

            if (data == null) {
                createDefaultPositions();
                return;
            }

            cachedPositions = data;

            if (Hud.lines != null) {
                for (InfoLine line : Hud.lines) {
                    LinePosition pos = data.linePositions.get(
                        line.getClass()
                            .getSimpleName());
                    if (pos != null && pos.isSet) {
                        if (line.position == null) {
                            line.position = new Position(pos.x, pos.y);
                        } else {
                            line.position.setX(pos.x);
                            line.position.setY(pos.y);
                            line.position.setEdit();
                        }
                    } else {
                        if (line.position == null) {
                            line.position = new Position(0, 0);
                        }
                    }
                }
            }

            HudConfig.hudPotion.PotionX = data.effectX;
            HudConfig.hudPotion.PotionY = data.effectY;
            HudConfig.hudGeneral.HudScale = data.scale;

            if (data.countItemX != -1 && data.countItemY != -1) {
                if (Hud.lines != null) {
                    for (InfoLine line : Hud.lines) {
                        if (line instanceof InfoCountItem) {
                            if (line.position == null) {
                                line.position = new Position(data.countItemX, data.countItemY);
                            } else {
                                line.position.setX(data.countItemX);
                                line.position.setY(data.countItemY);
                                line.position.setEdit();
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            createDefaultPositions();
        }
    }

    public static void savePositions(List<InfoLine> lines, int effectX, int effectY, float scale) {
        try {
            HudPositionsData data = new HudPositionsData();

            if (lines != null) {
                for (InfoLine line : lines) {
                    if (line.position != null && line.position.isEdit()) {
                        LinePosition pos = new LinePosition(line.position.getX(), line.position.getY());
                        data.linePositions.put(
                            line.getClass()
                                .getSimpleName(),
                            pos);
                    }
                }
            }

            data.effectX = effectX;
            data.effectY = effectY;
            data.scale = scale;

            if (lines != null) {
                for (InfoLine line : lines) {
                    if (line instanceof InfoCountItem && line.position != null && line.position.isEdit()) {
                        data.countItemX = line.position.getX();
                        data.countItemY = line.position.getY();
                        break;
                    }
                }
            }

            cachedPositions = data;

            try (Writer writer = new FileWriter(dataFile)) {
                GSON.toJson(data, writer);
            }

        } catch (IOException ignored) {}
    }

    public static void savePositions() {
        if (Hud.lines != null) {
            savePositions(
                Hud.lines,
                HudConfig.hudPotion.PotionX,
                HudConfig.hudPotion.PotionY,
                HudConfig.hudGeneral.HudScale);
        }
    }

    private static void createDefaultPositions() {
        if (Hud.lines != null) {
            int y = 40;
            for (InfoLine line : Hud.lines) {
                if (line.position == null) {
                    line.position = new Position(0, y);
                } else {
                    line.position.setX(0);
                    line.position.setY(y);
                }
                y += 11;
            }
        }
        HudConfig.hudPotion.PotionX = 5;
        HudConfig.hudPotion.PotionY = 2;
        HudConfig.hudGeneral.HudScale = 1.0f;
        savePositions();
    }

    public static HudPositionsData getCachedPositions() {
        if (cachedPositions == null) {
            loadPositions();
        }
        return cachedPositions;
    }
}
