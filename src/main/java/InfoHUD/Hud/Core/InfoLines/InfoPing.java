package InfoHUD.Hud.Core.InfoLines;

import InfoHUD.Configs.HudConfig;
import InfoHUD.Hud.Core.DataStorage;
import InfoHUD.Hud.Core.InfoLine;

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
