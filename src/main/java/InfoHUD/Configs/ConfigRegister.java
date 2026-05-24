package InfoHUD.Configs;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

public class ConfigRegister {

    public static void init() {
        register(HudConfig.class);
        register(GeneralConfig.class);
        register(LightOverlayConfig.class);
        register(ClassicBarConfig.class);
    }

    private static void register(Class<?> configClass) {
        try {
            ConfigurationManager.registerConfig(configClass);
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
    }
}
