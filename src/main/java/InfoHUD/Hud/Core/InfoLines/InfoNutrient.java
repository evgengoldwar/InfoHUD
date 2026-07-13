package InfoHUD.Hud.Core.InfoLines;

import InfoHUD.Configs.HudConfig;
import InfoHUD.Hud.Core.InfoLine;
import ca.wescook.nutrition.data.NutrientManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.proxy.ClientProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfoNutrient extends InfoLine {

    private static List<Nutrient> cachedNutrients = null;

    public InfoNutrient(int order) {
        super(order);
    }

    @Override
    public String getLineString() {
        NutrientManager localManager = ClientProxy.localNutrition;

        if (localManager != null) {
            Map<Nutrient, Float> allNutrients = localManager.get();
            StringBuilder sb = new StringBuilder();

            List<Nutrient> orderedNutrients = getOrderedNutrients();

            for (Nutrient nutrient : orderedNutrients) {
                Float value = allNutrients.get(nutrient);
                if (value != null) {
                    String color = getNutrientColor(nutrient.name);
                    String displayName = nutrient.name.substring(0, 1).toUpperCase();

                    sb.append(color)
                        .append(displayName)
                        .append(": ")
                        .append(Math.round(value))
                        .append("% ")
                        .append("§r");
                }
            }

            return sb.toString().trim();
        }

        return "";
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
            String[] nutrientOrder = {"dairy", "fruit", "grain", "protein", "vegetable"};

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
