package com.gtnewhorizons.infohud.hud.tags;

import java.util.function.Supplier;

import net.minecraft.util.StatCollector;

public final class InfoTag {

    public final String name;
    public final String category;
    public final boolean condition;
    private final Supplier<String> supplier;

    InfoTag(String name, String category, boolean condition, Supplier<String> supplier) {
        this.name = name;
        this.category = category;
        this.condition = condition;
        this.supplier = supplier;
    }

    public String getValue() {
        try {
            return supplier.get();
        } catch (Throwable ignored) {
            return null;
        }
    }

    public String getPlaceholder() {
        return "{" + name + "}";
    }

    public String getDescription() {
        String key = "infohud.tag." + name;
        return StatCollector.canTranslate(key) ? StatCollector.translateToLocal(key) : "";
    }
}
