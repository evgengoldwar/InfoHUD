package com.gtnewhorizons.infohud.hud.core.infolines;

import com.gtnewhorizons.infohud.configs.HudConfig;
import com.gtnewhorizons.infohud.hud.core.InfoLine;

public class InfoBiome extends InfoLine {

    public InfoBiome(int order) {
        super(order);
    }

    @Override
    public String getLineString() {
        return tr("info_biome", getBiome().biomeName, String.format("%.0f", getBiome().rainfall * 100));
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
