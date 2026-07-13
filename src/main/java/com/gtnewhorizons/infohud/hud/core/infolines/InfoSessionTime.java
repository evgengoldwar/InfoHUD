package com.gtnewhorizons.infohud.hud.core.infolines;

import com.gtnewhorizons.infohud.configs.HudConfig;
import com.gtnewhorizons.infohud.hud.core.DataStorage;
import com.gtnewhorizons.infohud.hud.core.InfoLine;

public class InfoSessionTime extends InfoLine {

    public InfoSessionTime(int order) {
        super(order);
    }

    @Override
    public String getLineString() {
        if (getPlayer() == null) {
            return tr("info_sessiontime.0", 0);
        }

        long elapsedNano = System.nanoTime() - DataStorage.getPlayerSessionStart(getPlayer().getUniqueID());
        long elapsedSeconds = elapsedNano / 1_000_000_000L;

        long hours = elapsedSeconds / 3600;
        long minutes = (elapsedSeconds % 3600) / 60;
        long seconds = elapsedSeconds % 60;

        if (hours > 0) {
            return tr("info_sessiontime.2", hours, minutes, seconds);
        } else if (minutes > 0) {
            return tr("info_sessiontime.1", minutes, seconds);
        } else {
            return tr("info_sessiontime.0", seconds);
        }
    }

    @Override
    public String getItemName() {
        return HudConfig.hudItems.SessionTimeItem;
    }

    @Override
    public boolean canRender() {
        return HudConfig.hudEnabled.SessionTimeEnable;
    }
}
