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

import com.gtnewhorizons.infohud.hud.layout.HudLayout;
import com.gtnewhorizons.infohud.hud.layout.HudLayoutStorage;
import com.gtnewhorizons.infohud.hud.layout.HudLine;
import com.gtnewhorizons.infohud.hud.tags.VanillaTags;

public class HudRenderer {

    public static final int LINE_HEIGHT = 11;
    public static final int TEXT_COLOR = 0xFFE0E0E0;
    public static final int POTION_ICON_SIZE = 18;
    public static final int COUNT_ITEM_WIDTH = 18;
    public static final int COUNT_ITEM_HEIGHT = 26;

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ResourceLocation INVENTORY_TEXTURE = new ResourceLocation(
        "textures/gui/container/inventory.png");

    public static void renderHud(int screenWidth, int screenHeight) {
        if (mc.currentScreen != null) return;
        if (mc.gameSettings.showDebugInfo) return;
        if (mc.thePlayer == null) return;

        HudLayout layout = HudLayoutStorage.get();
        if (layout.hudDisabled) return;

        float scale = layout.scale;

        if (layout.potionsEnabled) {
            drawPotions(layout, layout.potionX, layout.potionY);
        }

        int index = 0;
        for (HudLine line : layout.lines) {
            if (!line.enabled || !line.inGroup) continue;

            String text = line.render();
            if (text == null) continue;

            drawLine(text, layout.groupX, getGroupLineY(layout, index), line.getIconStack(), scale, TEXT_COLOR, 0);
            index++;
        }

        for (HudLine line : layout.lines) {
            if (!line.enabled || line.inGroup) continue;

            String text = line.render();
            if (text == null) continue;

            drawLine(text, line.x, line.y, line.getIconStack(), scale, TEXT_COLOR, 0);
        }

        if (layout.countItemEnabled) {
            String count = getCountItemText();
            if (count != null) {
                drawCountItem(
                    count,
                    getCountItemX(layout, screenWidth),
                    getCountItemY(layout, screenHeight),
                    mc.thePlayer.getHeldItem());
            }
        }
    }

    // ---------------------------------------------------------------------------------------------------------------
    // Geometry helpers, shared with the editor
    // ---------------------------------------------------------------------------------------------------------------

    public static int getGroupLineY(HudLayout layout, int index) {
        return layout.groupY + (int) (index * LINE_HEIGHT * layout.scale);
    }

    /** Unscaled width of a line including the icon and padding. */
    public static int getLineWidth(String text, ItemStack icon) {
        return mc.fontRenderer.getStringWidth(text) + (icon != null ? 14 : 6);
    }

    /** Screen rectangle {left, top, right, bottom} of a line drawn at x/y. */
    public static int[] getLineRect(String text, ItemStack icon, int x, int y, float scale) {
        int width = getLineWidth(text, icon);
        return new int[] { x, y + (int) (2 * scale), x + (int) Math.ceil(width * scale),
            y + (int) Math.ceil(13 * scale) };
    }

    public static int getCountItemX(HudLayout layout, int screenWidth) {
        return layout.countItemX < 0 ? screenWidth / 2 - 91 - 22 : layout.countItemX;
    }

    public static int getCountItemY(HudLayout layout, int screenHeight) {
        return layout.countItemY < 0 ? screenHeight - 24 : layout.countItemY;
    }

    /**
     * @return the amount of the held item in the inventory, or {@code null} if it is spread over less than two slots
     */
    public static String getCountItemText() {
        if (mc.thePlayer == null || mc.thePlayer.getHeldItem() == null) return null;
        if (VanillaTags.countHeldItem(true) <= 1) return null;
        return String.valueOf(VanillaTags.countHeldItem(false));
    }

    public static List<PotionEffect> getDisplayedPotions() {
        List<PotionEffect> result = new ArrayList<>();
        if (mc.thePlayer == null) return result;

        Collection<PotionEffect> active = mc.thePlayer.getActivePotionEffects();
        for (PotionEffect effect : active) {
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (potion != null && potion.hasStatusIcon()) {
                result.add(effect);
            }
        }
        result.sort(Comparator.comparingInt(PotionEffect::getPotionID));
        return result;
    }

