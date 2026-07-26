package com.gtnewhorizons.infohud.utils;

import net.minecraft.util.StatCollector;

public class Utils {

    public static String tr(String key) {
        return StatCollector.translateToLocal(key);
    }

    public static String tr(String key, Object... formatted) {
        return StatCollector.translateToLocalFormatted(key, formatted);
    }
}
