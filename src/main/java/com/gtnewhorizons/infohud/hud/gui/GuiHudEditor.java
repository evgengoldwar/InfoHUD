package com.gtnewhorizons.infohud.hud.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import com.gtnewhorizons.infohud.hud.core.HudRenderer;
import com.gtnewhorizons.infohud.hud.layout.HudLayout;
import com.gtnewhorizons.infohud.hud.layout.HudLayoutStorage;
import com.gtnewhorizons.infohud.hud.layout.HudLine;

public class GuiHudEditor extends GuiScreen {

    private static final int BTN_SAVE = 0;
    private static final int BTN_CANCEL = 1;
    private static final int BTN_ADD = 2;
    private static final int BTN_TAGS = 3;
    private static final int BTN_SCALE_DOWN = 4;
    private static final int BTN_SCALE_UP = 5;
    private static final int BTN_HUD_TOGGLE = 6;
    private static final int BTN_RESET_ALL = 7;

    private static final int NONE = 0;
    private static final int LINE = 1;
    private static final int GROUP = 2;
    private static final int POTIONS = 3;
    private static final int COUNT = 4;

    HudLayout working;

    private final List<LineBox> boxes = new ArrayList<>();
    private int[] groupRect;
    private int[] potionRect;
    private int[] countRect;
    private int toolbarLeft;
    private int toolbarRight;

    private int pressTarget = NONE;
    private HudLine pressLine;
    private int pressButton = -1;
    private int pressX;
    private int pressY;
    private boolean pressShift;
    private boolean dragging;
    private int dragTarget = NONE;
    private int dragOffsetX;
    private int dragOffsetY;

    private GuiButton hudToggleButton;

    private static class LineBox {

        HudLine line;
        String text;
        ItemStack icon;
        int x;
        int y;
        int[] rect;
        boolean available;
    }

    public GuiHudEditor() {
        this.working = HudLayoutStorage.get()
            .copy();
    }

    @Override
    public void initGui() {
        buttonList.clear();

        int rowWidth = 70 + 60 + 20 + 20 + 80 + 80 + 5 * 3;
        toolbarLeft = width / 2 - rowWidth / 2;
        toolbarRight = toolbarLeft + rowWidth;
        int x = toolbarLeft;
        int row1 = height - 46;
        int row2 = height - 24;

        buttonList.add(new GuiButton(BTN_ADD, x, row1, 70, 20, GuiUtil.t("editor.add_line")));
        x += 73;
        buttonList.add(new GuiButton(BTN_TAGS, x, row1, 60, 20, GuiUtil.t("editor.tags")));
        x += 63;
        buttonList.add(new GuiButton(BTN_SCALE_DOWN, x, row1, 20, 20, "-"));
        x += 23;
        buttonList.add(new GuiButton(BTN_SCALE_UP, x, row1, 20, 20, "+"));
        x += 23;
        hudToggleButton = new GuiButton(BTN_HUD_TOGGLE, x, row1, 80, 20, "");
        buttonList.add(hudToggleButton);
        x += 83;
        buttonList.add(new GuiButton(BTN_RESET_ALL, x, row1, 80, 20, GuiUtil.t("editor.reset_all")));

        buttonList.add(new GuiButton(BTN_SAVE, width / 2 - 84, row2, 80, 20, GuiUtil.t("save")));
        buttonList.add(new GuiButton(BTN_CANCEL, width / 2 + 4, row2, 80, 20, GuiUtil.t("cancel")));

        updateButtons();
    }

    private void updateButtons() {
        hudToggleButton.displayString = GuiUtil.onOff("editor.hud", !working.hudDisabled);
    }

