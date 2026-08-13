package com.gtnewhorizons.infohud.hud.core.infolines;

import java.util.ArrayList;
import java.util.List;

import com.gtnewhorizons.infohud.configs.HudConfig;
import com.gtnewhorizons.infohud.hud.core.InfoLine;

import ca.wescook.nutrition.api.INutritionManager;
import ca.wescook.nutrition.api.NutritionManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;

public class InfoNutrient extends InfoLine {

    private static List<Nutrient> cachedNutrients = null;

    public InfoNutrient(int order) {
        super(order);
    }

    @Override
    public String getLineString() {
        INutritionManager manager = NutritionManager.instance();
        StringBuilder sb = new StringBuilder();

        List<Nutrient> orderedNutrients = getOrderedNutrients();

        for (Nutrient nutrient : orderedNutrients) {
            float value = manager.get(getPlayer(), nutrient);
            String color = getNutrientColor(nutrient.name);
            String displayName = nutrient.name.substring(0, 1)
                .toUpperCase();

            sb.append(color)
                .append(displayName)
                .append(": ")
                .append(Math.round(value))
                .append("% ")
                .append("§r");

        }

        return sb.toString()
            .trim();
    }

    @Override
    public boolean canRender() {
        return HudConfig.hudEnabled.NutrientEnable;
    }

    @Override
    public String getItemName() {
        return HudConfig.hudItems.NutrientItem;
    }

    private String getNutrientColor(String nutrientName) {
        return switch (nutrientName.toLowerCase()) {
            case "dairy" -> "§b";
            case "fruit" -> "§d";
            case "grain" -> "§e";
            case "protein" -> "§6";
            case "vegetable" -> "§a";
            default -> "§f";
        };
    }

    private List<Nutrient> getOrderedNutrients() {
        if (cachedNutrients == null) {
            cachedNutrients = new ArrayList<>();
            String[] nutrientOrder = { "dairy", "fruit", "grain", "protein", "vegetable" };

            for (String nutrientName : nutrientOrder) {
                for (Nutrient nutrient : NutrientList.get()) {
                    if (nutrient.name.equalsIgnoreCase(nutrientName)) {
                        cachedNutrients.add(nutrient);
                        break;
                    }
                }
            }
        }
        return cachedNutrients;
    }
}
