package com.gtnewhorizons.infohud.hud.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.gtnewhorizons.infohud.hud.core.HudRenderer;
import com.gtnewhorizons.infohud.hud.layout.DefaultLines;
import com.gtnewhorizons.infohud.hud.layout.HudLayout;
import com.gtnewhorizons.infohud.hud.layout.HudLine;
import com.gtnewhorizons.infohud.hud.tags.InfoTag;
import com.gtnewhorizons.infohud.hud.tags.LineTemplate;

public class GuiLineEditor extends GuiScreen {

    private static final int BTN_DONE = 0;
    private static final int BTN_CANCEL = 1;
    private static final int BTN_ENABLED = 2;
    private static final int BTN_UP = 3;
    private static final int BTN_DOWN = 4;
    private static final int BTN_GROUP = 5;
    private static final int BTN_RESET = 6;
    private static final int BTN_DELETE = 7;
    private static final int BTN_HELD_ICON = 8;
    private static final int BTN_INSERT_ICON = 9;

    private final GuiHudEditor parent;
    private final HudLine line;
    private final HudLayout snapshot;

    private GuiTextField templateField;
    private GuiTextField iconField;
    private final TagListWidget tagList = new TagListWidget(false);
    private final FormatPalette palette = new FormatPalette();

    private GuiButton enabledButton;
    private GuiButton upButton;
    private GuiButton downButton;
    private GuiButton groupButton;
    private GuiButton resetButton;

    public GuiLineEditor(GuiHudEditor parent, HudLine line) {
        this(parent, line, parent.working.copy());
    }

    GuiLineEditor(GuiHudEditor parent, HudLine line, HudLayout snapshot) {
        this.parent = parent;
        this.line = line;
        this.snapshot = snapshot;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        buttonList.clear();

        String template = templateField != null ? templateField.getText() : line.template;
        String icon = iconField != null ? iconField.getText() : "";

        templateField = new GuiTextField(fontRendererObj, 10, 30, width - 20, 16);
        templateField.setMaxStringLength(512);
        templateField.setText(template);
        templateField.setFocused(true);

        iconField = new GuiTextField(fontRendererObj, 40, 72, 120, 16);
        iconField.setMaxStringLength(256);
        iconField.setText(icon);

        buttonList.add(new GuiButton(BTN_INSERT_ICON, 164, 70, 60, 20, GuiUtil.t("line.icon_insert")));
        buttonList.add(new GuiButton(BTN_HELD_ICON, 228, 70, 70, 20, GuiUtil.t("line.icon_held")));

        int y = 98;
        int w = 116;
        enabledButton = new GuiButton(BTN_ENABLED, 10, y, w, 20, "");
        upButton = new GuiButton(BTN_UP, 10, y + 22, 57, 20, GuiUtil.t("line.up"));
        downButton = new GuiButton(BTN_DOWN, 69, y + 22, 57, 20, GuiUtil.t("line.down"));
        groupButton = new GuiButton(BTN_GROUP, 10, y + 44, w, 20, "");
        resetButton = new GuiButton(BTN_RESET, 10, y + 66, w, 20, GuiUtil.t("line.reset"));
        buttonList.add(enabledButton);
        buttonList.add(upButton);
        buttonList.add(downButton);
        buttonList.add(groupButton);
        buttonList.add(resetButton);
        buttonList.add(new GuiButton(BTN_DELETE, 10, y + 88, w, 20, "§c" + GuiUtil.t("line.delete")));

        buttonList.add(new GuiButton(BTN_DONE, width - 134, height - 24, 60, 20, GuiUtil.t("done")));
        buttonList.add(new GuiButton(BTN_CANCEL, width - 70, height - 24, 60, 20, GuiUtil.t("cancel")));

        int panelLeft = 134;
        palette.setBounds(panelLeft, 98, width - 10 - panelLeft);
        tagList.setBounds(panelLeft, 100 + palette.getHeight(), width - 10, height - 28);

        updateButtons();
    }

