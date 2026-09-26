package com.gtnewhorizons.infohud.hud.tags;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

import gregtech.common.GTWorldgenerator;

final class GregTechTags {

    private GregTechTags() {}

    static void registerTags() {
        TagRegistry.registerCondition("gregtech", "ore_chunk", GregTechTags::isOreChunk);
    }

    private static boolean isOreChunk() {
        int chunkX = MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posX) >> 4;
        int chunkZ = MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posZ) >> 4;

        if (GTWorldgenerator.oregenPattern == GTWorldgenerator.OregenPattern.EQUAL_SPACING) {
            return (chunkX % 3 == 1 || chunkX % 3 == -2) && (chunkZ % 3 == 1 || chunkZ % 3 == -2);
        }

        if (GTWorldgenerator.oregenPattern == GTWorldgenerator.OregenPattern.AXISSYMMETRICAL) {
            return (chunkX % 3 == -1 || chunkX % 3 == 1) && (chunkZ % 3 == -1 || chunkZ % 3 == 1);
        }

        return false;
    }
}
