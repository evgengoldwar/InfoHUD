package InfoHUD.Hud.Core.InfoLines;

import InfoHUD.Configs.HudConfig;
import InfoHUD.Hud.Core.InfoLine;
import InfoHUD.Hud.Event.BloodMagicEvent;
import InfoHUD.Hud.HudUtils;

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
