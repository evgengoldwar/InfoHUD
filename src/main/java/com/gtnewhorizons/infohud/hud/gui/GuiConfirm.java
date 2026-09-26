package com.gtnewhorizons.infohud.hud.gui;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

public class GuiConfirm extends GuiScreen {

    private final GuiScreen parent;
    private final String title;
    private final String description;
    private final Runnable onConfirm;

    public GuiConfirm(GuiScreen parent, String title, String description, Runnable onConfirm) {
        this.parent = parent;
        this.title = title;
        this.description = description;
        this.onConfirm = onConfirm;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 - 104, height / 2 + 20, 100, 20, GuiUtil.t("yes")));
        buttonList.add(new GuiButton(1, width / 2 + 4, height / 2 + 20, 100, 20, GuiUtil.t("no")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, title, width / 2, height / 2 - 30, 0xFFFFFF);

        List<?> lines = fontRendererObj.listFormattedStringToWidth(description, Math.min(300, width - 20));
        for (int i = 0; i < lines.size(); i++) {
            drawCenteredString(fontRendererObj, "§7" + lines.get(i), width / 2, height / 2 - 16 + i * 10, 0xFFFFFF);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            onConfirm.run();
        }
        mc.displayGuiScreen(parent);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