    /** Unscaled width of one potion entry. */
    public static int getPotionEntryWidth() {
        return POTION_ICON_SIZE + 1 + mc.fontRenderer.getStringWidth("00") + 2;
    }

    // ---------------------------------------------------------------------------------------------------------------
    // Drawing
    // ---------------------------------------------------------------------------------------------------------------

    /**
     * Draws a line with its icon. The line is scaled around its top left corner.
     *
     * @param color      text color, alpha is respected
     * @param background background color, 0 for none
     */
    public static void drawLine(String text, int x, int y, ItemStack icon, float scale, int color, int background) {
        FontRenderer fr = mc.fontRenderer;

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(-x, -y, 0);

        if (background != 0) {
            drawRect(x, y + 2, x + getLineWidth(text, icon), y + 13, background);
        }

        int textX = x + 4;

        if (icon != null) {
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
            try {
                renderItem.renderItemAndEffectIntoGUI(fr, mc.renderEngine, icon, x + 2, y + 5);
            } catch (Exception ignored) {}
            renderItem.zLevel = 0.0F;

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();

            textX = x + 12;
        }

        boolean translucent = (color >>> 24) != 0xFF;
        if (translucent) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

        fr.drawStringWithShadow(text, textX, y + 4, color);

        if (translucent) {
            GL11.glDisable(GL11.GL_BLEND);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    /**
     * @return the unscaled width of the drawn potions
     */
    public static int drawPotions(HudLayout layout, int x, int y) {
        List<PotionEffect> effects = getDisplayedPotions();
        if (effects.isEmpty()) return 0;

        FontRenderer fr = mc.fontRenderer;
        float scale = layout.scale;

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(-x, -y, 0);

        int currentX = x;
        int entryWidth = getPotionEntryWidth();

        for (PotionEffect effect : effects) {
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (currentX + entryWidth > mc.displayWidth / scale) break;

            drawPotionIcon(potion, currentX, y, POTION_ICON_SIZE);

            int duration = effect.getDuration() / 20;
            String timeString;
            if (duration > 1800) {
                timeString = "∞";
            } else {
                timeString = String.format("%d:%02d", duration / 60, duration % 60);
            }

            String levelString = getLevel(effect.getAmplifier() + 1);
            int timeWidth = fr.getStringWidth(timeString);
            int timeX = currentX + (POTION_ICON_SIZE / 2) - (timeWidth / 2);

            if (layout.potionTime) {
                fr.drawStringWithShadow(timeString, timeX, y + POTION_ICON_SIZE + 1, 0xFFFFFF);
            }
            if (!levelString.isEmpty() && layout.potionLevel) {
                fr.drawStringWithShadow(
                    levelString,
                    currentX + POTION_ICON_SIZE + 3,
                    y + (POTION_ICON_SIZE / 2) - 4,
                    0xFFAA00);
            }

            currentX += entryWidth;
        }

        GL11.glPopMatrix();
        return currentX - x;
    }

    public static void drawCountItem(String text, int x, int y, ItemStack stack) {
        FontRenderer fr = mc.fontRenderer;

        GL11.glPushMatrix();

        if (stack != null) {
            RenderItem renderItem = RenderItem.getInstance();
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);

            renderItem.zLevel = 100.0F;
            renderItem.renderItemAndEffectIntoGUI(fr, mc.renderEngine, stack, x, y);
            renderItem.zLevel = 0.0F;

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glDisable(GL11.GL_LIGHTING);
        }

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
        mc.renderEngine.bindTexture(INVENTORY_TEXTURE);

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
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /** Draws a 1px frame. */
    public static void drawFrame(int left, int top, int right, int bottom, int color) {
        drawRect(left, top, right, top + 1, color);
        drawRect(left, bottom - 1, right, bottom, color);
        drawRect(left, top + 1, left + 1, bottom - 1, color);
        drawRect(right - 1, top + 1, right, bottom - 1, color);
    }
}
