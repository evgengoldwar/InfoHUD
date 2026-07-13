package com.gtnewhorizons.infohud.mixins.early;

import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizons.infohud.hud.core.ServerSender;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    public void onServerTick(CallbackInfo ci) {
        ServerSender.onServerTick((MinecraftServer) (Object) this);
    }
}
