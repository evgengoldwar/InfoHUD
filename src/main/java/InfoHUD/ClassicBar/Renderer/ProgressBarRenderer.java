package InfoHUD.ClassicBar.Renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

public class ProgressBarRenderer {

    public static void render(ProgressBarBuilder builder) {
        builder.updateAnimation();
        builder.updateEffects();

        if (builder.alpha <= 0.01f) return;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int barX = builder.x;
        int barY = builder.y;

        String currentText;
        int currentTextWidth = 0;
        if (builder.numberFormat != ProgressBarBuilder.NumberFormat.NONE) {
            currentText = getFormattedText(builder);
            currentTextWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(currentText);
        }

        int leftTextX = barX;
        int leftIconX = barX;

        if (builder.textSide == ProgressBarBuilder.Side.LEFT) {
            leftTextX = barX - currentTextWidth - 3;
            leftIconX = leftTextX;
        }

        if (builder.icon != null && builder.iconSide == ProgressBarBuilder.Side.LEFT) {
            leftIconX = leftTextX - builder.iconWidth - 3;
        }

        int rightTextX = barX + builder.width + 3;
        int rightIconX = rightTextX;

        if (builder.textSide == ProgressBarBuilder.Side.RIGHT) {
            rightTextX = barX + builder.width + 3;
            rightIconX = rightTextX + currentTextWidth + 3;
        }

        if (builder.icon != null && builder.iconSide == ProgressBarBuilder.Side.RIGHT) {
            rightIconX = rightTextX + currentTextWidth + 3;
        }

        if (builder.icon != null && builder.iconSide == ProgressBarBuilder.Side.LEFT) {
            drawIcon(builder, leftIconX, barY + (builder.height - builder.iconHeight) / 2, builder.alpha);
        }

        if (builder.textSide == ProgressBarBuilder.Side.LEFT
            && builder.numberFormat != ProgressBarBuilder.NumberFormat.NONE) {
            drawText(builder, leftTextX, barY, builder.alpha);
        }

        drawProgressBar(builder, barX, barY);

        if (builder.textSide == ProgressBarBuilder.Side.RIGHT
            && builder.numberFormat != ProgressBarBuilder.NumberFormat.NONE) {
            drawText(builder, rightTextX, barY, builder.alpha);
        }

        if (builder.icon != null && builder.iconSide == ProgressBarBuilder.Side.RIGHT) {
            drawIcon(builder, rightIconX, barY + (builder.height - builder.iconHeight) / 2, builder.alpha);
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private static void drawProgressBar(ProgressBarBuilder bar, int drawX, int drawY) {
        float a = bar.alpha;

        if (bar.damageFlash > 0.1f) {
            drawRect(
                drawX - 1,
                drawY - 1,
                drawX + bar.width + 1,
                drawY + bar.height + 1,
                ((int) (a * 0x44) << 24) | 0xFFFFFF | ((int) (bar.damageFlash * 100) << 24));
        }

        if (bar.showBackground) {
            int bg = applyAlpha(bar.backgroundColor, a);
            drawRect(drawX, drawY, drawX + bar.width, drawY + bar.height, bg);
            for (int i = 0; i < bar.width; i += 3) {
                int alpha = (i % 6 == 0) ? 0x15 : 0x08;
                alpha = (int) (alpha * a);
                drawRect(drawX + i, drawY + 1, drawX + i + 2, drawY + bar.height - 1, (alpha << 24));
            }
        }

        int minX = (int) (bar.width * bar.minProgress);
        int fillWidth = (int) (bar.width * bar.displayProgress);

        if (fillWidth > minX) {
            int startX = drawX + minX;
            int w = fillWidth - minX;
            int fill = applyAlpha(bar.fillColor, a);
            if (bar.showGradient) {
                drawGradientRect(
                    startX,
                    drawY,
                    startX + w,
                    drawY + bar.height,
                    fill,
                    applyAlpha(darken(bar.fillColor, 0.6f), a));
                if (w > 2) {
                    drawGradientRect(
                        startX + 1,
                        drawY + 1,
                        startX + w - 1,
                        drawY + bar.height / 3,
                        applyAlpha(lighten(bar.fillColor, 0.3f), a),
                        applyAlpha(lighten(bar.fillColor, 0.1f), a));
                }
                if (w > 2 && bar.height > 4) {
                    drawRect(
                        startX + 1,
                        drawY + bar.height - 2,
                        startX + w - 1,
                        drawY + bar.height - 1,
                        ((int) (0x22 * a) << 24));
                }
            } else {
                drawRect(startX, drawY, startX + w, drawY + bar.height, fill);
            }
        }

        if (bar.showBorder) {
            int border = applyAlpha(bar.borderColor, a);
            drawRect(drawX - 1, drawY - 1, drawX + bar.width + 1, drawY + bar.height + 1, ((int) (0x44 * a) << 24));
            for (int i = 0; i < bar.borderWidth; i++) {
                drawRectOutline(drawX - i, drawY - i, drawX + bar.width + i, drawY + bar.height + i, border);
            }
            drawRect(drawX, drawY, drawX + bar.width, drawY + 1, ((int) (0x33 * a) << 24) | 0xFFFFFF);
            drawRect(drawX, drawY, drawX + 1, drawY + bar.height, ((int) (0x33 * a) << 24) | 0xFFFFFF);
            drawRect(drawX + bar.width - 1, drawY, drawX + bar.width, drawY + bar.height, ((int) (0x33 * a) << 24));
            drawRect(drawX, drawY + bar.height - 1, drawX + bar.width, drawY + bar.height, ((int) (0x33 * a) << 24));
        }
    }

    private static int applyAlpha(int color, float alpha) {
        int a = (int) (((color >> 24) & 0xFF) * alpha);
        return (a << 24) | (color & 0x00FFFFFF);
    }

    private static void drawText(ProgressBarBuilder bar, int drawX, int drawY, float alpha) {
        String text = getFormattedText(bar);
        Minecraft mc = Minecraft.getMinecraft();
        int centeredY = drawY + 1 + (bar.height - mc.fontRenderer.FONT_HEIGHT) / 2;
        int color = applyAlpha(bar.textColor, alpha);
        mc.fontRenderer.drawStringWithShadow(text, drawX, centeredY, color);
    }

    private static void drawIcon(ProgressBarBuilder bar, int drawX, int drawY, float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.renderEngine.bindTexture(bar.icon);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
        drawTexturedModalRect(drawX, drawY + 1, (int) bar.iconU, (int) bar.iconV, bar.iconWidth, bar.iconHeight);
    }

    private static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
        float f = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 0, u * f, (v + height) * f);
        tessellator.addVertexWithUV(x + width, y + height, 0, (u + width) * f, (v + height) * f);
        tessellator.addVertexWithUV(x + width, y, 0, (u + width) * f, v * f);
        tessellator.addVertexWithUV(x, y, 0, u * f, v * f);
        tessellator.draw();
    }

    private static String getFormattedText(ProgressBarBuilder bar) {
        return switch (bar.numberFormat) {
            case FRACTION -> (int) bar.currentProgress + "/" + (int) bar.maxProgress;
            case PERCENTAGE -> (int) (bar.progress * 100) + "%";
            case CURRENT -> String.valueOf((int) bar.currentProgress);
            case MAX -> String.valueOf((int) bar.maxProgress);
            default -> "";
        };
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
