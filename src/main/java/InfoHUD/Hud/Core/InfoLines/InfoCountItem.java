package InfoHUD.Hud.Core.InfoLines;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import InfoHUD.Configs.HudConfig;
import InfoHUD.Hud.Core.InfoLine;
import cpw.mods.fml.common.Loader;
import xonin.backhand.api.core.BackhandUtils;

public class InfoCountItem extends InfoLine {

    public InfoCountItem(int order) {
        super(order);
    }

    @Override
    public String getLineString() {

        ItemStack heldItemStack = playerMP.getHeldItem();

        if (heldItemStack == null) {
            return "";
        }

        Item heldItem = heldItemStack.getItem();
        int heldMeta = heldItemStack.getItemDamage();

        int count = 0;

        for (ItemStack stack : playerMP.inventory.mainInventory) {
            if (stack == null) {
                continue;
            }

            if (stack.getItem() == heldItem && stack.getItemDamage() == heldMeta) {
                count += stack.stackSize;
            }
        }

        if (Loader.isModLoaded("backhand")) {
            ItemStack backHand = BackhandUtils.getOffhandItem(playerMP);

            if (backHand != null && backHand.getItem() == heldItem && backHand.getItemDamage() == heldMeta) {
                count += backHand.stackSize;
            }
        }

        return String.valueOf(count);
    }

    @Override
    public boolean canRender() {
        ItemStack heldItemStack = playerMP.getHeldItem();

        if (heldItemStack == null) return false;

        Item heldItem = heldItemStack.getItem();
        int heldMeta = heldItemStack.getItemDamage();

        int count = 0;

        for (ItemStack stack : playerMP.inventory.mainInventory) {
            if (stack == null) {
                continue;
            }

            if (stack.getItem() == heldItem && stack.getItemDamage() == heldMeta) {
                count++;
            }
        }

        if (Loader.isModLoaded("backhand")) {
            ItemStack backHand = BackhandUtils.getOffhandItem(playerMP);

            if (backHand != null && backHand.getItem() == heldItem && backHand.getItemDamage() == heldMeta) {
                count++;
            }
        }

        return count > 1 && HudConfig.hudEnabled.CountItemEnable;
    }
}
