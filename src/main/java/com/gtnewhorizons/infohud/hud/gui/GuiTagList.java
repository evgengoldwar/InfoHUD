package com.gtnewhorizons.infohud.hud.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.gtnewhorizons.infohud.hud.tags.InfoTag;

public class GuiTagList extends GuiScreen {

    private final GuiScreen parent;
    private final TagListWidget tagList = new TagListWidget(true);
    private final FormatPalette palette = new FormatPalette();
    private String copied = null;
    private int copiedTimer = 0;

    public GuiTagList(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 - 50, height - 24, 100, 20, GuiUtil.t("back")));

        int left = Math.max(10, width / 2 - 200);
        int right = Math.min(width - 10, width / 2 + 200);
        palette.setBounds(left + 60, 50, right - left - 60);
        tagList.setBounds(left, 54 + palette.getHeight(), right, height - 30);
    }

    @Override
    public void updateScreen() {
        if (copiedTimer > 0) copiedTimer--;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        FontRenderer fr = fontRendererObj;

        drawCenteredString(fr, GuiUtil.t("tags.title"), width / 2, 6, 0xFFFFFF);
        drawCenteredString(fr, "§7" + GuiUtil.t("tags.subtitle"), width / 2, 17, 0xFFFFFF);
        drawCenteredString(fr, "§7" + GuiUtil.t("tags.conditions"), width / 2, 27, 0xFFFFFF);
        drawCenteredString(fr, "§7" + GuiUtil.t("tags.filters"), width / 2, 37, 0xFFFFFF);
        fr.drawStringWithShadow("§7" + GuiUtil.t("tags.colors"), tagList.left, 52, 0xFFFFFF);

        palette.draw(mouseX, mouseY);
        tagList.draw(mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (copiedTimer > 0 && copied != null) {
            drawCenteredString(fr, "§a" + GuiUtil.t("tags.copied", copied), width / 2, height - 36, 0xFFFFFF);
        }

        int code = palette.getIndexAt(mouseX, mouseY);
        if (code >= 0) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add("&" + FormatPalette.getCode(code) + " - " + GuiUtil.formatName(code));
            tooltip.add("§e" + GuiUtil.t("tooltip.click_copy"));
            GuiUtil.drawTooltip(fr, tooltip, mouseX, mouseY, width, height);
        } else if (tagList.getTagAt(mouseX, mouseY) != null) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add("§e" + GuiUtil.t("tooltip.click_copy"));
            GuiUtil.drawTooltip(fr, tooltip, mouseX, mouseY, width, height);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton != 0) return;

        InfoTag tag = tagList.getTagAt(mouseX, mouseY);
        if (tag != null) {
            copy(tag.getPlaceholder());
            return;
        }

        int code = palette.getIndexAt(mouseX, mouseY);
        if (code >= 0) {
            copy("&" + FormatPalette.getCode(code));
        }
    }

    private void copy(String text) {
        setClipboardString(text);
        copied = text;
        copiedTimer = 40;
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            tagList.scroll(wheel);
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
