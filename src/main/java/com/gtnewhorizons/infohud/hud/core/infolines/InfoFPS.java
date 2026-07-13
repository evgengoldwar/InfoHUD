package com.gtnewhorizons.infohud.hud.core.infolines;

import com.gtnewhorizons.infohud.configs.HudConfig;
import com.gtnewhorizons.infohud.hud.core.InfoLine;
import com.gtnewhorizons.infohud.mixins.early.MinecraftAccessor;

public class InfoFPS extends InfoLine {

    public InfoFPS(int order) {
        super(order);
    }

    @Override
    public String getLineString() {
        return tr("info_fps", getPlayer().getDisplayName(), ((MinecraftAccessor) mc).getFps());
    }

    @Override
    public String getItemName() {
        return HudConfig.hudItems.FpsItem;
    }

    @Override
    public boolean canRender() {
        return HudConfig.hudEnabled.FpsEnable;
    }
}
