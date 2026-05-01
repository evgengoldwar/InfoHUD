package MinecraftImprovements.Hud.Core.InfoLines;

import MinecraftImprovements.Configs.HudConfig;
import MinecraftImprovements.Hud.Core.InfoLine;

public class InfoBiome extends InfoLine {

    public InfoBiome(int order) {
        super(order);
    }

    @Override
    public String getLineString() {
        return tr("info_biome", world.getBiomeGenForCoords(getX(), getZ()).biomeName, String.format("%.0f", world.getBiomeGenForCoords(getX(), getZ()).rainfall * 100));
    }

    @Override
    public boolean canRender() {
        return HudConfig.hudEnabled.BiomeEnable;
    }

    @Override
    public String getItemName() {
        return HudConfig.hudItems.BiomeItem;
    }
}
