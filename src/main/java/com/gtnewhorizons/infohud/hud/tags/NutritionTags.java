package com.gtnewhorizons.infohud.hud.tags;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;

import ca.wescook.nutrition.api.INutritionManager;
import ca.wescook.nutrition.api.NutritionManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;

final class NutritionTags {

    private static final String[] NUTRIENT_ORDER = { "dairy", "fruit", "grain", "protein", "vegetable" };
    private static List<Nutrient> cachedNutrients = null;

    private NutritionTags() {}

    static void registerTags() {
        String cat = "nutrition";
        TagRegistry.register(cat, "nutrients", NutritionTags::allNutrients);

        for (String name : NUTRIENT_ORDER) {
            TagRegistry.register(cat, name, () -> {
                Nutrient nutrient = find(name);
                return nutrient == null ? null : String.valueOf(Math.round(value(nutrient)));
            });
        }
    }

    private static float value(Nutrient nutrient) {
        INutritionManager manager = NutritionManager.instance();
        return manager.get(Minecraft.getMinecraft().thePlayer, nutrient);
    }

    private static Nutrient find(String name) {
        for (Nutrient nutrient : NutrientList.get()) {
            if (nutrient.name.equalsIgnoreCase(name)) {
                return nutrient;
            }
        }
        return null;
    }

    private static String allNutrients() {
        StringBuilder sb = new StringBuilder();

        for (Nutrient nutrient : getOrderedNutrients()) {
            sb.append(getNutrientColor(nutrient.name))
                .append(
                    nutrient.name.substring(0, 1)
                        .toUpperCase())
                .append(": ")
                .append(Math.round(value(nutrient)))
                .append("% ")
                .append("§r");
        }

        return sb.toString()
            .trim();
    }

    private static String getNutrientColor(String nutrientName) {
        return switch (nutrientName.toLowerCase()) {
            case "dairy" -> "§b";
            case "fruit" -> "§d";
            case "grain" -> "§e";
            case "protein" -> "§6";
            case "vegetable" -> "§a";
            default -> "§f";
        };
    }

    private static List<Nutrient> getOrderedNutrients() {
        if (cachedNutrients == null || cachedNutrients.isEmpty()) {
            List<Nutrient> ordered = new ArrayList<>();
            for (String nutrientName : NUTRIENT_ORDER) {
                Nutrient nutrient = find(nutrientName);
                if (nutrient != null) {
                    ordered.add(nutrient);
                }
            }
            cachedNutrients = ordered;
        }
        return cachedNutrients;
    }
}
