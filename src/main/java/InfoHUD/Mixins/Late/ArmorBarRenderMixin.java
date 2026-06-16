package InfoHUD.Mixins.Late;

import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import InfoHUD.Configs.ClassicBarConfig;
import locusway.overloadedarmorbar.overlay.ArmorBarRenderer;

@Mixin(value = ArmorBarRenderer.class, remap = false)
public class ArmorBarRenderMixin {

    @Inject(method = "onRenderGameOverlayEvent", at = @At("HEAD"), cancellable = true, remap = false)
    private void cancelArmorBarRender(RenderGameOverlayEvent.Pre event, CallbackInfo ci) {
        if (ClassicBarConfig.ClassicBarEnable) {
            ci.cancel();
        }
    }
}