    private void updateButtons() {
        enabledButton.displayString = GuiUtil.onOff("line.enabled", line.enabled);
        groupButton.displayString = GuiUtil.t(line.inGroup ? "line.detach" : "line.attach");

        int position = parent.working.getGroupPosition(line);
        int size = parent.working.getGroupLines()
            .size();
        upButton.enabled = line.inGroup && position > 1;
        downButton.enabled = line.inGroup && position < size;
        resetButton.enabled = line.isDefault() && DefaultLines.get(line.defaultId) != null;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        templateField.updateCursorCounter();
        iconField.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        FontRenderer fr = fontRendererObj;

        String title = GuiUtil.t("line.title");
        if (line.inGroup) {
            title += " §7(" + GuiUtil.t(
                "tooltip.group_position",
                parent.working.getGroupPosition(line),
                parent.working.getGroupLines()
                    .size())
                + ")";
        } else {
            title += " §7(" + GuiUtil.t("tooltip.detached") + ")";
        }
        drawCenteredString(fr, title, width / 2, 6, 0xFFFFFF);
        fr.drawStringWithShadow("§7" + GuiUtil.t("line.template_label"), 10, 19, 0xFFFFFF);
        templateField.drawTextBox();

        fr.drawStringWithShadow("§7" + GuiUtil.t("line.preview"), 10, 52, 0xFFFFFF);
        int previewX = 12 + fr.getStringWidth(GuiUtil.t("line.preview"));
        String preview = line.renderPreview();
        int[] rect = HudRenderer.getLineRect(preview, previewX, 46, 1.0F);
        HudRenderer.drawRect(rect[0] - 1, rect[1] - 1, rect[2] + 1, rect[3] + 1, 0x80000000);
        HudRenderer.drawLine(preview, previewX, 46, 1.0F, HudRenderer.TEXT_COLOR, 0);
        if (line.render() == null) {
            fr.drawStringWithShadow("§8" + GuiUtil.t("tooltip.hidden_now"), rect[2] + 6, 50, 0xFFFFFF);
        }

        fr.drawStringWithShadow("§7" + GuiUtil.t("line.icon"), 10, 76, 0xFFFFFF);
        iconField.drawTextBox();
        String iconName = iconField.getText()
            .trim();
        if (!iconName.isEmpty()) {
            if (HudRenderer.getIconStack(iconName) != null) {
                HudRenderer.drawLine(
                    LineTemplate.renderPreview(LineTemplate.iconTag(iconName)),
                    302,
                    68,
                    1.0F,
                    HudRenderer.TEXT_COLOR,
                    0);
            } else {
                fr.drawStringWithShadow("§c" + GuiUtil.t("line.icon_unknown"), 304, 76, 0xFFFFFF);
            }
        }

        palette.draw(mouseX, mouseY);
        tagList.draw(mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);

        InfoTag hoveredTag = tagList.getTagAt(mouseX, mouseY);
        int hoveredCode = palette.getIndexAt(mouseX, mouseY);
        if (hoveredTag != null) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add("§b" + hoveredTag.getPlaceholder());
            String description = hoveredTag.getDescription();
            if (!description.isEmpty()) {
                tooltip.addAll(fr.listFormattedStringToWidth(description, 200));
            }
            tooltip.add("§e" + GuiUtil.t("tooltip.click_insert"));
            GuiUtil.drawTooltip(fr, tooltip, mouseX, mouseY, width, height);
        } else if (hoveredCode >= 0) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add("&" + FormatPalette.getCode(hoveredCode) + " - " + GuiUtil.formatName(hoveredCode));
            tooltip.add("§e" + GuiUtil.t("tooltip.click_insert"));
            GuiUtil.drawTooltip(fr, tooltip, mouseX, mouseY, width, height);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        templateField.mouseClicked(mouseX, mouseY, mouseButton);
        iconField.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton != 0) return;

        InfoTag tag = tagList.getTagAt(mouseX, mouseY);
        if (tag != null) {
            insert(tag.getPlaceholder());
            return;
        }

        int code = palette.getIndexAt(mouseX, mouseY);
        if (code >= 0) {
            insert("&" + FormatPalette.getCode(code));
        }
    }

    private void insert(String text) {
        templateField.setFocused(true);
        iconField.setFocused(false);
        templateField.writeText(text);
        line.template = templateField.getText();
    }

    private void insertIcon(String name) {
        name = name.trim();
        if (!name.isEmpty()) {
            insert(LineTemplate.iconTag(name));
        }
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
            return;
        }

        if (keyCode == Keyboard.KEY_TAB) {
            boolean templateFocused = templateField.isFocused();
            templateField.setFocused(!templateFocused);
            iconField.setFocused(templateFocused);
            return;
        }

        if (templateField.textboxKeyTyped(typedChar, keyCode)) {
            line.template = templateField.getText();
        } else if (iconField.isFocused() && keyCode == Keyboard.KEY_RETURN) {
            insertIcon(iconField.getText());
        } else if (iconField.textboxKeyTyped(typedChar, keyCode)) {
            return;
        } else if (keyCode == Keyboard.KEY_RETURN) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case BTN_DONE -> {
                mc.displayGuiScreen(parent);
                return;
            }
            case BTN_CANCEL -> {
                parent.working = snapshot;
                mc.displayGuiScreen(parent);
                return;
            }
            case BTN_DELETE -> {
                parent.working.lines.remove(line);
                mc.displayGuiScreen(parent);
                return;
            }
            case BTN_ENABLED -> line.enabled = !line.enabled;
            case BTN_UP -> parent.working.moveInGroup(line, -1);
            case BTN_DOWN -> parent.working.moveInGroup(line, 1);
            case BTN_GROUP -> {
                if (line.inGroup) {
                    int x = Math.min(parent.working.groupX + 150, Math.max(0, width - 100));
                    parent.working.detach(line, x, HudRenderer.getGroupLineY(parent.working, 0));
                } else {
                    parent.working.attachToGroup(line, -1);
                }
            }
            case BTN_RESET -> {
                DefaultLines.Preset preset = DefaultLines.get(line.defaultId);
                if (preset != null) {
                    line.template = preset.getTemplate();
                    templateField.setText(line.template);
                }
            }
            case BTN_HELD_ICON -> {
                ItemStack held = mc.thePlayer != null ? mc.thePlayer.getHeldItem() : null;
                if (held != null) {
                    iconField.setText(HudLine.itemToString(held));
                    insertIcon(iconField.getText());
                }
            }
            case BTN_INSERT_ICON -> insertIcon(iconField.getText());
            default -> {}
        }
        updateButtons();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
