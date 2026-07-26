package com.gtnewhorizons.infohud.configs;

import static com.gtnewhorizons.infohud.InfoHUD.MODID;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = MODID, configSubDirectory = "InfoHUD", filename = "GeneralConfig")
@Config.LangKey("infohud.config.general.name")
public class GeneralConfig {

    @Config.DefaultBoolean(true)
    @Config.Order(1)
    public static boolean DisablePotionEffect;
}
