package com.gtnewhorizons.infohud.hud.core.infolines;

import com.gtnewhorizons.infohud.configs.HudConfig;
import com.gtnewhorizons.infohud.hud.core.InfoLine;

public class InfoDimension extends InfoLine {

    public InfoDimension(int order) {
        super(order);
    }

    @Override
    public String getLineString() {
        return tr("info_dimension", getProvider().getDimensionName(), getProvider().dimensionId);
    }

    @Override
    public boolean canRender() {
        return HudConfig.hudEnabled.DimensionEnable;
    }

    @Override
    public String getItemName() {
        return HudConfig.hudItems.DimensionItem;
    }
}
