package com.gtnewhorizons.infohud.hud.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.gtnewhorizons.infohud.configs.HudConfig;
import com.gtnewhorizons.infohud.hud.Hud;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoCountItem;
import com.gtnewhorizons.infohud.utils.Position;

public class HudRenderer {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void renderHud(int screenWidth, int screenHeight) {
        if (mc.currentScreen != null) return;
        if (mc.gameSettings.showDebugInfo) return;
        if (HudConfig.hudGeneral.HudDisable) return;

        float scaleHud = HudConfig.hudGeneral.HudScale;

        DataStorage.HudPositionsData cachedData = DataStorage.getCachedPositions();

        int effectX = cachedData != null ? cachedData.effectX : HudConfig.hudPotion.PotionX;
        int effectY = cachedData != null ? cachedData.effectY : HudConfig.hudPotion.PotionY;

        if (HudConfig.hudPotion.PotionEnable) {
            drawPotions(effectX, effectY, scaleHud, false);
        }

        List<InfoLine> orderedLines = Hud.lines;
        orderedLines.sort(Comparator.comparingInt(InfoLine::getOrder));
        int hudY = 40;
        int countItemX = screenWidth / 2 - 91 - 22;
        int countItemY = screenHeight - 24;

        for (InfoLine line : orderedLines) {
            if (!line.canRender()) continue;

            if (line instanceof InfoCountItem infoCountItem) {
                int cX = countItemX;
                int cY = countItemY;

                if (cachedData != null && cachedData.countItemX != -1 && cachedData.countItemY != -1) {
                    cX = cachedData.countItemX;
                    cY = cachedData.countItemY;
                } else if (infoCountItem.position != null && infoCountItem.position.isEdit()) {
                    cX = infoCountItem.position.getX();
                    cY = infoCountItem.position.getY();
                }

                drawCountItem(line.getLineString(), cX, cY);
                continue;
            }

            if (line.position == null || !line.position.isEdit()) {
                if (cachedData != null) {
                    DataStorage.LinePosition cachedPos = cachedData.linePositions.get(
                        line.getClass()
                            .getSimpleName());
                    if (cachedPos != null && cachedPos.isSet) {
                        line.position = new Position(cachedPos.x, cachedPos.y);
                        line.position.setEdit();
                    } else {
                        line.position = new Position(0, hudY);
                        hudY += (int) (11 * scaleHud);
                    }
                } else {
                    line.position = new Position(0, hudY);
                    hudY += (int) (11 * scaleHud);
                }
            }

            drawLine(
                line.getLineString(),
                line.position.getX(),
                line.position.getY(),
                line.getChachedItemStack(),
                scaleHud,
                false,
                null);
        }
    }

    public static void renderEditor(List<InfoLine> lines, float scaleHud, int effectX, int effectY, int countItemX,
        int countItemY, InfoLine draggingLine, InfoCountItem draggingCountItem) {

        if (HudConfig.hudPotion.PotionEnable) {
            drawPotions(effectX, effectY, scaleHud, true);
        }

        List<InfoLine> orderedLines = new ArrayList<>(lines);
        orderedLines.sort(Comparator.comparingInt(InfoLine::getOrder));
        int hudY = 40;

        for (InfoLine line : orderedLines) {
            if (!line.canRender()) continue;

            if (line instanceof InfoCountItem infoCountItem) {
                if (infoCountItem.position != null && infoCountItem.position.isEdit()) {
                    if (draggingCountItem != null) {
                        HudRenderer.drawRect(
                            infoCountItem.position.getX() - 2,
                            infoCountItem.position.getY() - 1,
                            infoCountItem.position.getX() + 20,
                            infoCountItem.position.getY() + 25,
                            0x40FFFF00);
                    } else {
                        HudRenderer.drawRect(
                            infoCountItem.position.getX() - 2,
                            infoCountItem.position.getY() - 1,
                            infoCountItem.position.getX() + 20,
                            infoCountItem.position.getY() + 25,
                            0x4000FFFF);
                    }

                    drawCountItem(line.getLineString(), infoCountItem.position.getX(), infoCountItem.position.getY());
                } else {
                    if (draggingCountItem != null) {
                        HudRenderer
                            .drawRect(countItemX - 2, countItemY - 1, countItemX + 20, countItemY + 25, 0x40FFFF00);
                    } else {
                        HudRenderer
                            .drawRect(countItemX - 2, countItemY - 1, countItemX + 20, countItemY + 25, 0x4000FFFF);
                    }

                    drawCountItem(line.getLineString(), countItemX, countItemY);
                }

                continue;
            }

            if (line.position == null || !line.position.isEdit()) {
                line.position = new Position(0, hudY);
                hudY += (int) (11 * scaleHud);
            }

            drawLine(
                line.getLineString(),
                line.position.getX(),
                line.position.getY(),
                line.getChachedItemStack(),
                scaleHud,
                true,
                line.equals(draggingLine) ? line : null);
        }
    }

