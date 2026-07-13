package com.gtnewhorizons.infohud.hud.core.infolines;

import com.gtnewhorizons.infohud.configs.HudConfig;
import com.gtnewhorizons.infohud.hud.core.DataStorage;
import com.gtnewhorizons.infohud.hud.core.InfoLine;

public class InfoPing extends InfoLine {

    public InfoPing(int order) {
        super(order);
    }

    @Override
    public boolean canRender() {
        return !mc.isSingleplayer() && HudConfig.hudEnabled.PingEnable;
    }

    @Override
    public String getLineString() {
        return tr("info_ping", DataStorage.getPlayerPing(getPlayer().getUniqueID()));
    }

    @Override
    public String getItemName() {
        return HudConfig.hudItems.PingItem;
    }
}
