package com.gtnewhorizons.infohud.hud.tags;

import static com.gtnewhorizons.infohud.hud.tags.TagRegistry.register;
import static com.gtnewhorizons.infohud.hud.tags.TagRegistry.registerCondition;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import com.gtnewhorizons.infohud.hud.HudUtils;
import com.gtnewhorizons.infohud.hud.core.DataStorage;
import com.gtnewhorizons.infohud.mixins.early.MinecraftAccessor;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class VanillaTags {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final String[] ROUGH_DIRECTION = { "South", "West", "North", "East" };
    private static final int MB = 1048576;

    private static int jumpCount = 0;
    private static boolean jumpListenerRegistered = false;

    private VanillaTags() {}

    static void registerTags() {
        registerJumpListener();

        String cat = "general";
        register(cat, "player", () -> player().getDisplayName());
        register(cat, "fps", () -> String.valueOf(((MinecraftAccessor) mc).getFps()));
        register(cat, "ping", () -> mc.isSingleplayer() ? null : str(DataStorage.getPlayerPing(uuid())));
        register(cat, "tps", () -> DataStorage.tps == -1 ? null : String.valueOf(DataStorage.tps));
        register(cat, "tps_color", () -> DataStorage.tps == -1 ? null : DataStorage.getTPSColor());
        register(cat, "mspt", () -> DataStorage.mspt == -1 ? null : String.valueOf(DataStorage.mspt));
        register(cat, "mspt_color", () -> DataStorage.mspt == -1 ? null : DataStorage.getMSPTColor());

        cat = "memory";
        register(cat, "mem_used", () -> str(usedMemory()));
        register(cat, "mem_alloc", () -> str(Runtime.getRuntime().totalMemory() / MB));
        register(cat, "mem_max", () -> str(maxMemory()));
        register(cat, "mem_percent", () -> str(usedMemory() * 100 / Math.max(1, maxMemory())));
        register(cat, "server_mem_used", () -> DataStorage.serverMemUsed == -1 ? null : str(DataStorage.serverMemUsed));
        register(
            cat,
            "server_mem_alloc",
            () -> DataStorage.serverMemAllocated == -1 ? null : str(DataStorage.serverMemAllocated));
        register(cat, "server_mem_max", () -> DataStorage.serverMemMax == -1 ? null : str(DataStorage.serverMemMax));

        cat = "position";
        register(cat, "x", () -> str(x()));
        register(cat, "y", () -> str(y()));
        register(cat, "z", () -> str(z()));
        register(cat, "chunk_x", () -> str(x() >> 4));
        register(cat, "chunk_z", () -> str(z() >> 4));
        register(
            cat,
            "direction",
            () -> ROUGH_DIRECTION[MathHelper.floor_double(player().rotationYaw * 4.0 / 360.0 + 0.5) & 3]);
        register(cat, "light", () -> str(world().getBlockLightValue(x(), y(), z())));

        cat = "world";
        register(cat, "dim_name", () -> world().provider.getDimensionName());
        register(cat, "dim_id", () -> str(world().provider.dimensionId));
        register(cat, "biome", () -> biome().biomeName);
        register(cat, "humidity", () -> String.format("%.0f", biome().rainfall * 100));
        register(cat, "temperature", () -> String.format("%.0f", biome().getFloatTemperature(x(), y(), z()) * 100));
        register(cat, "day", () -> str(world().getWorldTime() / 24000L));
        register(cat, "time", VanillaTags::worldClock);
        register(cat, "night", VanillaTags::nightSuffix);
        register(cat, "world_age", () -> formatShortDuration(world().getTotalWorldTime() / 20));
        registerCondition(cat, "slime_chunk", VanillaTags::isSlimeChunk);

        cat = "player";
        register(cat, "health", () -> String.format("%.0f", player().getHealth()));
        register(cat, "max_health", () -> String.format("%.0f", player().getMaxHealth()));
        register(
            cat,
            "food",
            () -> str(
                player().getFoodStats()
                    .getFoodLevel()));
        register(cat, "armor", () -> str(player().getTotalArmorValue()));
        register(cat, "xp_level", () -> str(player().experienceLevel));
        register(cat, "held_count", () -> str(countHeldItem()));
        register(cat, "jumps", () -> str(jumps()));
        register(cat, "play_time", VanillaTags::playTime);
        register(cat, "session_time", VanillaTags::sessionTime);
    }

    public static void resetSession() {
        jumpCount = 0;
    }

    private static String str(long value) {
        return String.valueOf(value);
    }

    private static EntityClientPlayerMP player() {
        return mc.thePlayer;
    }

    private static java.util.UUID uuid() {
        return player().getUniqueID();
    }

    private static World world() {
        return player().worldObj;
    }

    private static int x() {
        return MathHelper.floor_double(player().posX);
    }

    private static int y() {
        return MathHelper.floor_double(player().boundingBox.minY);
    }

    private static int z() {
        return MathHelper.floor_double(player().posZ);
    }

    private static BiomeGenBase biome() {
        return world().getBiomeGenForCoords(x(), z());
    }

    private static long usedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / MB;
    }

    private static long maxMemory() {
        return Runtime.getRuntime()
            .maxMemory() / MB;
    }

    private static String worldClock() {
        long adjustedTime = (world().getWorldTime() + 6_000) % 24_000;
        long hours = adjustedTime / 1_000;
        long minutes = ((adjustedTime % 1000) * 60) / 1000;
        return String.format("%02d:%02d", hours, minutes);
    }

    private static String nightSuffix() {
        boolean isNight = (world().getWorldTime() % 24000) >= 13000;
        return isNight ? " (" + EnumChatFormatting.DARK_GRAY + "Night" + EnumChatFormatting.RESET + ")" : "";
    }

    /**
     * Same format that was used by the old world/play time lines: 15s, 42m or 1,234h.
     */
    private static String formatShortDuration(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;

        if (hours > 0) {
            return String.format("%,dh", hours);
        } else if (minutes > 0) {
            return minutes + "m";
        }
        return totalSeconds + "s";
    }

    private static String playTime() {
        if (mc.isSingleplayer()) return null;
        int playTicks = player().getStatFileWriter()
            .writeStat(StatList.minutesPlayedStat);
        return formatShortDuration(playTicks / 20);
    }

    private static String sessionTime() {
        long elapsedSeconds = (System.nanoTime() - DataStorage.getPlayerSessionStart(uuid())) / 1_000_000_000L;
        long hours = elapsedSeconds / 3600;
        long minutes = (elapsedSeconds % 3600) / 60;
        long seconds = elapsedSeconds % 60;

        if (hours > 0) {
            return hours + "h " + minutes + "m " + seconds + "s";
        } else if (minutes > 0) {
            return minutes + "m " + seconds + "s";
        }
        return seconds + "s";
    }

    private static int jumps() {
        return player().getStatFileWriter()
            .writeStat(StatList.jumpStat) + jumpCount;
    }

    private static boolean isSlimeChunk() {
        long seed = DataStorage.worldSeed;

        if (seed == -1) {
            if (mc.getIntegratedServer() != null) {
                seed = mc.getIntegratedServer().worldServers[0].getSeed();
            } else if (world() != null) {
                seed = world().getSeed();
            } else {
                return false;
            }
        }

        return HudUtils.isSlimeChunk(seed, player(), world());
    }

    /**
     * @return how many of the held item the player has in the main inventory
     */
    public static int countHeldItem() {
        return countHeldItem(false);
    }

    /**
     * @param stacks count inventory slots holding the item instead of the item amount
     */
    public static int countHeldItem(boolean stacks) {
        EntityClientPlayerMP player = player();
        if (player == null) return 0;

        ItemStack held = player.getHeldItem();
        if (held == null) return 0;

        Item heldItem = held.getItem();
        int heldMeta = held.getItemDamage();
        int count = 0;

        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() == heldItem && stack.getItemDamage() == heldMeta) {
                count += stacks ? 1 : stack.stackSize;
            }
        }

        return count;
    }

    private static void registerJumpListener() {
        if (!jumpListenerRegistered) {
            MinecraftForge.EVENT_BUS.register(new JumpListener());
            jumpListenerRegistered = true;
        }
    }

    public static class JumpListener {

        @SubscribeEvent
        public void onPlayerJump(LivingEvent.LivingJumpEvent event) {
            if (event.entity == Minecraft.getMinecraft().thePlayer) {
                jumpCount++;
            }
        }
    }
}
