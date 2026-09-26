package com.gtnewhorizons.infohud.hud.tags;

import static com.gtnewhorizons.infohud.hud.tags.TagRegistry.register;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import com.gtnewhorizons.infohud.hud.layout.HudLine;

final class ItemTags {

    private ItemTags() {}

    static void registerTags() {
        String cat = "items";
        register(cat, "held_name", () -> held().getDisplayName());
        register(cat, "held_id", () -> HudLine.itemToString(held()));
        register(cat, "held_durability", () -> durability(held()));
        register(cat, "held_max_durability", () -> damageable(held()) ? String.valueOf(held().getMaxDamage()) : null);
        register(cat, "held_durability_percent", () -> {
            ItemStack stack = held();
            if (!damageable(stack)) return null;
            return String.valueOf((stack.getMaxDamage() - stack.getItemDamage()) * 100 / stack.getMaxDamage());
        });
        register(cat, "helmet_dur", () -> durability(armor(3)));
        register(cat, "chest_dur", () -> durability(armor(2)));
        register(cat, "legs_dur", () -> durability(armor(1)));
        register(cat, "boots_dur", () -> durability(armor(0)));
    }

    private static ItemStack held() {
        return Minecraft.getMinecraft().thePlayer.getHeldItem();
    }

    private static ItemStack armor(int slot) {
        return Minecraft.getMinecraft().thePlayer.inventory.armorItemInSlot(slot);
    }

    private static boolean damageable(ItemStack stack) {
        return stack != null && stack.isItemStackDamageable() && stack.getMaxDamage() > 0;
    }

    private static String durability(ItemStack stack) {
        if (!damageable(stack)) return null;
        return String.valueOf(stack.getMaxDamage() - stack.getItemDamage());
    }
}
