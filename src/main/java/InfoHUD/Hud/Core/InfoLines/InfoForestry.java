package InfoHUD.Hud.Core.InfoLines;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.biome.BiomeGenBase;

import InfoHUD.Configs.HudConfig;
import InfoHUD.Hud.Core.InfoLine;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;

public class InfoForestry extends InfoLine {

    public InfoForestry(int order) {
        super(order);
    }

    @Override
    public String getLineString() {
        BiomeGenBase biomeGenBase = getPlayer().worldObj.getBiomeGenForCoordsBody(getX(), getZ());
        EnumTemperature temperature = EnumTemperature.getFromBiome(biomeGenBase);
        EnumHumidity humidity = EnumHumidity.getFromValue(biomeGenBase.rainfall);
        float exactTemperature = biomeGenBase.getFloatTemperature(getX(), getY(), getZ());
        float exactHumidity = biomeGenBase.rainfall;

        return AlleleManager.climateHelper.toDisplay(temperature) + ": "
            + EnumChatFormatting.GOLD
            + (int) (exactTemperature * 100)
            + "%"
            + EnumChatFormatting.RESET
            + " "
            + AlleleManager.climateHelper.toDisplay(humidity)
            + ": "
            + EnumChatFormatting.GOLD
            + (int) (exactHumidity * 100)
            + "%"
            + EnumChatFormatting.RESET;
    }

    @Override
    public boolean canRender() {
        return HudConfig.hudEnabled.ForestryEnable;
    }

    @Override
    public String getItemName() {
        return HudConfig.hudItems.ForestryItem;
    }
}
