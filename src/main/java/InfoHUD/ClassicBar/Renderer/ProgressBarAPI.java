package InfoHUD.ClassicBar.Renderer;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class ProgressBarAPI {

    public enum Side {
        LEFT,
        RIGHT,
        NONE
    }

    public enum NumberFormat {
        FRACTION,
        PERCENTAGE,
        CURRENT,
        MAX,
        NONE
    }

    public enum AnimationStyle {
        NONE,
        SMOOTH,
        BOUNCE,
        ELASTIC
    }

    private float progress;
    private float displayProgress;
    private float maxProgress;
    private float currentProgress;
    private int x, y;
    private int width, height;
    private int backgroundColor;
    private int fillColor;
    private int borderColor;
    private Side textSide;
    private Side iconSide;
    private NumberFormat numberFormat;
    private ResourceLocation icon;
    private int iconWidth, iconHeight;
    private float iconU, iconV, iconU2, iconV2;
    private int textColor;
    private boolean showBackground;
    private boolean showBorder;
    private int borderWidth;
    private AnimationStyle animationStyle;
    private float animationSpeed;
    private long lastUpdateTime;
    private boolean showGlow;
    private int glowColor;
    private float glowAlpha;
    private float damageFlash;
    private boolean showGradient;
    private boolean showShine;
    private float shinePosition;
    private boolean rainbow;
    private float rainbowHue;

    public ProgressBarAPI(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.progress = 0.0f;
        this.displayProgress = 0.0f;
        this.maxProgress = 100;
        this.currentProgress = 0;
        this.backgroundColor = 0xFF444444;
        this.fillColor = 0xFF00FF00;
        this.borderColor = 0xFF000000;
        this.textSide = Side.RIGHT;
        this.iconSide = Side.NONE;
        this.numberFormat = NumberFormat.FRACTION;
        this.textColor = 0xFFFFFFFF;
        this.showBackground = true;
        this.showBorder = true;
        this.borderWidth = 1;
        this.animationStyle = AnimationStyle.SMOOTH;
        this.animationSpeed = 0.15f;
        this.lastUpdateTime = System.currentTimeMillis();
        this.showGlow = true;
        this.glowColor = 0xFFFFFF00;
        this.glowAlpha = 0.0f;
        this.damageFlash = 0.0f;
        this.showGradient = true;
        this.showShine = true;
        this.shinePosition = -0.5f;
        this.rainbow = false;
        this.rainbowHue = 0.0f;
    }

    public ProgressBarAPI setProgress(float current, float max) {
        float newProgress = max > 0 ? current / max : 0;
        if (newProgress < this.progress && this.progress > 0) {
            this.damageFlash = 1.0f;
        }
        this.currentProgress = current;
        this.maxProgress = max;
        this.progress = Math.max(0, Math.min(1, newProgress));
        return this;
    }

    public ProgressBarAPI setProgress(float progress) {
        return setProgress(progress * maxProgress, maxProgress);
    }

    public ProgressBarAPI setBackgroundColor(int color) {
        this.backgroundColor = color;
        return this;
    }

    public ProgressBarAPI setFillColor(int color) {
        this.fillColor = color;
        return this;
    }

    public ProgressBarAPI setBorderColor(int color) {
        this.borderColor = color;
        return this;
    }

    public ProgressBarAPI setTextColor(int color) {
        this.textColor = color;
        return this;
    }

    public ProgressBarAPI setTextSide(Side side) {
        this.textSide = side;
        return this;
    }

    public ProgressBarAPI setNumberFormat(NumberFormat format) {
        this.numberFormat = format;
        return this;
    }

    public ProgressBarAPI setIcon(ResourceLocation icon, int width, int height, float u, float v, float u2, float v2) {
        this.icon = icon;
        this.iconWidth = width;
        this.iconHeight = height;
        this.iconU = u;
        this.iconV = v;
        this.iconU2 = u2;
        this.iconV2 = v2;
        return this;
    }

    public ProgressBarAPI setIconSide(Side side) {
        this.iconSide = side;
        return this;
    }

    public ProgressBarAPI setShowBackground(boolean show) {
        this.showBackground = show;
        return this;
    }

    public ProgressBarAPI setShowBorder(boolean show) {
        this.showBorder = show;
        return this;
    }

    public ProgressBarAPI setBorderWidth(int width) {
        this.borderWidth = width;
        return this;
    }

    public ProgressBarAPI setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public ProgressBarAPI setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public ProgressBarAPI setAnimationStyle(AnimationStyle style) {
        this.animationStyle = style;
        return this;
    }

    public ProgressBarAPI setAnimationSpeed(float speed) {
        this.animationSpeed = speed;
        return this;
    }

    public ProgressBarAPI setShowGlow(boolean show) {
        this.showGlow = show;
        return this;
    }

    public ProgressBarAPI setGlowColor(int color) {
        this.glowColor = color;
        return this;
    }

    public ProgressBarAPI setShowGradient(boolean show) {
        this.showGradient = show;
        return this;
    }

    public ProgressBarAPI setShowShine(boolean show) {
        this.showShine = show;
        return this;
    }

    public ProgressBarAPI setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
        return this;
    }

    public void render() {
        updateAnimation();
        updateEffects();

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int barX = x;
        int barY = y;

        int iconOffset = 0;
        if (icon != null && iconSide != Side.NONE) {
            iconOffset = iconWidth + 4;
        }

        int textOffset = 0;
        if (numberFormat != NumberFormat.NONE) {
            String text = getFormattedText();
            int textWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
            textOffset = textWidth + 4;
        }

        if (showGlow && displayProgress >= 1.0f) {
            drawGlowEffect(barX, barY);
        }

        if (icon != null && iconSide == Side.LEFT) {
            drawIcon(barX, barY + (height - iconHeight) / 2);
            barX += iconOffset;
        }

        if (textSide == Side.LEFT && numberFormat != NumberFormat.NONE) {
            drawText(x + (iconSide == Side.LEFT ? iconOffset : 0), barY);
            barX += textOffset;
        }

        drawProgressBar(barX, barY);

        if (textSide == Side.RIGHT && numberFormat != NumberFormat.NONE) {
            drawText(barX + width + 4, barY);
        }

        if (icon != null && iconSide == Side.RIGHT) {
            drawIcon(barX + width + (textSide == Side.RIGHT ? textOffset : 0) + 4, barY + (height - iconHeight) / 2);
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private void updateAnimation() {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currentTime;

        if (deltaTime > 0.1f) deltaTime = 0.1f;

        float speed = animationSpeed * deltaTime * 60;

        switch (animationStyle) {
            case NONE:
                displayProgress = progress;
                break;

            case SMOOTH:
                displayProgress += (progress - displayProgress) * speed;
                if (Math.abs(displayProgress - progress) < 0.001f) {
                    displayProgress = progress;
                }
                break;

            case BOUNCE:
                float diff = progress - displayProgress;
                if (Math.abs(diff) < 0.001f) {
                    displayProgress = progress;
                } else if (diff > 0) {
                    displayProgress += diff * speed * 1.5f;
                } else {
                    float bounceFactor = 1.0f + Math.abs(diff) * 0.5f;
                    displayProgress += diff * speed * bounceFactor;
                    if (displayProgress < progress) {
                        displayProgress = progress + (progress - displayProgress) * 0.3f;
                    }
                }
                break;

            case ELASTIC:
                float elasticDiff = progress - displayProgress;
                if (Math.abs(elasticDiff) < 0.001f) {
                    displayProgress = progress;
                } else {
                    float elasticSpeed = speed * 2.0f;
                    if (elasticDiff > 0) {
                        displayProgress += elasticDiff * elasticSpeed;
                        if (displayProgress > progress) {
                            displayProgress = progress + (displayProgress - progress) * 0.5f;
                            if (displayProgress > progress * 1.2f) {
                                displayProgress = progress * 1.2f;
                            }
                        }
                    } else {
                        displayProgress += elasticDiff * elasticSpeed * 1.5f;
                        if (displayProgress < progress) {
                            displayProgress = progress - (progress - displayProgress) * 0.3f;
                        }
                    }
                }
                break;
        }

        if (showShine) {
            shinePosition += deltaTime * 0.5f;
            if (shinePosition > 1.5f) {
                shinePosition = -0.5f;
            }
        }

        if (rainbow) {
            rainbowHue += deltaTime * 0.5f;
            if (rainbowHue > 1.0f) {
                rainbowHue -= 1.0f;
            }
        }
    }

    private void updateEffects() {
        if (damageFlash > 0) {
            damageFlash -= 0.05f;
            if (damageFlash < 0) damageFlash = 0;
        }

        if (displayProgress >= 1.0f && showGlow) {
            glowAlpha = (float) (Math.sin(System.currentTimeMillis() * 0.005) * 0.3 + 0.7);
        } else {
            glowAlpha *= 0.9f;
        }
    }

    private void drawProgressBar(int drawX, int drawY) {
        if (damageFlash > 0.1f) {
            drawRect(
                drawX - 1,
                drawY - 1,
                drawX + width + 1,
                drawY + height + 1,
                0x44FFFFFF | ((int) (damageFlash * 100) << 24));
        }

        if (showBackground) {
            drawTexturedBackground(drawX, drawY);
        }

        int fillWidth = (int) (width * displayProgress);
        if (fillWidth > 0) {
            int currentFillColor = fillColor;

            if (rainbow) {
                currentFillColor = getRainbowColor(rainbowHue);
            }

            if (showGradient) {
                drawGradientRect(
                    drawX,
                    drawY,
                    drawX + fillWidth,
                    drawY + height,
                    currentFillColor,
                    darken(currentFillColor, 0.6f));

                if (fillWidth > 2) {
                    drawGradientRect(
                        drawX + 1,
                        drawY + 1,
                        drawX + fillWidth - 1,
                        drawY + height / 3,
                        lighten(currentFillColor, 0.3f),
                        lighten(currentFillColor, 0.1f));
                }

                if (fillWidth > 2 && height > 4) {
                    drawRect(drawX + 1, drawY + height - 2, drawX + fillWidth - 1, drawY + height - 1, 0x22000000);
                }
            } else {
                drawRect(drawX, drawY, drawX + fillWidth, drawY + height, currentFillColor);
            }

            if (showShine && fillWidth > 4) {
                int shineX = drawX + (int) (fillWidth * shinePosition);
                int shineWidth = Math.min(8, fillWidth / 3);
                if (shineX >= drawX && shineX + shineWidth <= drawX + fillWidth) {
                    drawGradientRect(
                        shineX,
                        drawY + 1,
                        shineX + shineWidth,
                        drawY + height - 1,
                        0x44FFFFFF,
                        0x00FFFFFF);
                }
            }

            if (fillWidth > 4) {
                for (int i = drawX + 4; i < drawX + fillWidth - 2; i += 4) {
                    drawRect(i, drawY + 2, i + 1, drawY + height - 2, 0x11000000);
                }
            }
        }

        if (showBorder) {
            drawEnhancedBorder(drawX, drawY);
        }
    }

    private void drawTexturedBackground(int drawX, int drawY) {
        drawRect(drawX, drawY, drawX + width, drawY + height, backgroundColor);

        for (int i = 0; i < width; i += 3) {
            int alpha = (i % 6 == 0) ? 0x15 : 0x08;
            drawRect(drawX + i, drawY + 1, drawX + i + 2, drawY + height - 1, (alpha << 24) | 0x000000);
        }
    }

    private void drawEnhancedBorder(int drawX, int drawY) {
        drawRect(drawX - 1, drawY - 1, drawX + width + 1, drawY + height + 1, 0x44000000);

        for (int i = 0; i < borderWidth; i++) {
            int alpha = 255 - i * 40;
            int borderColorWithAlpha = (alpha << 24) | (borderColor & 0x00FFFFFF);
            drawRectOutline(drawX - i, drawY - i, drawX + width + i, drawY + height + i, borderColorWithAlpha);
        }

        drawRect(drawX, drawY, drawX + width, drawY + 1, 0x33FFFFFF);
        drawRect(drawX, drawY, drawX + 1, drawY + height, 0x33FFFFFF);
        drawRect(drawX + width - 1, drawY, drawX + width, drawY + height, 0x33000000);
        drawRect(drawX, drawY + height - 1, drawX + width, drawY + height, 0x33000000);
    }

    private void drawGlowEffect(int drawX, int drawY) {
        for (int i = 1; i <= 3; i++) {
            int alpha = (int) (glowAlpha * (40 - i * 10));
            int glowColorWithAlpha = (alpha << 24) | (glowColor & 0x00FFFFFF);
            drawRectOutline(drawX - i, drawY - i, drawX + width + i, drawY + height + i, glowColorWithAlpha);
        }
    }

    private void drawText(int drawX, int drawY) {
        String text = getFormattedText();
        Minecraft mc = Minecraft.getMinecraft();
        int textHeight = mc.fontRenderer.FONT_HEIGHT;
        int centeredY = drawY + (height - textHeight) / 2;
        mc.fontRenderer.drawStringWithShadow(text, drawX, centeredY, textColor);
    }

    private void drawIcon(int drawX, int drawY) {
        Minecraft.getMinecraft().renderEngine.bindTexture(icon);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Gui.func_146110_a(drawX, drawY, iconU, iconV, iconWidth, iconHeight, iconWidth, iconHeight);
    }

    private String getFormattedText() {
        switch (numberFormat) {
            case FRACTION:
                return (int) currentProgress + "/" + (int) maxProgress;
            case PERCENTAGE:
                return (int) (progress * 100) + "%";
            case CURRENT:
                return String.valueOf((int) currentProgress);
            case MAX:
                return String.valueOf((int) maxProgress);
            default:
                return "";
        }
    }

    private void drawRect(int x1, int y1, int x2, int y2, int color) {
        Gui.drawRect(x1, y1, x2, y2, color);
    }

    private void drawGradientRect(int x1, int y1, int x2, int y2, int color1, int color2) {
        float a1 = (float) (color1 >> 24 & 255) / 255.0F;
        float r1 = (float) (color1 >> 16 & 255) / 255.0F;
        float g1 = (float) (color1 >> 8 & 255) / 255.0F;
        float b1 = (float) (color1 & 255) / 255.0F;

        float a2 = (float) (color2 >> 24 & 255) / 255.0F;
        float r2 = (float) (color2 >> 16 & 255) / 255.0F;
        float g2 = (float) (color2 >> 8 & 255) / 255.0F;
        float b2 = (float) (color2 & 255) / 255.0F;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(r1, g1, b1, a1);
        tessellator.addVertex(x1, y2, 0);
        tessellator.addVertex(x2, y2, 0);
        tessellator.setColorRGBA_F(r2, g2, b2, a2);
        tessellator.addVertex(x2, y1, 0);
        tessellator.addVertex(x1, y1, 0);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void drawRectOutline(int x1, int y1, int x2, int y2, int color) {
        drawRect(x1, y1, x2, y1 + 1, color);
        drawRect(x1, y2 - 1, x2, y2, color);
        drawRect(x1, y1, x1 + 1, y2, color);
        drawRect(x2 - 1, y1, x2, y2, color);
    }

    private int darken(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * factor);
        int g = (int) (((color >> 8) & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int lighten(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) + (255 - ((color >> 16) & 0xFF)) * factor));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) + (255 - ((color >> 8) & 0xFF)) * factor));
        int b = Math.min(255, (int) ((color & 0xFF) + (255 - (color & 0xFF)) * factor));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int getRainbowColor(float hue) {
        return Color.HSBtoRGB(hue, 0.8f, 1.0f);
    }
}
