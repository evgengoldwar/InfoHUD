package InfoHUD.Hud.Core;

import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.Loader;

public class BackhandHandler {

    private static boolean initialized = false;
    private static boolean backhandAvailable = false;
    private static Method getOffhandItemMethod = null;

    public static void init() {
        if (initialized) return;
        initialized = true;

        if (!Loader.isModLoaded("backhand")) return;

        try {
            Class<?> backhandUtilsClass = Class.forName("xonin.backhand.api.core.BackhandUtils");
            getOffhandItemMethod = backhandUtilsClass.getMethod("getOffhandItem", EntityPlayer.class);
            backhandAvailable = true;
        } catch (Exception e) {
            backhandAvailable = false;
        }
    }

    public static boolean isBackhandAvailable() {
        if (!initialized) init();
        return backhandAvailable;
    }

    public static ItemStack getOffhandItem(EntityPlayer player) {
        if (!backhandAvailable || getOffhandItemMethod == null) return null;

        try {
            Object result = getOffhandItemMethod.invoke(null, player);
            return (ItemStack) result;
        } catch (Exception e) {
            return null;
        }
    }
}
