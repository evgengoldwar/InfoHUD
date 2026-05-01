package MinecraftImprovements.Hud.Core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;

public class DataStorage {

    public static double tps = -1;
    public static double mspt = -1;
    public static int ping = 0;
    public static int serverMemUsed = -1;
    public static int serverMemAllocated = -1;
    public static int serverMemMax = -1;
    public static final Set<UUID> tpsSubscribers = new HashSet<>();
    public static final Set<UUID> memSubscribers = new HashSet<>();

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

    public static void unsubscribeAll(EntityPlayerMP player) {
        tpsSubscribers.remove(player.getUniqueID());
        memSubscribers.remove(player.getUniqueID());
    }

    public static void reset() {
        tps = -1;
        mspt = -1;
        serverMemAllocated = -1;
        serverMemMax = -1;
        serverMemUsed = -1;
    }
}
