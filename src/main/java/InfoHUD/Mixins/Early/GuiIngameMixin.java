package InfoHUD.Mixins.Early;

import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.GuiIngameForge;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import InfoHUD.Configs.HudConfig;
import InfoHUD.Hud.Core.BackhandHandler;
import InfoHUD.Hud.Core.InfoLine;
import InfoHUD.Hud.Core.InfoLines.InfoCountItem;
import InfoHUD.Hud.Hud;

@Mixin(value = GuiIngameForge.class)
public class GuiIngameMixin extends GuiIngame {

    @Shadow
    private FontRenderer fontrenderer;

    public GuiIngameMixin(Minecraft mc) {
        super(mc);
    }

    @Inject(
        method = "renderHUDText",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/settings/GameSettings;showDebugInfo:Z",
            shift = At.Shift.BEFORE))
    private void renderHud(int width, int height, CallbackInfo ci) {
        if (mc.currentScreen != null) return;

        if (mc.gameSettings.showDebugInfo) return;

        if (HudConfig.hudGeneral.HudDisable) return;

        int hudY = HudConfig.hudGeneral.HudY;

        List<InfoLine> orderedLines = Hud.lines;

        orderedLines.sort(Comparator.comparingInt(InfoLine::getOrder));

        for (InfoLine line : orderedLines) {
            if (!line.canRender()) continue;

            if (line instanceof InfoCountItem infoCountItem) {
                drawHeldItemCounter(infoCountItem, width, height);
                continue;
            }

            drawHudInfo(line.getLineString(), HudConfig.hudGeneral.HudX, hudY, line.getChachedItemStack());
            hudY += 11;
        }
    }

    @Unique
    private void drawHudInfo(String string, int x, int y, ItemStack itemStack) {
        GL11.glPushMatrix();
        float scaleHud = HudConfig.hudGeneral.HudScale;
        GL11.glScalef(scaleHud, scaleHud, scaleHud);

        FontRenderer fr = mc.fontRenderer;

        if (itemStack != null) {
            GL11.glPushMatrix();

            float scaleItem = 0.5F;
            GL11.glScalef(scaleItem, scaleItem, scaleItem);

            int scaledX = (int) ((x + 2) / scaleItem);
            int scaledY = (int) ((y + 2) / scaleItem) + 3;

            RenderHelper.enableGUIStandardItemLighting();

            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);

            RenderItem renderItem = RenderItem.getInstance();
            renderItem.zLevel = 100.0F;

            renderItem.renderItemAndEffectIntoGUI(fr, mc.renderEngine, itemStack, scaledX, scaledY);

            renderItem.zLevel = 0.0F;

            GL11.glDisable(GL11.GL_LIGHTING);

            GL11.glPopMatrix();

            fr.drawStringWithShadow(string, x + 12, y + 4, 14737632);
        } else {
            fr.drawStringWithShadow(string, x + 4, y + 4, 14737632);
        }

        GL11.glPopMatrix();
    }

    @Unique
    private void drawHeldItemCounter(InfoCountItem line, int width, int height) {

        ItemStack heldItem = mc.thePlayer.getHeldItem();

        if (heldItem == null) {
            return;
        }

        String text = line.getLineString();

        FontRenderer fr = mc.fontRenderer;
        RenderItem renderItem = RenderItem.getInstance();

        int hotbarX = width / 2 - 91;

        int x = hotbarX - 22;

        if (BackhandHandler.isBackhandAvailable() && BackhandHandler.getOffhandItem(mc.thePlayer) != null) {
            x = hotbarX - 55;
        }

        int y = height - 24;

        GL11.glPushMatrix();

        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);

        renderItem.zLevel = 100.0F;
        renderItem.renderItemAndEffectIntoGUI(fr, mc.renderEngine, heldItem, x, y);
        renderItem.zLevel = 0.0F;

        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glPushMatrix();

        float scaleText = 0.6F;

        int textWidth = fr.getStringWidth(text);
        float centeredX = x + 8 - (textWidth * scaleText) / 2;
        float textY = y + 17;

        GL11.glTranslatef(centeredX, textY, 0);
        GL11.glScalef(scaleText, scaleText, scaleText);

        fr.drawStringWithShadow(text, 0, 0, 16777215);

        GL11.glPopMatrix();

        GL11.glPopMatrix();
    }
}
