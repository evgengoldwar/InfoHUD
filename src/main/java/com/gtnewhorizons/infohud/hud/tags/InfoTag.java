package com.gtnewhorizons.infohud.hud.tags;

import java.util.function.Supplier;

import net.minecraft.util.StatCollector;

/**
 * A single piece of information that can be inserted into a HUD line as {@code {name}}.
 * <p>
 * A supplier returning {@code null} means the value is currently unavailable (no data from the server, wrong
 * dimension, ...). A line that uses an unavailable tag is hidden from the HUD.
 * <p>
 * Condition tags always produce an empty string and only control whether the line is visible
 * (for example {@code {slime_chunk}} shows the line only inside a slime chunk).
 */
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
