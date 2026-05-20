package InfoHUD.Hud.Core.InfoLines;

import net.minecraft.stats.StatList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import InfoHUD.Configs.HudConfig;
import InfoHUD.Hud.Core.InfoLine;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class InfoJump extends InfoLine {

    protected static int jumpCount = 0;

    public InfoJump(int order) {
        super(order);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getLineString() {
        int jump = playerMP.getStatFileWriter()
            .writeStat(StatList.jumpStat) + jumpCount;
        return tr("info_jump", jump);
    }

    @Override
    public boolean canRender() {
        return HudConfig.hudEnabled.JumpEnable;
    }

    @Override
    public String getItemName() {
        return HudConfig.hudItems.JumpItem;
    }

    @SubscribeEvent
    public void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        if (event.entity == playerMP) {
            jumpCount++;
        }
    }
}
