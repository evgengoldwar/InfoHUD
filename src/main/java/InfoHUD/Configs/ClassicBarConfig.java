package InfoHUD.Configs;

import static InfoHUD.InfoHUD.MODID;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = MODID, category = "classicbar", configSubDirectory = "InfoHUD", filename = "LightOverlayConfig")
@Config.LangKey("infohud.config.classic_bar.name")
public class ClassicBarConfig {

    @Config.DefaultBoolean(false)
    @Config.Order(1)
    public static boolean ClassicBarEnable;
}
