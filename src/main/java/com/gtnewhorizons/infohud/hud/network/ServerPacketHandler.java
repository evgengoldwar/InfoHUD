package com.gtnewhorizons.infohud.hud.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

import io.netty.buffer.Unpooled;

public class ServerPacketHandler {

    public static List<String> readHandshakeChannels(C17PacketCustomPayload packet) {
        List<String> channels = new ArrayList<>();

        if (!"OSL|Handshake".equals(packet.func_149559_c())) {
            return channels;
        }

        PacketBuffer buffer = new PacketBuffer(Unpooled.wrappedBuffer(packet.func_149558_e()));

        try {
            while (buffer.readableBytes() > 0) {
                String channel = buffer.readStringFromBuffer(256);
                channels.add(channel);
            }
        } catch (IOException ignored) {} finally {
            buffer.release();
        }

        return channels;
    }
}
