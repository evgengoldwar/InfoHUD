package com.gtnewhorizons.infohud.hud.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

import com.gtnewhorizons.infohud.hud.layout.HudLayout;

public class GuiElementSettings extends GuiScreen {

    public enum Element {
        GROUP,
        POTIONS,
        COUNT_ITEM
    }

    private static final int BTN_DONE = 0;
    private static final int BTN_ENABLED = 1;
    private static final int BTN_RESET_POS = 2;
    private static final int BTN_POTION_TIME = 3;
    private static final int BTN_POTION_LEVEL = 4;

    private final GuiHudEditor parent;
    private final Element element;

    public GuiElementSettings(GuiHudEditor parent, Element element) {
        this.parent = parent;
        this.element = element;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        HudLayout layout = parent.working;
        int x = width / 2 - 75;
        int y = height / 2 - 50;

        buttonList.add(new GuiButton(BTN_ENABLED, x, y, 150, 20, GuiUtil.onOff("element.enabled", enabled(layout))));
        y += 24;

        if (element == Element.POTIONS) {
            String time = GuiUtil.onOff("element.potion_time", layout.potionTime);
            buttonList.add(new GuiButton(BTN_POTION_TIME, x, y, 150, 20, time));
            y += 24;
            String level = GuiUtil.onOff("element.potion_level", layout.potionLevel);
            buttonList.add(new GuiButton(BTN_POTION_LEVEL, x, y, 150, 20, level));
            y += 24;
        }

        buttonList.add(new GuiButton(BTN_RESET_POS, x, y, 150, 20, GuiUtil.t("element.reset_position")));
        y += 30;
        buttonList.add(new GuiButton(BTN_DONE, x, y, 150, 20, GuiUtil.t("done")));
    }

    private boolean enabled(HudLayout layout) {
        return switch (element) {
            case GROUP -> layout.groupEnabled;
            case POTIONS -> layout.potionsEnabled;
            case COUNT_ITEM -> layout.countItemEnabled;
        };
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        String title = GuiUtil.t(switch (element) {
            case GROUP -> "editor.group";
            case POTIONS -> "editor.potions";
            case COUNT_ITEM -> "editor.count_item";
        });
        drawCenteredString(fontRendererObj, title, width / 2, height / 2 - 70, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        HudLayout layout = parent.working;

        switch (button.id) {
            case BTN_DONE -> {
                mc.displayGuiScreen(parent);
                return;
            }
            case BTN_ENABLED -> {
                switch (element) {
                    case GROUP -> layout.groupEnabled = !layout.groupEnabled;
                    case POTIONS -> layout.potionsEnabled = !layout.potionsEnabled;
                    case COUNT_ITEM -> layout.countItemEnabled = !layout.countItemEnabled;
                }
            }
            case BTN_POTION_TIME -> layout.potionTime = !layout.potionTime;
            case BTN_POTION_LEVEL -> layout.potionLevel = !layout.potionLevel;
            case BTN_RESET_POS -> {
                switch (element) {
                    case GROUP -> {
                        layout.groupX = HudLayout.DEFAULT_GROUP_X;
                        layout.groupY = HudLayout.DEFAULT_GROUP_Y;
                    }
                    case POTIONS -> {
                        layout.potionX = HudLayout.DEFAULT_POTION_X;
                        layout.potionY = HudLayout.DEFAULT_POTION_Y;
                    }
                    case COUNT_ITEM -> {
                        layout.countItemX = -1;
                        layout.countItemY = -1;
                    }
                }
            }
            default -> {}
        }
        initGui();
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
