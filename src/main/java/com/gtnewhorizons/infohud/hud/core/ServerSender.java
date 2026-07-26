package com.gtnewhorizons.infohud.hud.core;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.gtnewhorizons.infohud.InfoHUD;

import io.netty.buffer.Unpooled;

public class ServerSender {

    private static int tickCounter = 0;
    private static boolean seedSent = false;

    public static void onServerTick(MinecraftServer server) {
        tickCounter++;

        if (tickCounter % 20 == 0) {
            sendTPSAndMem(server);
        }

        sendSeed(server);
    }

    private static void sendTPSAndMem(MinecraftServer server) {
        if (DataStorage.tpsSubscribers.isEmpty() && DataStorage.memSubscribers.isEmpty()) {
            return;
        }

        double meanTickTime = MathHelper.average(server.tickTimeArray) * 1.0E-6D;
        double tps = Math.min(1000.0D / meanTickTime, 20.0D);

        Runtime runtime = Runtime.getRuntime();
        long usedMem = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMem = runtime.maxMemory() / 1024 / 1024;
        long allocatedMem = runtime.totalMemory() / 1024 / 1024;

        for (EntityPlayerMP playerMP : server.getConfigurationManager().playerEntityList) {

            if (DataStorage.tpsSubscribers.contains(playerMP.getUniqueID())) {
                sendTPSPacket(playerMP, tps, meanTickTime);
            }

            if (DataStorage.memSubscribers.contains(playerMP.getUniqueID())) {
                sendMemPacket(playerMP, usedMem, maxMem, allocatedMem);
            }
        }
    }

    private static void sendSeed(MinecraftServer server) {
        if (seedSent || DataStorage.seedSubscribers.isEmpty()) {
            return;
        }

        World world = server.worldServers[0];
        long seed = world.getSeed();
        DataStorage.worldSeed = seed;

        for (EntityPlayerMP player : server.getConfigurationManager().playerEntityList) {
            if (DataStorage.seedSubscribers.contains(player.getUniqueID())) {
                sendSeedPacket(player, seed);
            }
        }

        seedSent = true;
    }

    private static void sendTPSPacket(EntityPlayerMP player, double tps, double mspt) {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeDouble(round(tps));
        buffer.writeDouble(round(mspt));

        S3FPacketCustomPayload packet = new S3FPacketCustomPayload(InfoHUD.NETWORK_MODID + "HUD|TPS", buffer);

        player.playerNetServerHandler.sendPacket(packet);
    }

    private static void sendMemPacket(EntityPlayerMP player, long used, long max, long allocated) {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeInt((int) used);
        buffer.writeInt((int) max);
        buffer.writeInt((int) allocated);

        S3FPacketCustomPayload packet = new S3FPacketCustomPayload(InfoHUD.NETWORK_MODID + "HUD|Mem", buffer);

        player.playerNetServerHandler.sendPacket(packet);
    }

    private static void sendSeedPacket(EntityPlayerMP player, long seed) {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeLong(seed);

        S3FPacketCustomPayload packet = new S3FPacketCustomPayload(InfoHUD.NETWORK_MODID + "HUD|Seed", buffer);

        player.playerNetServerHandler.sendPacket(packet);
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
