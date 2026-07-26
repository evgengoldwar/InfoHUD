package com.gtnewhorizons.infohud.classicbar.event;

import java.util.Objects;

import net.minecraftforge.client.event.RenderGameOverlayEvent;

import com.gtnewhorizons.infohud.configs.ClassicBarConfig;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class VanillaRenderEvent {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderPre(RenderGameOverlayEvent.Pre event) {
        if (!ClassicBarConfig.ClassicBarEnable) return;

        switch (Objects.requireNonNull(event.type)) {
            case HEALTH, AIR, FOOD, ARMOR -> event.setCanceled(true);
        }
    }
}
