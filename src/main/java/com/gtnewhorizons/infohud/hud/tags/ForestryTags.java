package com.gtnewhorizons.infohud.hud.tags;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;

final class ForestryTags {

    private ForestryTags() {}

    static void registerTags() {
        String cat = "forestry";
        TagRegistry.register(
            cat,
            "forestry_temp_name",
            () -> AlleleManager.climateHelper.toDisplay(EnumTemperature.getFromBiome(biome())));
        TagRegistry.register(cat, "forestry_temp", () -> {
            EntityPlayer p = player();
            int x = MathHelper.floor_double(p.posX);
            int y = MathHelper.floor_double(p.boundingBox.minY);
            int z = MathHelper.floor_double(p.posZ);
            return String.valueOf((int) (biome().getFloatTemperature(x, y, z) * 100));
        });
        TagRegistry.register(
            cat,
            "forestry_humidity_name",
            () -> AlleleManager.climateHelper.toDisplay(EnumHumidity.getFromValue(biome().rainfall)));
        TagRegistry.register(cat, "forestry_humidity", () -> String.valueOf((int) (biome().rainfall * 100)));
    }

    private static EntityPlayer player() {
        return Minecraft.getMinecraft().thePlayer;
    }

    private static BiomeGenBase biome() {
        EntityPlayer p = player();
        return p.worldObj.getBiomeGenForCoordsBody(MathHelper.floor_double(p.posX), MathHelper.floor_double(p.posZ));
    }
}
