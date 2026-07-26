package com.gtnewhorizons.infohud.hud.core.infolines;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.gtnewhorizons.infohud.configs.HudConfig;
import com.gtnewhorizons.infohud.hud.core.InfoLine;

public class InfoCountItem extends InfoLine {

    public InfoCountItem(int order) {
        super(order);
    }

    @Override
    public String getLineString() {

        ItemStack heldItemStack = getPlayer().getHeldItem();

        if (heldItemStack == null) {
            return "";
        }

        Item heldItem = heldItemStack.getItem();
        int heldMeta = heldItemStack.getItemDamage();

        int count = 0;

        for (ItemStack stack : getPlayer().inventory.mainInventory) {
            if (stack == null) {
                continue;
            }

            if (stack.getItem() == heldItem && stack.getItemDamage() == heldMeta) {
                count += stack.stackSize;
            }
        }

        return String.valueOf(count);
    }

    @Override
    public boolean canRender() {
        ItemStack heldItemStack = getPlayer().getHeldItem();

        if (heldItemStack == null) return false;

        Item heldItem = heldItemStack.getItem();
        int heldMeta = heldItemStack.getItemDamage();

        int count = 0;

        for (ItemStack stack : getPlayer().inventory.mainInventory) {
            if (stack == null) {
                continue;
            }

            if (stack.getItem() == heldItem && stack.getItemDamage() == heldMeta) {
                count++;
            }
        }

        return count > 1 && HudConfig.hudEnabled.CountItemEnable;
    }
}
