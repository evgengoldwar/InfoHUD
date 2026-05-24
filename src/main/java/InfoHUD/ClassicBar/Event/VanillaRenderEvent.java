package InfoHUD.ClassicBar.Event;

import java.util.Objects;

import net.minecraftforge.client.event.RenderGameOverlayEvent;

import InfoHUD.Configs.ClassicBarConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class VanillaRenderEvent {

    @SubscribeEvent
    public void onRenderPre(RenderGameOverlayEvent.Pre event) {
        if (!ClassicBarConfig.ClassicBarEnable) return;

        switch (Objects.requireNonNull(event.type)) {
            case HEALTH, AIR, FOOD, ARMOR -> event.setCanceled(true);
        }
    }
}
