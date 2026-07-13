package com.gtnewhorizons.infohud.hud.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

import com.gtnewhorizons.infohud.InfoHUD;
import io.netty.buffer.Unpooled;

public class OSLHandshakePayload {

    public static final String CHANNEL = InfoHUD.NETWORK_MODID + "HUD";

    public static C17PacketCustomPayload getHandshake() {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());

        List<String> channels = new ArrayList<>();

        channels.add(CHANNEL + "|TPS");
        channels.add(CHANNEL + "|Mem");
        channels.add(CHANNEL + "|Seed");

        for (String channel : channels) {
            try {
                buffer.writeStringToBuffer(channel);
            } catch (IOException ignored) {}
        }

        return new C17PacketCustomPayload("OSL|Handshake", buffer);
    }
}
