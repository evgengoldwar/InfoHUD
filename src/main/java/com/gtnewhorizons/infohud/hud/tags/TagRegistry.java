package com.gtnewhorizons.infohud.hud.tags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.util.StatCollector;

import com.gtnewhorizons.infohud.hud.HudUtils;

import cpw.mods.fml.common.Loader;

public final class TagRegistry {

    private static final Map<String, InfoTag> TAGS = new LinkedHashMap<>();
    private static final List<String> CATEGORIES = new ArrayList<>();
    private static boolean initialized = false;

    private TagRegistry() {}

    public static void init() {
        if (initialized) return;
        initialized = true;

        VanillaTags.registerTags();

        if (Loader.isModLoaded(HudUtils.BLOOD_MAGIC_ID)) {
            BloodMagicTags.registerTags();
        }

        if (Loader.isModLoaded(HudUtils.THAUMCRAFT_ID)) {
            ThaumcraftTags.registerTags();
        }

        if (Loader.isModLoaded(HudUtils.GREG_TECH_ID)) {
            GregTechTags.registerTags();
        }

        if (Loader.isModLoaded(HudUtils.FORESTRY_ID)) {
            ForestryTags.registerTags();
        }

        if (Loader.isModLoaded(HudUtils.NUTRIENT_ID)) {
            NutritionTags.registerTags();
        }
    }

    public static void register(String category, String name, Supplier<String> supplier) {
        add(new InfoTag(name, category, false, supplier));
    }

    public static void registerCondition(String category, String name, Supplier<Boolean> condition) {
        add(new InfoTag(name, category, true, () -> Boolean.TRUE.equals(condition.get()) ? "" : null));
    }

    private static void add(InfoTag tag) {
        TAGS.put(tag.name, tag);
        if (!CATEGORIES.contains(tag.category)) {
            CATEGORIES.add(tag.category);
        }
    }

    public static InfoTag get(String name) {
        return TAGS.get(name.toLowerCase(Locale.ROOT));
    }

    public static Collection<InfoTag> all() {
        return Collections.unmodifiableCollection(TAGS.values());
    }

    public static List<String> categories() {
        return Collections.unmodifiableList(CATEGORIES);
    }

    public static List<InfoTag> byCategory(String category) {
        List<InfoTag> result = new ArrayList<>();
        for (InfoTag tag : TAGS.values()) {
            if (tag.category.equals(category)) {
                result.add(tag);
            }
        }
        return result;
    }

    public static String getCategoryName(String category) {
        String key = "infohud.tag_category." + category;
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key);
        }
        return category.substring(0, 1)
            .toUpperCase(Locale.ROOT) + category.substring(1);
    }
}
