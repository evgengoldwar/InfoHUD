package InfoHUD.Mixins.Early;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import InfoHUD.Configs.ClassicBarConfig;
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

        float scaleHud = HudConfig.hudGeneral.HudScale;
        int hudY = HudConfig.hudGeneral.HudY;

        if (HudConfig.hudPotion.PotionEnable) {
            int potionsHeight = drawActivePotions(HudConfig.hudGeneral.HudX + 5, hudY + 2);

            if (potionsHeight > 0) {
                hudY += potionsHeight + (int) (1 * scaleHud);
            }
        }

        List<InfoLine> orderedLines = Hud.lines;
        orderedLines.sort(Comparator.comparingInt(InfoLine::getOrder));

        for (InfoLine line : orderedLines) {
            if (!line.canRender()) continue;

            if (line instanceof InfoCountItem infoCountItem) {
                drawHeldItemCounter(infoCountItem, width, height);
                continue;
            }

            drawHudInfo(line.getLineString(), HudConfig.hudGeneral.HudX, hudY, line.getChachedItemStack());
            hudY += (int) (11 * scaleHud);
        }
    }

    @Unique
    private int drawActivePotions(int x, int y) {
        if (mc.thePlayer == null) return 0;

        Collection<PotionEffect> activePotions = mc.thePlayer.getActivePotionEffects();
        if (activePotions.isEmpty()) return 0;

        FontRenderer fr = mc.fontRenderer;
        float scaleHud = HudConfig.hudGeneral.HudScale;

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(scaleHud, scaleHud, scaleHud);
        GL11.glTranslatef(-x, -y, 0);

        List<PotionEffect> sortedEffects = new ArrayList<>(activePotions);
        sortedEffects.sort(Comparator.comparingInt(PotionEffect::getPotionID));

        int currentX = x;
        int iconSize = 18;
        int spacing = 1;
        int levelWidth = fr.getStringWidth("00") + 2;

        for (PotionEffect effect : sortedEffects) {
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (potion == null || !potion.hasStatusIcon()) continue;

            if (currentX + iconSize + levelWidth > mc.displayWidth / scaleHud) break;

            drawPotionIcon(potion, currentX, y, iconSize);

            int duration = effect.getDuration() / 20;
            String timeString;
            if (duration > 1800) {
                timeString = "∞";
            } else {
                int minutes = duration / 60;
                int seconds = duration % 60;
                timeString = String.format("%d:%02d", minutes, seconds);
            }

            String levelString = getLevel(effect.getAmplifier() + 1);

            int timeWidth = fr.getStringWidth(timeString);
            int timeX = currentX + (iconSize / 2) - (timeWidth / 2);

            if (HudConfig.hudPotion.TimeEnable) {
                fr.drawStringWithShadow(timeString, timeX, y + iconSize + 1, 0xFFFFFF);
            }

            if (!levelString.isEmpty() && HudConfig.hudPotion.LevelEnable) {
                fr.drawStringWithShadow(levelString, currentX + iconSize + 3, y + (iconSize / 2) - 4, 0xFFAA00);
            }

            currentX += iconSize + spacing + levelWidth;
        }

        GL11.glPopMatrix();

        int totalHeight = iconSize + (HudConfig.hudPotion.TimeEnable ? 10 : 0) + 1;
        return (int) (totalHeight * scaleHud);
    }

    @Unique
    private void drawPotionIcon(Potion potion, int x, int y, int size) {
        ResourceLocation inventoryTexture = new ResourceLocation("textures/gui/container/inventory.png");
        mc.renderEngine.bindTexture(inventoryTexture);

        int iconIndex = potion.getStatusIconIndex();
        int textureX = iconIndex % 8 * 18;
        int textureY = 198 + iconIndex / 8 * 18;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + size, 0, textureX / 256.0, (textureY + 18) / 256.0);
        tessellator.addVertexWithUV(x + size, y + size, 0, (textureX + 18) / 256.0, (textureY + 18) / 256.0);
        tessellator.addVertexWithUV(x + size, y, 0, (textureX + 18) / 256.0, textureY / 256.0);
        tessellator.addVertexWithUV(x, y, 0, textureX / 256.0, textureY / 256.0);
        tessellator.draw();

        GL11.glDisable(GL11.GL_BLEND);
    }

    @Unique
    private String getLevel(int level) {
        if (level <= 1) return "";
        return String.valueOf(level);
    }

    @Unique
    private void drawHudInfo(String string, int x, int y, ItemStack itemStack) {
        GL11.glPushMatrix();
        float scaleHud = HudConfig.hudGeneral.HudScale;

        FontRenderer fr = mc.fontRenderer;

        if (itemStack != null) {
            GL11.glTranslatef(x, y, 0);
            GL11.glScalef(scaleHud, scaleHud, scaleHud);
            GL11.glTranslatef(-x, -y, 0);

            GL11.glPushMatrix();

            float scaleItem = 0.5F;
            GL11.glTranslatef(x + 2, y + 2, 0);
            GL11.glScalef(scaleItem, scaleItem, scaleItem);
            GL11.glTranslatef(-(x + 2), -(y + 2), 0);

            int scaledX = x + 2;
            int scaledY = y + 5;

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
            GL11.glTranslatef(x, y, 0);
            GL11.glScalef(scaleHud, scaleHud, scaleHud);
            GL11.glTranslatef(-x, -y, 0);

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

        if (BackhandHandler.isLoaded() && BackhandHandler.getBackHandItemStack(mc.thePlayer) != null) {
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

    @Inject(
        method = "renderGameOverlay",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/client/GuiIngameForge;renderToolHightlight(II)V",
            ordinal = 0),
        cancellable = true)
    private void onRenderToolHighlight(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        if (ClassicBarConfig.ClassicBarEnable) {
            ci.cancel();
        }
    }
}