    private void computeLayout() {
        boxes.clear();
        float scale = working.scale;

        int index = 0;
        groupRect = null;
        for (HudLine line : working.getGroupLines()) {
            LineBox box = createBox(line, working.groupX, HudRenderer.getGroupLineY(working, index), scale);
            groupRect = union(groupRect, box.rect);
            index++;
        }

        if (groupRect == null) {
            groupRect = HudRenderer
                .getLineRect(GuiUtil.t("editor.empty_group"), null, working.groupX, working.groupY, scale);
        }
        groupRect = new int[] { groupRect[0] - 2, groupRect[1] - 2, groupRect[2] + 2, groupRect[3] + 2 };

        for (HudLine line : working.getDetachedLines()) {
            createBox(line, line.x, line.y, scale);
        }

        int potionWidth = Math
            .max(60, HudRenderer.getDisplayedPotions()
                .size() * HudRenderer.getPotionEntryWidth());
        potionRect = new int[] { working.potionX - 2, working.potionY - 2,
            working.potionX + (int) (potionWidth * scale) + 2,
            working.potionY + (int) ((HudRenderer.POTION_ICON_SIZE + 10) * scale) + 2 };

        int cx = HudRenderer.getCountItemX(working, width);
        int cy = HudRenderer.getCountItemY(working, height);
        countRect = new int[] { cx - 2, cy - 1, cx + HudRenderer.COUNT_ITEM_WIDTH + 2,
            cy + HudRenderer.COUNT_ITEM_HEIGHT + 1 };
    }

    private LineBox createBox(HudLine line, int x, int y, float scale) {
        LineBox box = new LineBox();
        box.line = line;
        box.text = line.renderPreview();
        box.icon = line.getIconStack();
        box.x = x;
        box.y = y;
        box.rect = HudRenderer.getLineRect(box.text, box.icon, x, y, scale);
        box.available = line.render() != null;
        boxes.add(box);
        return box;
    }

    private static int[] union(int[] a, int[] b) {
        if (a == null) return b.clone();
        return new int[] { Math.min(a[0], b[0]), Math.min(a[1], b[1]), Math.max(a[2], b[2]), Math.max(a[3], b[3]) };
    }

    private static boolean inside(int[] rect, int x, int y) {
        return rect != null && GuiUtil.inside(x, y, rect[0], rect[1], rect[2], rect[3]);
    }

    private LineBox getBoxAt(int mouseX, int mouseY) {
        for (int i = boxes.size() - 1; i >= 0; i--) {
            LineBox box = boxes.get(i);
            if (inside(box.rect, mouseX, mouseY)) return box;
        }
        return null;
    }

    private LineBox getBox(HudLine line) {
        for (LineBox box : boxes) {
            if (box.line == line) return box;
        }
        return null;
    }

    private int getInsertIndex(int mouseY) {
        int index = 0;
        for (LineBox box : boxes) {
            if (!box.line.inGroup) continue;
            if ((box.rect[1] + box.rect[3]) / 2 < mouseY) index++;
        }
        return index;
    }

