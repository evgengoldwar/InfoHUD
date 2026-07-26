package com.gtnewhorizons.infohud.mixins.early;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraftforge.client.GuiIngameForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizons.infohud.hud.core.HudRenderer;

@Mixin(value = GuiIngameForge.class)
public class GuiIngameMixin extends GuiIngame {

    public GuiIngameMixin(Minecraft p_i1036_1_) {
        super(p_i1036_1_);
    }

    @Inject(
        method = "renderHUDText",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/settings/GameSettings;showDebugInfo:Z",
            shift = At.Shift.BEFORE))
    private void renderHud(int width, int height, CallbackInfo ci) {
        HudRenderer.renderHud(width, height);
    }
}
