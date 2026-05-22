package InfoHUD.Hud.Core.InfoLines;

import InfoHUD.Configs.HudConfig;
import InfoHUD.Hud.Core.DataStorage;
import InfoHUD.Hud.Core.InfoLine;
import InfoHUD.Hud.HudUtils;

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
            } else if (getPlayer().worldObj != null) {
                seed = getPlayer().worldObj.getSeed();
            } else {
                return false;
            }
        }

        return HudUtils.isSlimeChunk(seed, getPlayer(), getPlayer().worldObj);
    }
}
