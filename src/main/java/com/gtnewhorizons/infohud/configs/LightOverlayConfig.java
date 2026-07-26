package com.gtnewhorizons.infohud.configs;

import static com.gtnewhorizons.infohud.InfoHUD.MODID;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = MODID, category = "light", configSubDirectory = "InfoHUD", filename = "LightOverlayConfig")
@Config.LangKey("infohud.config.light.name")
public class LightOverlayConfig {

    @Config.DefaultBoolean(false)
    @Config.Order(1)
    public static boolean DisableFollowNumber;
}
