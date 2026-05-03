package MinecraftImprovements.Hud.Core.InfoLines;

import MinecraftImprovements.Configs.HudConfig;
import MinecraftImprovements.Hud.Core.DataStorage;
import MinecraftImprovements.Hud.Core.InfoLine;
import MinecraftImprovements.Hud.HudUtils;

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
            } else if (world != null) {
                seed = world.getSeed();
            } else {
                return false;
            }
        }

        return HudUtils.isSlimeChunk(seed, playerMP, world);
    }
}
