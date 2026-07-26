package com.gtnewhorizons.infohud.hud.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.gtnewhorizons.infohud.configs.HudConfig;
import com.gtnewhorizons.infohud.hud.Hud;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoCountItem;
import com.gtnewhorizons.infohud.utils.Position;

public class GuiHudEditor extends GuiScreen {

    private InfoLine draggingLine = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    private List<InfoLine> lines;
    private InfoCountItem draggingCountItem = null;
    private int countDragOffsetX = 0;
    private int countDragOffsetY = 0;
    private int countItemX;
    private int countItemY;
    private boolean draggingEffects = false;
    private int effectDragOffsetX = 0;
    private int effectDragOffsetY = 0;
    private int effectX;
    private int effectY;
    private float currentScale;

    @Override
    public void initGui() {
        super.initGui();
        lines = new ArrayList<>(Hud.lines);
        lines.sort(Comparator.comparingInt(InfoLine::getOrder));

        DataStorage.loadPositions();
        DataStorage.HudPositionsData cachedData = DataStorage.getCachedPositions();

        if (cachedData != null) {
            for (InfoLine line : lines) {
                DataStorage.LinePosition cachedPos = cachedData.linePositions.get(
                    line.getClass()
                        .getSimpleName());
                if (cachedPos != null && cachedPos.isSet) {
                    if (line.position == null) {
                        line.position = new Position(cachedPos.x, cachedPos.y);
                    } else {
                        line.position.setX(cachedPos.x);
                        line.position.setY(cachedPos.y);
                    }
                    line.position.setEdit();
                } else {
                    if (line.position == null) {
                        line.position = new Position(0, 40 + lines.indexOf(line) * 11);
                    }
                }
            }
        }

        if (cachedData != null) {
            effectX = cachedData.effectX;
            effectY = cachedData.effectY;
            currentScale = cachedData.scale;
        } else {
            effectX = HudConfig.hudPotion.PotionX;
            effectY = HudConfig.hudPotion.PotionY;
            currentScale = HudConfig.hudGeneral.HudScale;
        }

        if (cachedData != null && cachedData.countItemX != -1 && cachedData.countItemY != -1) {
            countItemX = cachedData.countItemX;
            countItemY = cachedData.countItemY;
            for (InfoLine line : lines) {
                if (line instanceof InfoCountItem && line.position != null) {
                    line.position.setX(countItemX);
                    line.position.setY(countItemY);
                    line.position.setEdit();
                    break;
                }
            }
        } else {
            countItemX = width / 2 - 91 - 22;
            countItemY = height - 24;
        }

        HudConfig.hudPotion.PotionX = effectX;
        HudConfig.hudPotion.PotionY = effectY;
        HudConfig.hudGeneral.HudScale = currentScale;
        currentScale = HudConfig.hudGeneral.HudScale;

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height - 30, 60, 20, "Save"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 30, this.height - 30, 60, 20, "Reset"));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 40, this.height - 30, 60, 20, "Cancel"));

        int scaleButtonY = this.height - 55;
        int centerX = this.width / 2 - 30;
        this.buttonList.add(new GuiButton(3, centerX + 10, scaleButtonY, 20, 20, "-"));
        this.buttonList.add(new GuiButton(4, centerX + 30, scaleButtonY, 20, 20, "+"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (draggingEffects) {
            GL11.glPushMatrix();
            GL11.glTranslatef(effectX, effectY, 0);
            GL11.glScalef(currentScale, currentScale, currentScale);
            GL11.glTranslatef(-effectX, -effectY, 0);
            HudRenderer.drawRect(effectX - 5, effectY - 2, effectX + 250, effectY + 30, 0x40FFFF00);
            GL11.glPopMatrix();
        } else {
            GL11.glPushMatrix();
            GL11.glTranslatef(effectX, effectY, 0);
            GL11.glScalef(currentScale, currentScale, currentScale);
            GL11.glTranslatef(-effectX, -effectY, 0);
            HudRenderer.drawRect(effectX - 5, effectY - 2, effectX + 250, effectY + 30, 0x4000FFFF);
            GL11.glPopMatrix();
        }

        HudRenderer.renderEditor(
            lines,
            currentScale,
            effectX,
            effectY,
            countItemX,
            countItemY,
            draggingLine,
            draggingCountItem);

        int scaleButtonY = this.height - 55;
        int centerX = this.width / 2 - 30;
        drawCenteredString(
            fontRendererObj,
            "§eScale: §f" + String.format("%.1f", currentScale),
            centerX + 30,
            scaleButtonY - 12,
            0xFFFFFF);

        drawCenteredString(fontRendererObj, "§eDrag elements with left mouse button", width / 2, 10, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "§7Right-click to reset position", width / 2, 22, 0xFFFFFF);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            if (HudRenderer.isMouseOverEffects(mouseX, mouseY, effectX, effectY)) {
                draggingEffects = true;
                effectDragOffsetX = mouseX - effectX;
                effectDragOffsetY = mouseY - effectY;
                return;
            }

            for (InfoLine line : lines) {
                if (!line.canRender()) {
                    continue;
                }

                if (line instanceof InfoCountItem infoCountItem) {
                    if (infoCountItem.position != null && infoCountItem.position.isEdit()) {
                        if (HudRenderer.isMouseOverCountItem(
                            mouseX,
                            mouseY,
                            infoCountItem.position.getX(),
                            infoCountItem.position.getY())) {
                            draggingCountItem = infoCountItem;
                            countDragOffsetX = mouseX - infoCountItem.position.getX();
                            countDragOffsetY = mouseY - infoCountItem.position.getY();
                            return;
                        }
                    } else {
                        if (HudRenderer.isMouseOverCountItem(mouseX, mouseY, countItemX, countItemY)) {
                            draggingCountItem = infoCountItem;
                            countDragOffsetX = mouseX - countItemX;
                            countDragOffsetY = mouseY - countItemY;
                            return;
                        }
                    }
                }

                if (HudRenderer.isMouseOverLine(mouseX, mouseY, line, currentScale)) {
                    line.position.setEdit();
                    draggingLine = line;
                    dragOffsetX = mouseX - line.position.getX();
                    dragOffsetY = mouseY - line.position.getY();
                    return;
                }
            }
        } else if (mouseButton == 1) {
            if (HudRenderer.isMouseOverEffects(mouseX, mouseY, effectX, effectY)) {
                effectX = 5;
                HudConfig.hudPotion.PotionX = 5;
                effectY = 2;
                HudConfig.hudPotion.PotionY = 2;
                return;
            }

            for (InfoLine line : lines) {
                if (!line.canRender()) {
                    continue;
                }

                if (line instanceof InfoCountItem infoCountItem) {
                    countItemX = width / 2 - 91 - 22;
                    countItemY = height - 24;

                    if (infoCountItem.position != null && infoCountItem.position.isEdit()) {
                        if (HudRenderer.isMouseOverCountItem(
                            mouseX,
                            mouseY,
                            infoCountItem.position.getX(),
                            infoCountItem.position.getY())) {
                            countItemX = width / 2 - 91 - 22;
                            countItemY = height - 24;
                            infoCountItem.position.setEdit();
                            infoCountItem.position.setX(countItemX);
                            infoCountItem.position.setY(countItemY);
                            return;
                        }
                    } else {
                        if (HudRenderer.isMouseOverCountItem(mouseX, mouseY, countItemX, countItemY)) {
                            countItemX = width / 2 - 91 - 22;
                            countItemY = height - 24;
                            return;
                        }
                    }
                }
                if (line.position != null && line.position.isEdit()) {
                    if (HudRenderer.isMouseOverLine(mouseX, mouseY, line, currentScale)) {
                        line.position.resetToDefault();
                        draggingLine = null;
                        return;
                    }
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which) {
        if (which == 0 || which == -1) {
            draggingLine = null;
            draggingEffects = false;
            draggingCountItem = null;
        }
        super.mouseMovedOrUp(mouseX, mouseY, which);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        if (Mouse.isButtonDown(0)) {
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

            if (draggingLine != null) {
                draggingLine.position.setX(mouseX - dragOffsetX);
                draggingLine.position.setY(mouseY - dragOffsetY);
            } else if (draggingEffects) {
                effectX = mouseX - effectDragOffsetX;
                effectY = mouseY - effectDragOffsetY;
            } else if (draggingCountItem != null) {
                countItemX = mouseX - countDragOffsetX;
                countItemY = mouseY - countDragOffsetY;

                if (draggingCountItem.position != null) {
                    draggingCountItem.position.setEdit();
                    draggingCountItem.position.setX(countItemX);
                    draggingCountItem.position.setY(countItemY);
                } else {
                    draggingCountItem.position = new Position(countItemX, countItemY);
                }
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                savePositions();
                mc.displayGuiScreen(null);
                break;
            case 1:
                resetAllPositions();
                break;
            case 2:
                cancelChanges();
                mc.displayGuiScreen(null);
                break;
            case 3:
                currentScale = Math.max(0.5f, currentScale - 0.1f);
                break;
            case 4:
                currentScale = Math.min(2.0f, currentScale + 0.1f);
                break;
        }
    }

    private void savePositions() {
        HudConfig.hudPotion.PotionX = effectX;
        HudConfig.hudPotion.PotionY = effectY;
        HudConfig.hudGeneral.HudScale = currentScale;
        DataStorage.savePositions(lines, effectX, effectY, currentScale);
    }

    private void resetAllPositions() {
        effectX = 5;
        HudConfig.hudPotion.PotionX = 5;
        effectY = 2;
        HudConfig.hudPotion.PotionY = 2;
        countItemX = width / 2 - 91 - 22;
        countItemY = height - 24;
        currentScale = 1.0F;
        HudConfig.hudGeneral.HudScale = 1.0F;
        for (InfoLine line : lines) {
            if (line.position != null) {
                line.position.resetToDefault();
            }
        }
    }

    private void cancelChanges() {
        for (InfoLine line : lines) {
            if (line.position != null && line.position.isEdit()) {
                line.position.resetToDefault();
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
