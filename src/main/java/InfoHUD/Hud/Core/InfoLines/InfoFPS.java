package InfoHUD.Hud.Core.InfoLines;

import InfoHUD.Configs.HudConfig;
import InfoHUD.Hud.Core.InfoLine;
import InfoHUD.Mixins.Early.MinecraftAccessor;

public class InfoFPS extends InfoLine {

    public InfoFPS(int order) {
        super(order);
    }

    @Override
    public String getLineString() {
        return tr("info_fps", getPlayer().getDisplayName(), ((MinecraftAccessor) this.mc).getFps());
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