    private static void drawLine(String text, int x, int y, ItemStack icon, float scaleHud, boolean editorMode,
        InfoLine dragContext) {
        GL11.glPushMatrix();
        FontRenderer fr = mc.fontRenderer;

        if (icon != null) {
            GL11.glTranslatef(x, y, 0);
            GL11.glScalef(scaleHud, scaleHud, scaleHud);
            GL11.glTranslatef(-x, -y, 0);

            if (editorMode) {
                int lineWidth = fr.getStringWidth(text);
                if (dragContext != null) {
                    drawRect(x - 2, y + 1, x + lineWidth + 16, y + 14, 0x40FFFF00);
                }
            }

            GL11.glPushMatrix();
            float scaleItem = 0.5F;
            GL11.glTranslatef(x + 2, y + 2, 0);
            GL11.glScalef(scaleItem, scaleItem, scaleItem);
            GL11.glTranslatef(-(x + 2), -(y + 2), 0);

            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);

            RenderItem renderItem = RenderItem.getInstance();
            renderItem.zLevel = 100.0F;
            renderItem.renderItemAndEffectIntoGUI(fr, mc.renderEngine, icon, x + 2, y + 5);
            renderItem.zLevel = 0.0F;

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();

            fr.drawStringWithShadow(text, x + 12, y + 4, 14737632);
        } else {
            GL11.glTranslatef(x, y, 0);
            GL11.glScalef(scaleHud, scaleHud, scaleHud);
            GL11.glTranslatef(-x, -y, 0);

            if (editorMode && dragContext != null) {
                int lineWidth = fr.getStringWidth(text);
                drawRect(x - 2, y - 1, x + lineWidth + 4, y + 11, 0x40FFFF00);
            }

            fr.drawStringWithShadow(text, x + 4, y + 4, 14737632);
        }

        GL11.glPopMatrix();
    }

    private static void drawPotions(int x, int y, float scaleHud, boolean editorMode) {
        if (mc.thePlayer == null) return;

        Collection<PotionEffect> activePotions = mc.thePlayer.getActivePotionEffects();
        if (activePotions.isEmpty()) return;

        FontRenderer fr = mc.fontRenderer;

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
    }

    private static void drawCountItem(String text, int x, int y) {
        ItemStack heldItem = mc.thePlayer != null ? mc.thePlayer.getHeldItem() : null;
        if (heldItem == null) return;

        FontRenderer fr = mc.fontRenderer;
        RenderItem renderItem = RenderItem.getInstance();

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

    private static void drawPotionIcon(Potion potion, int x, int y, int size) {
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

    private static String getLevel(int level) {
        if (level <= 1) return "";
        return String.valueOf(level);
    }

    public static void drawRect(int left, int top, int right, int bottom, int color) {
        if (left < right) {
            int temp = left;
            left = right;
            right = temp;
        }
        if (top < bottom) {
            int temp = top;
            top = bottom;
            bottom = temp;
        }

        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, a);

        tessellator.startDrawingQuads();
        tessellator.addVertex(left, bottom, 0.0D);
        tessellator.addVertex(right, bottom, 0.0D);
        tessellator.addVertex(right, top, 0.0D);
        tessellator.addVertex(left, top, 0.0D);
        tessellator.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static boolean isMouseOverLine(int mouseX, int mouseY, InfoLine line, float scaleHud) {
        if (line == null || line.position == null) return false;

        FontRenderer fr = mc.fontRenderer;
        int x = line.position.getX();
        int y = line.position.getY();
        String text = line.getLineString();
        int textWidth = fr.getStringWidth(text) + (line.getChachedItemStack() != null ? 16 : 4);
        int textHeight = (int) (11 * scaleHud);
        int scaledWidth = (int) (textWidth * scaleHud);

        return mouseX >= x - 2 && mouseX <= x + scaledWidth && mouseY >= y - 1 && mouseY <= y + textHeight;
    }

    public static boolean isMouseOverEffects(int mouseX, int mouseY, int effectX, int effectY) {
        return mouseX >= effectX - 10 && mouseX <= effectX + 250 && mouseY >= effectY - 5 && mouseY <= effectY + 30;
    }

    public static boolean isMouseOverCountItem(int mouseX, int mouseY, int countItemX, int countItemY) {
        return mouseX >= countItemX - 2 && mouseX <= countItemX + 20
            && mouseY >= countItemY - 1
            && mouseY <= countItemY + 30;
    }
}
