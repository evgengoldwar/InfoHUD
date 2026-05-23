package InfoHUD.ClassicBar.Renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

public class ProgressBarRenderer {

    public static void render(ProgressBarBuilder builder) {
        builder.updateAnimation();
        builder.updateEffects();

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int barX = builder.x;
        int barY = builder.y;

        int iconOffset = (builder.icon != null && builder.iconSide != ProgressBarBuilder.Side.NONE)
            ? builder.iconWidth + 3
            : 0;
        int textOffset = (builder.numberFormat != ProgressBarBuilder.NumberFormat.NONE) ? getMaxTextWidth(builder) + 3
            : 0;

        if (builder.icon != null && builder.iconSide == ProgressBarBuilder.Side.LEFT) {
            drawIcon(builder, barX, barY + (builder.height - builder.iconHeight) / 2);
            barX += iconOffset;
        }

        if (builder.textSide == ProgressBarBuilder.Side.LEFT
            && builder.numberFormat != ProgressBarBuilder.NumberFormat.NONE) {
            drawText(builder, builder.x + (builder.iconSide == ProgressBarBuilder.Side.LEFT ? iconOffset : 0), barY);
            barX += textOffset;
        }

        drawProgressBar(builder, barX, barY);

        if (builder.textSide == ProgressBarBuilder.Side.RIGHT
            && builder.numberFormat != ProgressBarBuilder.NumberFormat.NONE) {
            drawText(builder, barX + builder.width + 3, barY);
        }

        if (builder.icon != null && builder.iconSide == ProgressBarBuilder.Side.RIGHT) {
            drawIcon(
                builder,
                barX + builder.width + (builder.textSide == ProgressBarBuilder.Side.RIGHT ? textOffset : 0) + 3,
                barY + (builder.height - builder.iconHeight) / 2);
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private static void drawProgressBar(ProgressBarBuilder b, int drawX, int drawY) {
        if (b.damageFlash > 0.1f) {
            drawRect(
                drawX - 1,
                drawY - 1,
                drawX + b.width + 1,
                drawY + b.height + 1,
                0x44FFFFFF | ((int) (b.damageFlash * 100) << 24));
        }

        if (b.showBackground) {
            drawRect(drawX, drawY, drawX + b.width, drawY + b.height, b.backgroundColor);
            for (int i = 0; i < b.width; i += 3) {
                int alpha = (i % 6 == 0) ? 0x15 : 0x08;
                drawRect(drawX + i, drawY + 1, drawX + i + 2, drawY + b.height - 1, (alpha << 24) | 0x000000);
            }
        }

        int minX = (int) (b.width * b.minProgress);
        int fillWidth = (int) (b.width * b.displayProgress);

        if (fillWidth > minX) {
            int startX = drawX + minX;
            int w = fillWidth - minX;
            if (b.showGradient) {
                drawGradientRect(startX, drawY, startX + w, drawY + b.height, b.fillColor, darken(b.fillColor, 0.6f));
                if (w > 2) {
                    drawGradientRect(
                        startX + 1,
                        drawY + 1,
                        startX + w - 1,
                        drawY + b.height / 3,
                        lighten(b.fillColor, 0.3f),
                        lighten(b.fillColor, 0.1f));
                }
                if (w > 2 && b.height > 4) {
                    drawRect(startX + 1, drawY + b.height - 2, startX + w - 1, drawY + b.height - 1, 0x22000000);
                }
            } else {
                drawRect(startX, drawY, startX + w, drawY + b.height, b.fillColor);
            }
        }

        if (b.showBorder) {
            drawRect(drawX - 1, drawY - 1, drawX + b.width + 1, drawY + b.height + 1, 0x44000000);
            for (int i = 0; i < b.borderWidth; i++) {
                drawRectOutline(drawX - i, drawY - i, drawX + b.width + i, drawY + b.height + i, b.borderColor);
            }
            drawRect(drawX, drawY, drawX + b.width, drawY + 1, 0x33FFFFFF);
            drawRect(drawX, drawY, drawX + 1, drawY + b.height, 0x33FFFFFF);
            drawRect(drawX + b.width - 1, drawY, drawX + b.width, drawY + b.height, 0x33000000);
            drawRect(drawX, drawY + b.height - 1, drawX + b.width, drawY + b.height, 0x33000000);
        }
    }

    private static void drawText(ProgressBarBuilder b, int drawX, int drawY) {
        String text = getFormattedText(b);
        Minecraft mc = Minecraft.getMinecraft();
        int centeredY = drawY + (b.height - mc.fontRenderer.FONT_HEIGHT) / 2;
        mc.fontRenderer.drawStringWithShadow(text, drawX, centeredY, b.textColor);
    }

    private static void drawIcon(ProgressBarBuilder b, int drawX, int drawY) {
        Minecraft.getMinecraft().renderEngine.bindTexture(b.icon);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Gui.func_146110_a(drawX, drawY, b.iconU, b.iconV, b.iconWidth, b.iconHeight, b.iconWidth, b.iconHeight);
    }

    private static String getFormattedText(ProgressBarBuilder b) {
        switch (b.numberFormat) {
            case FRACTION:
                return (int) b.currentProgress + "/" + (int) b.maxProgress;
            case PERCENTAGE:
                return (int) (b.progress * 100) + "%";
            case CURRENT:
                return String.valueOf((int) b.currentProgress);
            case MAX:
                return String.valueOf((int) b.maxProgress);
            default:
                return "";
        }
    }

    private static int getMaxTextWidth(ProgressBarBuilder b) {
        Minecraft mc = Minecraft.getMinecraft();
        String maxText = "";
        switch (b.numberFormat) {
            case FRACTION:
                maxText = (int) b.maxProgress + "/" + (int) b.maxProgress;
                break;
            case PERCENTAGE:
                maxText = "100%";
                break;
            case CURRENT:
            case MAX:
                maxText = String.valueOf((int) b.maxProgress);
                break;
            default:
                return 0;
        }
        return mc.fontRenderer.getStringWidth(maxText);
    }

    private static void drawRect(int x1, int y1, int x2, int y2, int color) {
        Gui.drawRect(x1, y1, x2, y2, color);
    }

    private static void drawGradientRect(int x1, int y1, int x2, int y2, int color1, int color2) {
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

    private static void drawRectOutline(int x1, int y1, int x2, int y2, int color) {
        drawRect(x1, y1, x2, y1 + 1, color);
        drawRect(x1, y2 - 1, x2, y2, color);
        drawRect(x1, y1, x1 + 1, y2, color);
        drawRect(x2 - 1, y1, x2, y2, color);
    }

    private static int darken(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * factor);
        int g = (int) (((color >> 8) & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int lighten(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) + (255 - ((color >> 16) & 0xFF)) * factor));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) + (255 - ((color >> 8) & 0xFF)) * factor));
        int b = Math.min(255, (int) ((color & 0xFF) + (255 - (color & 0xFF)) * factor));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
