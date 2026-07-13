package com.gtnewhorizons.infohud.hud.event;

import net.minecraft.client.Minecraft;

import com.gtnewhorizons.infohud.hud.core.DataStorage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class TickListener {

    private int tpsTimer = 20;

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent event) {
        if (tpsTimer == 0) {
            tpsTimer = 20;

            if (Minecraft.getMinecraft()
                .isSingleplayer()) {
                try {
                    DataStorage.getClientTPS();
                } catch (NullPointerException ignored) {}
            }
        }

        tpsTimer--;
    }
}
