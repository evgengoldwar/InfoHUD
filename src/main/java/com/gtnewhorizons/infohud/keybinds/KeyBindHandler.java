package com.gtnewhorizons.infohud.keybinds;

import static com.gtnewhorizons.infohud.utils.Utils.tr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.gtnewhorizons.infohud.InfoHUD;
import com.gtnewhorizons.infohud.hud.core.GuiHudEditor;
import com.gtnewhorizons.infohud.lightoverlay.LightLevelOverlayRenderer;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

public class KeyBindHandler {

    public static KeyBinding toggleLight = new KeyBinding(
        tr("infohud.keybind.desc.light_overlay"),
        Keyboard.KEY_L,
        InfoHUD.MODNAME);

    public static KeyBinding openEditGui = new KeyBinding(
        "infohud.keybind.desc.open_infoline_editor",
        Keyboard.KEY_J,
        InfoHUD.MODNAME);

    public KeyBindHandler() {
        ClientRegistry.registerKeyBinding(toggleLight);
        ClientRegistry.registerKeyBinding(openEditGui);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (toggleLight.isPressed()) {
            LightLevelOverlayRenderer.toggleMode();
        }

        if (openEditGui.isPressed()) {
            Minecraft.getMinecraft()
                .displayGuiScreen(new GuiHudEditor());
        }
    }
}
