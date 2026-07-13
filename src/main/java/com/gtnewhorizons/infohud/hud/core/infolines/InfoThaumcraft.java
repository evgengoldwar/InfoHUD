package com.gtnewhorizons.infohud.hud.core.infolines;

import static com.gtnewhorizons.infohud.hud.HudUtils.getWarpPerm;
import static com.gtnewhorizons.infohud.hud.HudUtils.getWarpSticky;
import static com.gtnewhorizons.infohud.hud.HudUtils.getWarpTemp;
import static com.gtnewhorizons.infohud.hud.HudUtils.getWarpTotal;

import com.gtnewhorizons.infohud.configs.HudConfig;
import com.gtnewhorizons.infohud.hud.core.InfoLine;

public class InfoThaumcraft extends InfoLine {

    public InfoThaumcraft(int order) {
        super(order);
    }

    @Override
    public String getLineString() {
        return tr(
            "info_warp",
            getWarpTotal(getPlayer()),
            getWarpPerm(getPlayer()),
            getWarpSticky(getPlayer()),
            getWarpTemp(getPlayer()));
    }

    @Override
    public boolean canRender() {
        return HudConfig.hudEnabled.WarpEnable;
    }

    @Override
    public String getItemName() {
        return HudConfig.hudItems.WarpItem;
    }
}
