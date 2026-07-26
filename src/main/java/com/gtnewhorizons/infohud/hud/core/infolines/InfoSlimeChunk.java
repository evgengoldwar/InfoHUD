package com.gtnewhorizons.infohud.hud.core.infolines;

import com.gtnewhorizons.infohud.configs.HudConfig;
import com.gtnewhorizons.infohud.hud.HudUtils;
import com.gtnewhorizons.infohud.hud.core.DataStorage;
import com.gtnewhorizons.infohud.hud.core.InfoLine;

public class InfoSlimeChunk extends InfoLine {

    public InfoSlimeChunk(int order) {
        super(order);
    }

    @Override
    public String getLineString() {
        return tr("info_slimechunk");
    }

    @Override
    public String getItemName() {
        return HudConfig.hudItems.SlimeChunkItem;
    }

    @Override
    public boolean canRender() {
        if (!HudConfig.hudEnabled.SlimeChunkEnable) {
            return false;
        }

        long seed = DataStorage.worldSeed;

        if (seed == -1) {
            if (mc.getIntegratedServer() != null) {
                seed = mc.getIntegratedServer().worldServers[0].getSeed();
            } else if (getWorld() != null) {
                seed = getWorld().getSeed();
            } else {
                return false;
            }
        }

        return HudUtils.isSlimeChunk(seed, getPlayer(), getWorld());
    }
}
