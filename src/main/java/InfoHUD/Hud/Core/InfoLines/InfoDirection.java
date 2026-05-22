package InfoHUD.Hud.Core.InfoLines;

import net.minecraft.util.MathHelper;

import InfoHUD.Configs.HudConfig;
import InfoHUD.Hud.Core.InfoLine;

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
