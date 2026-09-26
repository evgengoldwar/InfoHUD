package com.gtnewhorizons.infohud.hud.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import com.gtnewhorizons.infohud.hud.core.HudRenderer;
import com.gtnewhorizons.infohud.hud.tags.InfoTag;
import com.gtnewhorizons.infohud.hud.tags.TagRegistry;

/**
 * Scrollable list of all tags grouped by category, showing their current values.
 */
class TagListWidget {

    private final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    private final List<Object> rows = new ArrayList<>();
    private final boolean showDescriptions;
    private final int rowHeight;

    int left;
    int top;
    int right;
    int bottom;
    private int scroll = 0;

    TagListWidget(boolean showDescriptions) {
        this.showDescriptions = showDescriptions;
        this.rowHeight = showDescriptions ? 20 : 10;

        for (String category : TagRegistry.categories()) {
            rows.add(category);
            rows.addAll(TagRegistry.byCategory(category));
        }
    }

    void setBounds(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        clampScroll();
    }

    private int visibleRows() {
        return Math.max(1, (bottom - top - 4) / rowHeight);
    }

    private void clampScroll() {
        scroll = Math.max(0, Math.min(scroll, rows.size() - visibleRows()));
    }

    void scroll(int wheel) {
        if (wheel == 0) return;
        scroll += wheel > 0 ? -3 : 3;
        clampScroll();
    }

    boolean isMouseOver(int mouseX, int mouseY) {
        return GuiUtil.inside(mouseX, mouseY, left, top, right, bottom);
    }

    InfoTag getTagAt(int mouseX, int mouseY) {
        if (!isMouseOver(mouseX, mouseY)) return null;
        int index = scroll + (mouseY - top - 2) / rowHeight;
        if (index < 0 || index >= rows.size() || index - scroll >= visibleRows()) return null;
        Object row = rows.get(index);
        return row instanceof InfoTag ? (InfoTag) row : null;
    }

    void draw(int mouseX, int mouseY) {
        HudRenderer.drawRect(left, top, right, bottom, 0xA0000000);
        HudRenderer.drawFrame(left, top, right, bottom, 0x60FFFFFF);

        int width = right - left - 10;
        int visible = visibleRows();
        InfoTag hovered = getTagAt(mouseX, mouseY);

        for (int i = 0; i < visible && scroll + i < rows.size(); i++) {
            Object row = rows.get(scroll + i);
            int y = top + 2 + i * rowHeight;

            if (row instanceof String category) {
                fr.drawStringWithShadow(
                    fr.trimStringToWidth("§e§l" + TagRegistry.getCategoryName(category), width),
                    left + 3,
                    y + 1,
                    0xFFFFFF);
                continue;
            }

            InfoTag tag = (InfoTag) row;
            if (tag == hovered) {
                HudRenderer.drawRect(left + 1, y, right - 6, y + rowHeight, 0x40FFFFFF);
            }

            String text = "§b" + tag.getPlaceholder() + "§7 = §r" + formatValue(tag);
            fr.drawStringWithShadow(fr.trimStringToWidth(text, width), left + 6, y + 1, 0xFFFFFF);

            if (showDescriptions) {
                fr.drawStringWithShadow(
                    fr.trimStringToWidth("§7" + tag.getDescription(), width - 6),
                    left + 12,
                    y + 10,
                    0xFFFFFF);
            }
        }

        if (rows.size() > visible) {
            int trackHeight = bottom - top - 2;
            int barHeight = Math.max(8, trackHeight * visible / rows.size());
            int barY = top + 1 + (trackHeight - barHeight) * scroll / Math.max(1, rows.size() - visible);
            HudRenderer.drawRect(right - 5, top + 1, right - 1, bottom - 1, 0x40FFFFFF);
            HudRenderer.drawRect(right - 5, barY, right - 1, barY + barHeight, 0xC0FFFFFF);
        }
    }

    static String formatValue(InfoTag tag) {
        String value = tag.getValue();
        if (tag.condition) {
            return value != null ? "§a" + GuiUtil.t("tag.condition_true")
                : "§8" + GuiUtil.t("tag.condition_false");
        }
        if (value == null) return "§8" + GuiUtil.t("tag.unavailable");
        return value.isEmpty() ? "§8\"\"" : value;
    }
}
