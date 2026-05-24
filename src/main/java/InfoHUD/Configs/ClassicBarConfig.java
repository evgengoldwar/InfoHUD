package InfoHUD.Configs;

import static InfoHUD.InfoHUD.MODID;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = MODID, category = "classicbar", configSubDirectory = "InfoHUD", filename = "LightOverlayConfig")
@Config.LangKey("infohud.config.classic_bar.name")
public class ClassicBarConfig {

    @Config.DefaultBoolean(false)
    @Config.Order(1)
    public static boolean ClassicBarEnable;

    @Config.DefaultFloat(1.0F)
    @Config.RangeFloat(min = 0.5F, max = 2.0F)
    @Config.Order(2)
    public static float ClassicBarScale;
}
