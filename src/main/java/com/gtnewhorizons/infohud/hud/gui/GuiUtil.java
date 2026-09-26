package com.gtnewhorizons.infohud.hud.gui;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizons.infohud.hud.core.HudRenderer;

final class GuiUtil {

    /** Names of the formatting codes, in the order of LineTemplate.FORMAT_CODES. */
    static final String[] FORMAT_NAMES = { "black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple",
        "gold", "gray", "dark_gray", "blue", "green", "aqua", "red", "light_purple", "yellow", "white", "obfuscated",
        "bold", "strikethrough", "underline", "italic", "reset" };

    private GuiUtil() {}

    static String t(String key, Object... args) {
        return StatCollector.translateToLocalFormatted("infohud.gui." + key, args);
    }

    static String onOff(String key, boolean value) {
        return t(key) + ": " + (value ? "§a" + t("on") : "§c" + t("off"));
    }

    static String formatName(int index) {
        return t("format." + FORMAT_NAMES[index]);
    }

    static void drawTooltip(FontRenderer fr, List<String> lines, int mouseX, int mouseY, int screenWidth,
        int screenHeight) {
        if (lines.isEmpty()) return;

        int width = 0;
        for (String line : lines) {
            width = Math.max(width, fr.getStringWidth(line));
        }
        int height = lines.size() * 10;

        int x = mouseX + 12;
        int y = mouseY - 12;
        if (x + width + 4 > screenWidth) x = Math.max(2, mouseX - width - 16);
        if (y + height + 4 > screenHeight) y = screenHeight - height - 4;
        if (y < 2) y = 2;

        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 400);
        HudRenderer.drawRect(x - 3, y - 3, x + width + 3, y + height + 1, 0xF0100010);
        HudRenderer.drawFrame(x - 3, y - 3, x + width + 3, y + height + 1, 0x805000FF);
        for (int i = 0; i < lines.size(); i++) {
            fr.drawStringWithShadow(lines.get(i), x, y + i * 10, 0xFFFFFFFF);
        }
        GL11.glPopMatrix();
    }

    static boolean inside(int mouseX, int mouseY, int left, int top, int right, int bottom) {
        return mouseX >= left && mouseX < right && mouseY >= top && mouseY < bottom;
    }
}