    private boolean isDropIntoGroup(int mouseX, int mouseY) {
        return dragging && dragTarget == LINE
            && pressLine != null
            && !pressLine.inGroup
            && isShiftKeyDown()
            && inside(groupRect, mouseX, mouseY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        computeLayout();

        FontRenderer fr = fontRendererObj;
        float scale = working.scale;

        boolean potionsActive = dragging && dragTarget == POTIONS;
        HudRenderer.drawRect(
            potionRect[0],
            potionRect[1],
            potionRect[2],
            potionRect[3],
            potionsActive ? 0x40FFFF00 : working.potionsEnabled ? 0x2000FFFF : 0x30FF0000);
        HudRenderer.drawFrame(
            potionRect[0],
            potionRect[1],
            potionRect[2],
            potionRect[3],
            potionsActive ? 0xFFFFFF00 : 0x9000FFFF);
        drawLabel(GuiUtil.t("editor.potions"), potionRect);
        HudRenderer.drawPotions(working, working.potionX, working.potionY);

        boolean groupActive = (dragging && dragTarget == GROUP) || isDropIntoGroup(mouseX, mouseY);
        HudRenderer.drawRect(
            groupRect[0],
            groupRect[1],
            groupRect[2],
            groupRect[3],
            working.groupEnabled ? 0x2000FFFF : 0x30FF0000);
        HudRenderer.drawFrame(
            groupRect[0],
            groupRect[1],
            groupRect[2],
            groupRect[3],
            groupActive ? 0xFFFFFF00 : 0x9000FFFF);
        drawLabel(GuiUtil.t("editor.group"), groupRect);
        if (working.getGroupLines()
            .isEmpty()) {
            fr.drawStringWithShadow(
                "§7" + GuiUtil.t("editor.empty_group"),
                working.groupX + 4,
                working.groupY + 4,
                0xFFFFFF);
        }

        LineBox hovered = pressTarget == NONE ? getBoxAt(mouseX, mouseY) : null;
        for (LineBox box : boxes) {
            int color = HudRenderer.TEXT_COLOR;
            int background = 0;

            if (!box.line.enabled) {
                color = 0x80E0E0E0;
                background = 0x40FF0000;
            } else if (!box.available) {
                color = 0xA0E0E0E0;
            }

            if (dragging && dragTarget == LINE && box.line == pressLine) {
                background = 0x60FFFF00;
            } else if (box == hovered) {
                background = 0x40FFFFFF;
            }

            HudRenderer.drawLine(box.text, box.x, box.y, box.icon, scale, color, background);

            if (!box.line.inGroup) {
                HudRenderer.drawFrame(box.rect[0] - 1, box.rect[1] - 1, box.rect[2] + 1, box.rect[3] + 1, 0x9000FF00);
            }
        }

        if (isDropIntoGroup(mouseX, mouseY)) {
            int index = getInsertIndex(mouseY);
            int y = HudRenderer.getGroupLineY(working, index) + (int) (2 * scale);
            HudRenderer.drawRect(groupRect[0], y - 1, groupRect[2], y + 1, 0xFFFFFF00);
        }

        boolean countActive = dragging && dragTarget == COUNT;
        HudRenderer.drawRect(
            countRect[0],
            countRect[1],
            countRect[2],
            countRect[3],
            countActive ? 0x40FFFF00 : working.countItemEnabled ? 0x2000FFFF : 0x30FF0000);
        HudRenderer.drawFrame(countRect[0], countRect[1], countRect[2], countRect[3], 0x60FFFFFF);
        String count = HudRenderer.getCountItemText();
        ItemStack held = mc.thePlayer != null ? mc.thePlayer.getHeldItem() : null;
        HudRenderer.drawCountItem(
            count != null ? count : "#",
            countRect[0] + 2,
            countRect[1] + 1,
            count != null ? held : null);

        String hint1 = GuiUtil.t("editor.hint1");
        String hint2 = GuiUtil.t("editor.hint2");
        int hintWidth = Math.max(fr.getStringWidth(hint1), fr.getStringWidth(hint2)) / 2 + 5;
        HudRenderer.drawRect(width / 2 - hintWidth, 2, width / 2 + hintWidth, 25, 0x80000000);
        drawCenteredString(fr, hint1, width / 2, 4, 0xFFFFFF);
        drawCenteredString(fr, hint2, width / 2, 14, 0xFFFFFF);

        HudRenderer.drawRect(toolbarLeft - 4, height - 50, toolbarRight + 4, height - 2, 0x90000000);
        fr.drawStringWithShadow(
            GuiUtil.t("editor.scale", String.format("%.1f", scale)),
            width / 2 + 90,
            height - 18,
            0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (hovered != null) {
            drawLineTooltip(hovered, mouseX, mouseY);
        } else if (pressTarget == NONE && isOverGroup(mouseX, mouseY)) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add("\u00a7f" + GuiUtil.t("editor.group"));
            if (!working.groupEnabled) {
                tooltip.add("\u00a7c" + GuiUtil.t("tooltip.group_disabled"));
            }
            tooltip.add("\u00a7e" + GuiUtil.t("tooltip.drag_group"));
            tooltip.add("\u00a7e" + GuiUtil.t("tooltip.rmb_settings"));
            GuiUtil.drawTooltip(fr, tooltip, mouseX, mouseY, width, height);
        } else if (pressTarget == NONE && isOverPotions(mouseX, mouseY)) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add("§f" + GuiUtil.t("editor.potions"));
            tooltip.add("§e" + GuiUtil.t("tooltip.rmb_settings"));
            GuiUtil.drawTooltip(fr, tooltip, mouseX, mouseY, width, height);
        } else if (pressTarget == NONE && inside(countRect, mouseX, mouseY)) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add("§f" + GuiUtil.t("editor.count_item"));
            tooltip.add("§e" + GuiUtil.t("tooltip.rmb_settings"));
            GuiUtil.drawTooltip(fr, tooltip, mouseX, mouseY, width, height);
        }
    }

    private void drawLabel(String text, int[] rect) {
        int[] label = getLabelRect(text, rect);
        fontRendererObj.drawStringWithShadow("\u00a7b" + text, label[0] + 2, label[1] + 1, 0xFFFFFF);
    }

    private int[] getLabelRect(String text, int[] rect) {
        int top = rect[1] >= 10 ? rect[1] - 10 : rect[3];
        return new int[] { rect[0], top, rect[0] + fontRendererObj.getStringWidth(text) + 4, top + 10 };
    }

    private boolean isOverGroup(int mouseX, int mouseY) {
        return inside(groupRect, mouseX, mouseY)
            || inside(getLabelRect(GuiUtil.t("editor.group"), groupRect), mouseX, mouseY);
    }

    private boolean isOverPotions(int mouseX, int mouseY) {
        return inside(potionRect, mouseX, mouseY)
            || inside(getLabelRect(GuiUtil.t("editor.potions"), potionRect), mouseX, mouseY);
    }

    private void drawLineTooltip(LineBox box, int mouseX, int mouseY) {
        FontRenderer fr = fontRendererObj;
        List<String> tooltip = new ArrayList<>();
        tooltip.add("§7" + GuiUtil.t("tooltip.template"));
        tooltip.add("§f" + fr.trimStringToWidth(box.line.template, 300));

        if (!box.line.enabled) {
            tooltip.add("§c" + GuiUtil.t("tooltip.disabled"));
        } else if (!box.available) {
            tooltip.add("§8" + GuiUtil.t("tooltip.hidden_now"));
        }

        if (box.line.inGroup) {
            tooltip.add(
                "§7" + GuiUtil.t(
                    "tooltip.group_position",
                    working.getGroupPosition(box.line),
                    working.getGroupLines()
                        .size()));
            tooltip.add("§e" + GuiUtil.t("tooltip.drag_group"));
            tooltip.add("§e" + GuiUtil.t("tooltip.shift_drag_detach"));
        } else {
            tooltip.add("§a" + GuiUtil.t("tooltip.detached"));
            tooltip.add("§e" + GuiUtil.t("tooltip.shift_drop_attach"));
        }
        tooltip.add("§e" + GuiUtil.t("tooltip.rmb_settings"));

        GuiUtil.drawTooltip(fr, tooltip, mouseX, mouseY, width, height);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (Object obj : buttonList) {
            if (((GuiButton) obj).mousePressed(mc, mouseX, mouseY)) {
                super.mouseClicked(mouseX, mouseY, mouseButton);
                return;
            }
        }

        if (mouseButton != 0 && mouseButton != 1) return;

        computeLayout();
        resetPress();

        if (inside(countRect, mouseX, mouseY)) {
            pressTarget = COUNT;
        } else {
            LineBox box = getBoxAt(mouseX, mouseY);
            if (box != null) {
                pressTarget = LINE;
                pressLine = box.line;
            } else if (isOverGroup(mouseX, mouseY)) {
                pressTarget = GROUP;
            } else if (isOverPotions(mouseX, mouseY)) {
                pressTarget = POTIONS;
            }
        }

        if (pressTarget != NONE) {
            pressButton = mouseButton;
            pressX = mouseX;
            pressY = mouseY;
            pressShift = isShiftKeyDown();
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceClick) {
        if (pressTarget == NONE || button != pressButton) return;

        if (!dragging) {
            if (Math.abs(mouseX - pressX) + Math.abs(mouseY - pressY) < 3) return;
            startDrag();
        }

        int x = Math.max(0, Math.min(width - 4, mouseX - dragOffsetX));
        int y = Math.max(0, Math.min(height - 4, mouseY - dragOffsetY));

        switch (dragTarget) {
            case GROUP -> {
                working.groupX = x;
                working.groupY = y;
            }
            case LINE -> {
                pressLine.x = x;
                pressLine.y = y;
            }
            case POTIONS -> {
                working.potionX = x;
                working.potionY = y;
            }
            case COUNT -> {
                working.countItemX = x;
                working.countItemY = y;
            }
            default -> {}
        }
    }

    private void startDrag() {
        dragging = true;

        switch (pressTarget) {
            case LINE -> {
                if (pressLine.inGroup && !pressShift) {
                    dragTarget = GROUP;
                    dragOffsetX = pressX - working.groupX;
                    dragOffsetY = pressY - working.groupY;
                } else {
                    if (pressLine.inGroup) {
                        LineBox box = getBox(pressLine);
                        working.detach(pressLine, box != null ? box.x : pressX, box != null ? box.y : pressY);
                    }
                    dragTarget = LINE;
                    dragOffsetX = pressX - pressLine.x;
                    dragOffsetY = pressY - pressLine.y;
                }
            }
            case GROUP -> {
                dragTarget = GROUP;
                dragOffsetX = pressX - working.groupX;
                dragOffsetY = pressY - working.groupY;
            }
            case POTIONS -> {
                dragTarget = POTIONS;
                dragOffsetX = pressX - working.potionX;
                dragOffsetY = pressY - working.potionY;
            }
            case COUNT -> {
                dragTarget = COUNT;
                working.countItemX = HudRenderer.getCountItemX(working, width);
                working.countItemY = HudRenderer.getCountItemY(working, height);
                dragOffsetX = pressX - working.countItemX;
                dragOffsetY = pressY - working.countItemY;
            }
            default -> dragging = false;
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which) {
        super.mouseMovedOrUp(mouseX, mouseY, which);
        if (which < 0 || which != pressButton) return;

        if (dragging) {
            computeLayout();
            if (isDropIntoGroup(mouseX, mouseY)) {
                working.attachToGroup(pressLine, getInsertIndex(mouseY));
            }
        } else if (which == 1) {
            openSettings();
        }

        resetPress();
    }

    private void openSettings() {
        switch (pressTarget) {
            case LINE -> mc.displayGuiScreen(new GuiLineEditor(this, pressLine));
            case GROUP -> mc.displayGuiScreen(new GuiElementSettings(this, GuiElementSettings.Element.GROUP));
            case POTIONS -> mc.displayGuiScreen(new GuiElementSettings(this, GuiElementSettings.Element.POTIONS));
            case COUNT -> mc.displayGuiScreen(new GuiElementSettings(this, GuiElementSettings.Element.COUNT_ITEM));
            default -> {}
        }
    }

    private void resetPress() {
        pressTarget = NONE;
        pressLine = null;
        pressButton = -1;
        pressShift = false;
        dragging = false;
        dragTarget = NONE;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case BTN_SAVE -> {
                HudLayoutStorage.save(working.copy());
                mc.displayGuiScreen(null);
            }
            case BTN_CANCEL -> mc.displayGuiScreen(null);
            case BTN_ADD -> mc.displayGuiScreen(new GuiAddLine(this));
            case BTN_TAGS -> mc.displayGuiScreen(new GuiTagList(this));
            case BTN_SCALE_DOWN -> working.setScale(working.scale - 0.1F);
            case BTN_SCALE_UP -> working.setScale(working.scale + 0.1F);
            case BTN_HUD_TOGGLE -> working.hudDisabled = !working.hudDisabled;
            case BTN_RESET_ALL -> mc.displayGuiScreen(
                new GuiConfirm(
                    this,
                    GuiUtil.t("confirm.reset_all"),
                    GuiUtil.t("confirm.reset_all.desc"),
                    () -> working = HudLayout.createDefault()));
            default -> {}
        }
        updateButtons();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
