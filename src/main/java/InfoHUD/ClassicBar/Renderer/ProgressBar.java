package InfoHUD.ClassicBar.Renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import InfoHUD.ClassicBar.Enum.IconPosition;
import InfoHUD.ClassicBar.Enum.TextPosition;

public class ProgressBar extends Gui {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void drawBar(int x, int y, int width, int height, float current, float max, boolean showFraction,
                               int barColor, int backgroundColor, ResourceLocation texture, int textureSize, IconPosition iconPosition,
                               TextPosition textPosition) {

        FontRenderer fr = mc.fontRenderer;

        float progress = current / max;

        if (progress < 0F) progress = 0F;

        if (progress > 1F) progress = 1F;

        int filled = (int) (width * progress);

        drawRectGL(x, y, width, height, backgroundColor);

        drawRectGL(x, y, filled, height, barColor);

        drawBorder(x, y, width, height, 0xFF000000);

        if (texture != null && iconPosition != IconPosition.HIDDEN) {

            int iconX = x;

            if (iconPosition == IconPosition.LEFT) {
                iconX = x - textureSize - 4;
            }

            if (iconPosition == IconPosition.RIGHT) {
                iconX = x + width + 4;
            }

            drawTexture(texture, iconX, y - (textureSize - height) / 2, textureSize, textureSize);
        }

        if (textPosition != TextPosition.HIDDEN) {

            String text;

            if (showFraction) {
                text = ((int) current) + "/" + ((int) max);
            } else {
                text = String.valueOf((int) current);
            }

            int textWidth = fr.getStringWidth(text);

            int textX = x;

            if (textPosition == TextPosition.LEFT) {
                textX = x - textWidth - 6;
            }

            if (textPosition == TextPosition.RIGHT) {
                textX = x + width + 6;
            }

            int textY = y + (height / 2 - 4);

            fr.drawStringWithShadow(text, textX, textY, 0xFFFFFFFF);
        }
    }

    public static void drawRectGL(int x, int y, int width, int height, int color) {

        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;

        GL11.glPushMatrix();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glColor4f(r, g, b, a);

        GL11.glBegin(GL11.GL_QUADS);

        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y + height);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x + width, y);

        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopMatrix();
    }

    public static void drawBorder(int x, int y, int width, int height, int color) {

        drawRectGL(x, y, width, 1, color);
        drawRectGL(x, y + height - 1, width, 1, color);

        drawRectGL(x, y, 1, height, color);
        drawRectGL(x + width - 1, y, 1, height, color);
    }

    public static void drawTexture(ResourceLocation texture, int x, int y, int width, int height) {

        Minecraft mc = Minecraft.getMinecraft();

        mc.getTextureManager()
            .bindTexture(texture);

        GL11.glPushMatrix();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glColor4f(1F, 1F, 1F, 1F);

        GL11.glBegin(GL11.GL_QUADS);

        GL11.glTexCoord2f(0F, 0F);
        GL11.glVertex2f(x, y);

        GL11.glTexCoord2f(0F, 1F);
        GL11.glVertex2f(x, y + height);

        GL11.glTexCoord2f(1F, 1F);
        GL11.glVertex2f(x + width, y + height);

        GL11.glTexCoord2f(1F, 0F);
        GL11.glVertex2f(x + width, y);

        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopMatrix();
    }

    public static void drawOverlay(int x, int y, int width, int height, float current, float future, float max, int color) {

        float progressCurrent = current / max;
        float progressFuture = future / max;

        if (progressCurrent < 0F) progressCurrent = 0F;
        if (progressFuture > 1F) progressFuture = 1F;
        if (progressFuture <= progressCurrent) return;

        int startX = x + (int) (width * progressCurrent);
        int overlayWidth = (int) (width * progressFuture) - (int) (width * progressCurrent);

        drawRectGL(startX + 1, y + 1, overlayWidth - 2, height - 2, color);
    }
}
