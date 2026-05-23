package InfoHUD.ClassicBar;

import net.minecraftforge.common.MinecraftForge;

import InfoHUD.ClassicBar.Event.ClassicBarRenderEvent;
import InfoHUD.ClassicBar.Event.VanillaRenderEvent;

public class ClassicBar {

    public static void initEvent() {
        MinecraftForge.EVENT_BUS.register(new VanillaRenderEvent());
        MinecraftForge.EVENT_BUS.register(new ClassicBarRenderEvent());
    }
}
