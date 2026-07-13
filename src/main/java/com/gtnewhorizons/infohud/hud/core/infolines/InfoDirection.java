package com.gtnewhorizons.infohud.hud.core.infolines;

import net.minecraft.util.MathHelper;

import com.gtnewhorizons.infohud.configs.HudConfig;
import com.gtnewhorizons.infohud.hud.core.InfoLine;

public class InfoDirection extends InfoLine {

    public InfoDirection(int order) {
        super(order);
    }

    @Override
    public String getLineString() {
        return tr(
            "info_direction",
            ROUGHDIRECTION[MathHelper.floor_double(getPlayer().rotationYaw * 4.0 / 360.0 + 0.5) & 3]);
    }

    @Override
    public boolean canRender() {
        return HudConfig.hudEnabled.DirectionEnable;
    }

    @Override
    public String getItemName() {
        return HudConfig.hudItems.DirectionItem;
    }
}
