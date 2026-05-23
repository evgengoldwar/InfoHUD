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
        drawBar(
            x,
            y,
            width,
            height,
            current,
            max,
            showFraction,
            barColor,
            backgroundColor,
            texture,
            textureSize,
            iconPosition,
            textPosition,
            0xFFFFFFFF);
    }

    public static void drawBar(int x, int y, int width, int height, float current, float max, boolean showFraction,
        int barColor, int backgroundColor, ResourceLocation texture, int textureSize, IconPosition iconPosition,
        TextPosition textPosition, int textColor) {

        FontRenderer fr = mc.fontRenderer;

        float progress = current / max;
        if (progress < 0F) progress = 0F;
        if (progress > 1F) progress = 1F;

        int innerWidth = width - 2;
        int fillWidth = Math.round(innerWidth * progress);

        int outerBg = 0xFF0A0A0A;

        drawRectGL(x, y, width, height, outerBg);
        drawRectGL(x + 1, y + 1, innerWidth, height - 2, backgroundColor);

        if (fillWidth > 0) {
            int mainR = (barColor >> 16) & 0xFF;
            int mainG = (barColor >> 8) & 0xFF;
            int mainB = barColor & 0xFF;

            int darkR = (int) (mainR * 0.6F);
            int darkG = (int) (mainG * 0.6F);
            int darkB = (int) (mainB * 0.6F);
            int darkColor = (0xFF << 24) | (darkR << 16) | (darkG << 8) | darkB;
            drawRectGL(x, y + 1, fillWidth + 2, height - 1, darkColor);

            int brightR = Math.min(255, (int) (mainR * 1.4F));
            int brightG = Math.min(255, (int) (mainG * 1.4F));
            int brightB = Math.min(255, (int) (mainB * 1.4F));
            int brightColor = (0xFF << 24) | (brightR << 16) | (brightG << 8) | brightB;
            int halfHeight = Math.max(1, (height - 2) / 2);
            drawRectGL(x, y + 1, fillWidth + 2, halfHeight, brightColor);

            int midR = (mainR + brightR) / 2;
            int midG = (mainG + brightG) / 2;
            int midB = (mainB + brightB) / 2;
            int midColor = (0xFF << 24) | (midR << 16) | (midG << 8) | midB;
            drawRectGL(x, y + 1 + halfHeight, fillWidth + 2, Math.min(2, height - 2 - halfHeight), midColor);
        }

        drawBorder(x, y, width, height, 0xFF555544);

        if (texture != null && iconPosition != IconPosition.HIDDEN) {
            int iconX = x;
            int iconOffset = textureSize + 2;

            if (iconPosition == IconPosition.LEFT) {
                iconX = x - iconOffset;
            }
            if (iconPosition == IconPosition.RIGHT) {
                iconX = x + width + 2;
            }

            drawTexture(texture, iconX, y + (height - textureSize) / 2, textureSize, textureSize);
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
            int iconOffset = (texture != null && iconPosition != IconPosition.HIDDEN) ? textureSize + 4 : 0;

            if (textPosition == TextPosition.LEFT) {
                textX = x - textWidth - 6 - iconOffset;
            }
            if (textPosition == TextPosition.RIGHT) {
                textX = x + width + 6 + iconOffset;
            }

            int textY = y + (height / 2) - (fr.FONT_HEIGHT / 2);
            fr.drawStringWithShadow(text, textX, textY, textColor);
        }
    }

    public static void drawRectGL(int x, int y, int width, int height, int color) {
        if (width <= 0 || height <= 0) return;

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

    public static void drawOverlay(int x, int y, int width, int height, float current, float future, float max,
        int color) {
        float progressCurrent = current / max;
        float progressFuture = future / max;

        if (progressCurrent < 0F) progressCurrent = 0F;
        if (progressFuture > 1F) progressFuture = 1F;
        if (progressFuture <= progressCurrent) return;

        int innerWidth = width - 2;
        int startX = x + 1 + Math.round(innerWidth * progressCurrent);
        int endX = x + 1 + Math.round(innerWidth * progressFuture);
        int overlayWidth = endX - startX;
        if (overlayWidth <= 0) return;

        int mainR = (color >> 16) & 0xFF;
        int mainG = (color >> 8) & 0xFF;
        int mainB = color & 0xFF;

        int darkR = (int) (mainR * 0.5F);
        int darkG = (int) (mainG * 0.5F);
        int darkB = (int) (mainB * 0.5F);
        int darkColor = (0xFF << 24) | (darkR << 16) | (darkG << 8) | darkB;

        drawRectGL(startX, y + 1, overlayWidth, height - 2, darkColor);

        int brightR = Math.min(255, (int) (mainR * 1.2F));
        int brightG = Math.min(255, (int) (mainG * 1.2F));
        int brightB = Math.min(255, (int) (mainB * 1.2F));
        int brightColor = (0xFF << 24) | (brightR << 16) | (brightG << 8) | brightB;

        drawRectGL(startX, y + 1, overlayWidth, (height - 2) / 2, brightColor);
    }
}
