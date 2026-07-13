package com.gtnewhorizons.infohud.mixins.early;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizons.infohud.hud.core.DataStorage;
import com.gtnewhorizons.infohud.hud.network.OSLHandshakePayload;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

@Mixin(value = NetHandlerPlayClient.class)
public class NetHandlerPlayerClientMixin {

    @Shadow
    @Final
    private NetworkManager netManager;

    @Inject(method = "handleJoinGame", at = @At("TAIL"))
    private void sendHandshake(CallbackInfo ci) {
        C17PacketCustomPayload packet = OSLHandshakePayload.getHandshake();
        this.netManager.scheduleOutboundPacket(packet);
    }

    @Inject(method = "handlePlayerListItem", at = @At("HEAD"))
    private void getPing(S38PacketPlayerListItem packet, CallbackInfo ci) {
        if (packet == null || packet.func_149122_c() == null) {
            return;
        }
        if (packet.func_149122_c()
            .equals(Minecraft.getMinecraft().thePlayer.getDisplayName())) {
            DataStorage.setPlayerPing(Minecraft.getMinecraft().thePlayer.getUniqueID(), packet.func_149120_e());
        }
    }

    @Inject(method = "handleCustomPayload", at = @At("HEAD"))
    private void handlePacket(S3FPacketCustomPayload packet, CallbackInfo ci) {
        if (packet == null || packet.func_149169_c() == null) {
            return;
        }

        String channel = packet.func_149169_c();

        if (channel.equals(OSLHandshakePayload.CHANNEL + "|TPS")) {
            ByteBuf bytebuf = Unpooled.wrappedBuffer(packet.func_149168_d());
            try {
                double tps = bytebuf.readDouble();
                double mspt = bytebuf.readDouble();
                DataStorage.tps = tps;
                DataStorage.mspt = mspt;
            } catch (Exception ignored) {}
        }

        if (channel.equals(OSLHandshakePayload.CHANNEL + "|Mem")) {
            ByteBuf bytebuf = Unpooled.wrappedBuffer(packet.func_149168_d());
            try {
                int used = bytebuf.readInt();
                int max = bytebuf.readInt();
                int allocated = bytebuf.readInt();
                DataStorage.serverMemUsed = used;
                DataStorage.serverMemMax = max;
                DataStorage.serverMemAllocated = allocated;
            } catch (Exception ignored) {}
        }

        if (channel.equals(OSLHandshakePayload.CHANNEL + "|Seed")) {
            ByteBuf byteBuf = Unpooled.wrappedBuffer(packet.func_149168_d());
            try {
                DataStorage.worldSeed = byteBuf.readLong();
            } catch (Exception ignored) {}
        }
    }
}
