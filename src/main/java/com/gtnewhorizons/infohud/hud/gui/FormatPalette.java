package com.gtnewhorizons.infohud.hud.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import com.gtnewhorizons.infohud.hud.core.HudRenderer;
import com.gtnewhorizons.infohud.hud.tags.LineTemplate;

class FormatPalette {

    static final int CELL = 12;
    private static final int[] COLORS = { 0x000000, 0x0000AA, 0x00AA00, 0x00AAAA, 0xAA0000, 0xAA00AA, 0xFFAA00,
        0xAAAAAA, 0x555555, 0x5555FF, 0x55FF55, 0x55FFFF, 0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF };

    private final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    int x;
    int y;
    int width;

    void setBounds(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    private int perRow() {
        return Math.max(1, width / CELL);
    }

    int getHeight() {
        int count = LineTemplate.FORMAT_CODES.length();
        return ((count + perRow() - 1) / perRow()) * CELL;
    }

    int getIndexAt(int mouseX, int mouseY) {
        if (mouseX < x || mouseY < y) return -1;
        int column = (mouseX - x) / CELL;
        int row = (mouseY - y) / CELL;
        if (column >= perRow()) return -1;
        int index = row * perRow() + column;
        return index < LineTemplate.FORMAT_CODES.length() ? index : -1;
    }

    static char getCode(int index) {
        return LineTemplate.FORMAT_CODES.charAt(index);
    }

    void draw(int mouseX, int mouseY) {
        int hovered = getIndexAt(mouseX, mouseY);
        String codes = LineTemplate.FORMAT_CODES;

        for (int i = 0; i < codes.length(); i++) {
            int cx = x + (i % perRow()) * CELL;
            int cy = y + (i / perRow()) * CELL;
            char code = codes.charAt(i);

            HudRenderer.drawFrame(cx, cy, cx + CELL - 1, cy + CELL - 1, i == hovered ? 0xFFFFFFFF : 0xFF707070);

            if (i < COLORS.length) {
                HudRenderer.drawRect(cx + 1, cy + 1, cx + CELL - 2, cy + CELL - 2, 0xFF000000 | COLORS[i]);
            } else {
                HudRenderer.drawRect(cx + 1, cy + 1, cx + CELL - 2, cy + CELL - 2, 0xFF303030);
                String letter = code == 'r' ? "R" : "§" + code + "A";
                fr.drawStringWithShadow(letter, cx + 3, cy + 2, 0xFFFFFF);
            }
        }
    }
}
