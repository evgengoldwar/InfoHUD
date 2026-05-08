package InfoHUD.KeyBinds;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import InfoHUD.InfoHUD;
import InfoHUD.LightOverlay.LightLevelOverlayRenderer;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

import static InfoHUD.Utils.Utils.tr;


public class KeyBindHandler {

    public static KeyBinding toggleLight = new KeyBinding(tr("infohud.keybind.desc.light_overlay"), Keyboard.KEY_L, InfoHUD.MODNAME);

    public KeyBindHandler() {
        ClientRegistry.registerKeyBinding(toggleLight);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (toggleLight.isPressed()) {
            LightLevelOverlayRenderer.toggleMode();
        }
    }
}
