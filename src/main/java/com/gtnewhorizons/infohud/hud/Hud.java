package com.gtnewhorizons.infohud.hud;

import net.minecraftforge.common.MinecraftForge;

import com.gtnewhorizons.infohud.hud.event.BloodMagicEvent;
import com.gtnewhorizons.infohud.hud.event.JoinWorldEvent;
import com.gtnewhorizons.infohud.hud.event.TickListener;
import com.gtnewhorizons.infohud.hud.tags.TagRegistry;
import com.gtnewhorizons.infohud.hud.tags.VanillaTags;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;

public class Hud {

    public static void initEvent() {
        FMLCommonHandler.instance()
            .bus()
            .register(new TickListener());
        JoinWorldEvent joinWorldEvent = new JoinWorldEvent();
        MinecraftForge.EVENT_BUS.register(joinWorldEvent);
        FMLCommonHandler.instance()
            .bus()
            .register(joinWorldEvent);

        if (Loader.isModLoaded(HudUtils.BLOOD_MAGIC_ID)) {
            MinecraftForge.EVENT_BUS.register(new BloodMagicEvent());
        }

        TagRegistry.init();
    }

    public static void onWorldJoin() {
        VanillaTags.resetSession();
    }
}
