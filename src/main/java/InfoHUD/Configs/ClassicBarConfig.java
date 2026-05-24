package InfoHUD.Configs;

import com.gtnewhorizon.gtnhlib.config.Config;

import static InfoHUD.InfoHUD.MODID;

@Config(modid = MODID, category = "classicbar", configSubDirectory = "InfoHUD", filename = "LightOverlayConfig")
@Config.LangKey("infohud.config.classic_bar.name")
public class ClassicBarConfig {

    @Config.DefaultBoolean(false)
    @Config.Order(1)
    public static boolean ClassicBarEnable;
}
