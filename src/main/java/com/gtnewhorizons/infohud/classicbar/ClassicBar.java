package com.gtnewhorizons.infohud.classicbar;

import net.minecraftforge.common.MinecraftForge;

import com.gtnewhorizons.infohud.classicbar.event.ClassicBarRenderEvent;
import com.gtnewhorizons.infohud.classicbar.event.VanillaRenderEvent;

public class ClassicBar {

    public static void initEvent() {
        MinecraftForge.EVENT_BUS.register(new VanillaRenderEvent());
        MinecraftForge.EVENT_BUS.register(new ClassicBarRenderEvent());
    }
}
