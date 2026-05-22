package InfoHUD.Hud.Core.InfoLines;

import static InfoHUD.Hud.HudUtils.getWarpPerm;
import static InfoHUD.Hud.HudUtils.getWarpSticky;
import static InfoHUD.Hud.HudUtils.getWarpTemp;
import static InfoHUD.Hud.HudUtils.getWarpTotal;

import InfoHUD.Configs.HudConfig;
import InfoHUD.Hud.Core.InfoLine;

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
