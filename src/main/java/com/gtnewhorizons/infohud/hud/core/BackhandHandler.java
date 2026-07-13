package com.gtnewhorizons.infohud.hud.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.Loader;
import xonin.backhand.api.core.BackhandUtils;

public class BackhandHandler {
    private static final boolean BACKHAND_LOADED = Loader.isModLoaded("backhand");

    public static boolean isLoaded() {
        return BACKHAND_LOADED;
    }

    public static ItemStack getBackHandItemStack(EntityPlayer player) {
        if (!isLoaded()) return null;

        return BackhandUtils.getOffhandItem(player);
    }
}
