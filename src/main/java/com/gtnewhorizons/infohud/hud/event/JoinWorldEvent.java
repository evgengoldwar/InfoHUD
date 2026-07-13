package com.gtnewhorizons.infohud.hud.event;

import java.util.UUID;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import com.gtnewhorizons.infohud.hud.core.DataStorage;
import com.gtnewhorizons.infohud.hud.Hud;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class JoinWorldEvent {

    private boolean needsInit = false;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        needsInit = true;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        needsInit = true;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (needsInit && event.entity instanceof EntityClientPlayerMP playerMP) {
            needsInit = false;

            UUID uuid = playerMP.getUniqueID();
            DataStorage.initPlayer(uuid);
            DataStorage.reset();
            BloodMagicEvent.playerMaxLP.remove(playerMP.getDisplayName());
            Hud.initLines();

            playerMP.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS));
        }
    }
}
