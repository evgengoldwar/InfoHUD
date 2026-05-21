package InfoHUD.Hud.Core.InfoLines;

import net.minecraft.client.Minecraft;
import net.minecraft.stats.StatList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import InfoHUD.Configs.HudConfig;
import InfoHUD.Hud.Core.InfoLine;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class InfoJump extends InfoLine {

    private static int jumpCount = 0;
    private static boolean registered = false;

    public InfoJump(int order) {
        super(order);
        registerEventHandler();
        jumpCount = 0;
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

    private static void registerEventHandler() {
        if (!registered) {
            MinecraftForge.EVENT_BUS.register(new JumpEvent());
            registered = true;
        }
    }

    public static class JumpEvent {

        @SubscribeEvent
        public void onPlayerJump(LivingEvent.LivingJumpEvent event) {
            if (event.entity == Minecraft.getMinecraft().thePlayer) {
                jumpCount++;
            }
        }
    }
}
