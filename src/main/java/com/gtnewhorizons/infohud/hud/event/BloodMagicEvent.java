package com.gtnewhorizons.infohud.hud.event;

import java.util.HashMap;
import java.util.Map;

import WayofTime.alchemicalWizardry.api.event.AddToNetworkEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class BloodMagicEvent {

    public static final Map<String, Integer> playerMaxLP = new HashMap<>();

    @SubscribeEvent
    public void onAddToNetwork(AddToNetworkEvent event) {
        if (event.maximum == 700_000_000) return;
        playerMaxLP.put(event.ownerNetwork, event.maximum);
    }

    public static int getMaxLP(String playerName) {
        return playerMaxLP.getOrDefault(playerName, 0);
    }
}
