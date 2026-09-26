package com.gtnewhorizons.infohud.hud.tags;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import com.gtnewhorizons.infohud.hud.HudUtils;

final class ThaumcraftTags {

    private ThaumcraftTags() {}

    static void registerTags() {
        String cat = "thaumcraft";
        TagRegistry.register(cat, "warp_total", () -> String.valueOf(HudUtils.getWarpTotal(player())));
        TagRegistry.register(cat, "warp_perm", () -> String.valueOf(HudUtils.getWarpPerm(player())));
        TagRegistry.register(cat, "warp_sticky", () -> String.valueOf(HudUtils.getWarpSticky(player())));
        TagRegistry.register(cat, "warp_temp", () -> String.valueOf(HudUtils.getWarpTemp(player())));
    }

    private static EntityPlayer player() {
        return Minecraft.getMinecraft().thePlayer;
    }
}
