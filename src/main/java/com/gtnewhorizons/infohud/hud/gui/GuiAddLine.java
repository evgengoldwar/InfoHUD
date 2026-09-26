package com.gtnewhorizons.infohud.hud.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.gtnewhorizons.infohud.hud.core.HudRenderer;
import com.gtnewhorizons.infohud.hud.layout.DefaultLines;
import com.gtnewhorizons.infohud.hud.layout.HudLayout;
import com.gtnewhorizons.infohud.hud.layout.HudLine;
import com.gtnewhorizons.infohud.hud.tags.LineTemplate;

public class GuiAddLine extends GuiScreen {

    private static final int ROW_HEIGHT = 16;

    private final GuiHudEditor parent;
    private final List<DefaultLines.Preset> entries = new ArrayList<>();
    private int scroll = 0;
    private int listLeft;
    private int listRight;
    private int listTop;
    private int listBottom;

    public GuiAddLine(GuiHudEditor parent) {
        this.parent = parent;
        entries.add(null);
        entries.addAll(DefaultLines.available());
    }

    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 - 50, height - 24, 100, 20, GuiUtil.t("back")));

        listLeft = Math.max(10, width / 2 - 180);
        listRight = Math.min(width - 10, width / 2 + 180);
        listTop = 32;
        listBottom = height - 30;
        clampScroll();
    }

    private int visibleRows() {
        return Math.max(1, (listBottom - listTop) / ROW_HEIGHT);
    }

    private void clampScroll() {
        scroll = Math.max(0, Math.min(scroll, entries.size() - visibleRows()));
    }

    private int getIndexAt(int mouseX, int mouseY) {
        if (!GuiUtil.inside(mouseX, mouseY, listLeft, listTop, listRight, listBottom)) return -1;
        int row = (mouseY - listTop) / ROW_HEIGHT;
        if (row >= visibleRows()) return -1;
        int index = scroll + row;
        return index < entries.size() ? index : -1;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        FontRenderer fr = fontRendererObj;

        drawCenteredString(fr, GuiUtil.t("add.title"), width / 2, 8, 0xFFFFFF);
        drawCenteredString(fr, "§7" + GuiUtil.t("add.subtitle"), width / 2, 19, 0xFFFFFF);

        HudRenderer.drawRect(listLeft, listTop, listRight, listBottom, 0xA0000000);
        HudRenderer.drawFrame(listLeft, listTop, listRight, listBottom, 0x60FFFFFF);

        int hovered = getIndexAt(mouseX, mouseY);
        int nameWidth = 100;

        for (int row = 0; row < visibleRows() && scroll + row < entries.size(); row++) {
            int index = scroll + row;
            DefaultLines.Preset preset = entries.get(index);
            int y = listTop + row * ROW_HEIGHT;

            if (index == hovered) {
                HudRenderer.drawRect(listLeft + 1, y, listRight - 1, y + ROW_HEIGHT, 0x40FFFFFF);
            }

            if (preset == null) {
                fr.drawStringWithShadow("§a+ " + GuiUtil.t("add.empty"), listLeft + 4, y + 4, 0xFFFFFF);
                continue;
            }

            String name = preset.getName();
            if (parent.working.hasDefault(preset.id)) {
                name = "§7" + name;
            }
            fr.drawStringWithShadow(fr.trimStringToWidth(name, nameWidth - 4), listLeft + 4, y + 4, 0xFFFFFF);

            String preview = LineTemplate.renderPreview(preset.getTemplate());
            HudRenderer.drawLine(preview, listLeft + nameWidth, y + 1, 1.0F, HudRenderer.TEXT_COLOR, 0);

            if (parent.working.hasDefault(preset.id)) {
                String added = "§8" + GuiUtil.t("add.already_added");
                fr.drawStringWithShadow(added, listRight - 4 - fr.getStringWidth(added), y + 4, 0xFFFFFF);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton != 0) return;

        int index = getIndexAt(mouseX, mouseY);
        if (index < 0) return;

        HudLayout snapshot = parent.working.copy();
        DefaultLines.Preset preset = entries.get(index);
        HudLine line = preset == null ? new HudLine(null, "") : preset.create();
        parent.working.lines.add(line);

        mc.displayGuiScreen(new GuiLineEditor(parent, line, snapshot));
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            scroll += wheel > 0 ? -1 : 1;
            clampScroll();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        mc.displayGuiScreen(parent);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
