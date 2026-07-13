package com.gtnewhorizons.infohud.hud.core.infolines;

import com.gtnewhorizons.infohud.configs.HudConfig;
import com.gtnewhorizons.infohud.hud.HudUtils;
import com.gtnewhorizons.infohud.hud.core.InfoLine;
import com.gtnewhorizons.infohud.hud.event.BloodMagicEvent;

public class InfoBloodMagic extends InfoLine {

    public InfoBloodMagic(int order) {
        super(order);
    }

    @Override
    public String getLineString() {
        return tr("info_blood_magic", getCurrentLP(), getMaxLP());
    }

    private String getCurrentLP() {
        return String.format("%,d", HudUtils.getPlayerLPTag(getPlayer()));
    }

    private String getMaxLP() {
        return String.format(
            "%,d",
            Math.max(HudUtils.getPlayerMaxLPTag(getPlayer()), BloodMagicEvent.getMaxLP(getPlayer().getDisplayName())));
    }

    @Override
    public String getItemName() {
        return HudConfig.hudItems.LpItem;
    }

    @Override
    public boolean canRender() {
        return HudConfig.hudEnabled.LpEnable;
    }
}
