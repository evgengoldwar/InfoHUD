package com.gtnewhorizons.infohud.hud.tags;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import com.gtnewhorizons.infohud.hud.HudUtils;
import com.gtnewhorizons.infohud.hud.event.BloodMagicEvent;

final class BloodMagicTags {

    private BloodMagicTags() {}

    static void registerTags() {
        String cat = "blood_magic";
        TagRegistry.register(cat, "lp", () -> String.format("%,d", HudUtils.getPlayerLPTag(player())));
        TagRegistry.register(
            cat,
            "lp_max",
            () -> String.format(
                "%,d",
                Math.max(
                    HudUtils.getPlayerMaxLPTag(player()),
                    BloodMagicEvent.getMaxLP(player().getDisplayName()))));
    }

    private static EntityPlayer player() {
        return Minecraft.getMinecraft().thePlayer;
    }
}
