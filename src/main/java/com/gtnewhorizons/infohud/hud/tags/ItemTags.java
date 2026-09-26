package com.gtnewhorizons.infohud.hud.tags;

import static com.gtnewhorizons.infohud.hud.tags.TagRegistry.register;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import com.gtnewhorizons.infohud.hud.layout.HudLine;

public final class ItemTags {

    public static final String SLOT_PREFIX = "@";
    private static final String[] ARMOR_SLOTS = { "boots", "legs", "chest", "helmet" };

    private ItemTags() {}

    static void registerTags() {
        String cat = "items";
        register(cat, "held_name", () -> held().getDisplayName());
        register(cat, "held_id", () -> HudLine.itemToString(held()));
        register(cat, "held_dur", () -> durability(held()));
        register(cat, "held_max_dur", () -> damageable(held()) ? String.valueOf(held().getMaxDamage()) : null);
        register(cat, "held_dur_percent", () -> durabilityPercent(held()));
        register(cat, "held_icon", () -> icon("held"));

        for (int slot = ARMOR_SLOTS.length - 1; slot >= 0; slot--) {
            int armorSlot = slot;
            String name = ARMOR_SLOTS[slot];
            register(cat, name + "_name", () -> {
                ItemStack stack = armor(armorSlot);
                return stack == null ? null : stack.getDisplayName();
            });
            register(cat, name + "_dur", () -> durability(armor(armorSlot)));
            register(
                cat,
                name + "_max_dur",
                () -> damageable(armor(armorSlot)) ? String.valueOf(armor(armorSlot).getMaxDamage()) : null);
            register(cat, name + "_dur_percent", () -> durabilityPercent(armor(armorSlot)));
            register(cat, name + "_icon", () -> icon(name));
        }
    }

    public static ItemStack getSlotStack(String slot) {
        if (Minecraft.getMinecraft().thePlayer == null) return null;
        if (slot.equals("held")) return held();
        for (int i = 0; i < ARMOR_SLOTS.length; i++) {
            if (ARMOR_SLOTS[i].equals(slot)) return armor(i);
        }
        return null;
    }

    private static String icon(String slot) {
        if (getSlotStack(slot) == null) return null;
        return LineTemplate.ICON_START + SLOT_PREFIX + slot + LineTemplate.ICON_END;
    }

    private static String durabilityPercent(ItemStack stack) {
        if (!damageable(stack)) return null;
        return String.valueOf((stack.getMaxDamage() - stack.getItemDamage()) * 100 / stack.getMaxDamage());
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
