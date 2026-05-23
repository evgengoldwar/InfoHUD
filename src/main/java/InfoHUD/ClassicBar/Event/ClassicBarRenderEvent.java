package InfoHUD.ClassicBar.Event;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import InfoHUD.ClassicBar.Renderer.ProgressBarAPI;
import InfoHUD.ClassicBar.Renderer.ProgressBarAPI.AnimationStyle;
import InfoHUD.ClassicBar.Renderer.ProgressBarAPI.NumberFormat;
import InfoHUD.ClassicBar.Renderer.ProgressBarAPI.Side;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClassicBarRenderEvent {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final int BAR_WIDTH = 81;
    private static final int BAR_HEIGHT = 12;
    private static final int ICON_SIZE = 9;
    private static final int GAP = 2;

    private static final int ARMOR_FILL_COLOR = 0xFF9999AA;
    private static final int ARMOR_BG_COLOR = 0xFF252533;
    private static final int ARMOR_TEXT_COLOR = 0xFFCCCCDD;

    private static final int HEALTH_FILL_COLOR = 0xFFDD4444;
    private static final int HEALTH_BG_COLOR = 0xFF331111;
    private static final int HEALTH_TEXT_COLOR = 0xFFFF6666;

    private static final int FOOD_FILL_COLOR = 0xFFDD9922;
    private static final int FOOD_BG_COLOR = 0xFF332211;
    private static final int FOOD_TEXT_COLOR = 0xFFFFCC66;

    private static final int AIR_FILL_COLOR = 0xFF4499DD;
    private static final int AIR_BG_COLOR = 0xFF112233;
    private static final int AIR_TEXT_COLOR = 0xFF88CCFF;

    private static final int OVERLAY_COLOR = 0xFFFFDD44;

    private static final ResourceLocation ARMOR_ICON = new ResourceLocation("infohud", "textures/gui/armor.png");
    private static final ResourceLocation HEART_ICON = new ResourceLocation("infohud", "textures/gui/heart.png");
    private static final ResourceLocation FOOD_ICON = new ResourceLocation("infohud", "textures/gui/food.png");
    private static final ResourceLocation BUBBLE_ICON = new ResourceLocation("infohud", "textures/gui/bubble.png");

    private ProgressBarAPI healthBar;
    private ProgressBarAPI armorBar;
    private ProgressBarAPI foodBar;
    private ProgressBarAPI airBar;

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        int width = event.resolution.getScaledWidth();
        int height = event.resolution.getScaledHeight();

        int leftBarX = width / 2 - 120;
        int rightBarX = width / 2 + 10;

        int barY = height - 45;

        if (healthBar == null) {
            initBars(leftBarX, rightBarX, barY);
        }

        updateBarPositions(leftBarX, rightBarX, barY);
        updateBarProgress();
        renderBars(rightBarX, barY);
    }

    private void initBars(int leftBarX, int rightBarX, int barY) {
        healthBar = new ProgressBarAPI(leftBarX, barY, BAR_WIDTH, BAR_HEIGHT).setFillColor(HEALTH_FILL_COLOR)
            .setBackgroundColor(HEALTH_BG_COLOR)
            .setTextColor(HEALTH_TEXT_COLOR)
            .setTextSide(Side.LEFT)
            .setNumberFormat(NumberFormat.CURRENT)
            .setIcon(HEART_ICON, ICON_SIZE, ICON_SIZE, 0, 0, 16, 16)
            .setIconSide(Side.LEFT)
            .setBorderWidth(1)
            .setAnimationStyle(AnimationStyle.BOUNCE)
            .setAnimationSpeed(0.15f)
            .setShowGlow(true)
            .setGlowColor(0xFFFF4444)
            .setShowShine(true);

        foodBar = new ProgressBarAPI(rightBarX, barY, BAR_WIDTH, BAR_HEIGHT).setFillColor(FOOD_FILL_COLOR)
            .setBackgroundColor(FOOD_BG_COLOR)
            .setTextColor(FOOD_TEXT_COLOR)
            .setTextSide(Side.RIGHT)
            .setNumberFormat(NumberFormat.CURRENT)
            .setIcon(FOOD_ICON, ICON_SIZE, ICON_SIZE, 0, 0, 16, 16)
            .setIconSide(Side.RIGHT)
            .setBorderWidth(1)
            .setAnimationStyle(AnimationStyle.SMOOTH)
            .setAnimationSpeed(0.12f)
            .setShowGlow(false)
            .setShowShine(true);
    }

    private void updateBarPositions(int leftBarX, int rightBarX, int barY) {
        if (healthBar != null) {
            healthBar.setPosition(leftBarX, barY);
        }
        if (armorBar != null) {
            armorBar.setPosition(leftBarX, barY - BAR_HEIGHT - GAP);
        }
        if (foodBar != null) {
            foodBar.setPosition(rightBarX, barY);
        }
        if (airBar != null) {
            airBar.setPosition(rightBarX, barY - BAR_HEIGHT - GAP);
        }
    }

    private void updateBarProgress() {
        float currentHP = mc.thePlayer.getHealth();
        float maxHP = mc.thePlayer.getMaxHealth();
        float currentArmor = mc.thePlayer.getTotalArmorValue();
        float maxArmor = 20F;
        float currentFood = mc.thePlayer.getFoodStats()
            .getFoodLevel();
        float maxFood = 20F;

        healthBar.setProgress(currentHP, maxHP);
        foodBar.setProgress(currentFood, maxFood);

        if (currentArmor > 0F) {
            if (armorBar == null) {
                armorBar = new ProgressBarAPI(0, 0, BAR_WIDTH, BAR_HEIGHT).setFillColor(ARMOR_FILL_COLOR)
                    .setBackgroundColor(ARMOR_BG_COLOR)
                    .setTextColor(ARMOR_TEXT_COLOR)
                    .setTextSide(Side.LEFT)
                    .setNumberFormat(NumberFormat.CURRENT)
                    .setIcon(ARMOR_ICON, ICON_SIZE, ICON_SIZE, 0, 0, 16, 16)
                    .setIconSide(Side.LEFT)
                    .setBorderWidth(1)
                    .setAnimationStyle(AnimationStyle.ELASTIC)
                    .setAnimationSpeed(0.2f)
                    .setShowGlow(false)
                    .setShowShine(true);
            }
            armorBar.setProgress(currentArmor, maxArmor);
        } else {
            armorBar = null;
        }
    }

    private void renderBars(int rightBarX, int barY) {
        if (armorBar != null) {
            armorBar.render();
        }

        healthBar.render();
        foodBar.render();

        renderFoodPreview(rightBarX, barY);
        renderAir(rightBarX, barY);
    }

    private void renderFoodPreview(int rightBarX, int barY) {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (heldItem != null && heldItem.getItem() instanceof ItemFood) {
            ItemFood food = (ItemFood) heldItem.getItem();
            int foodHealAmount = food.func_150905_g(heldItem);
            float currentFood = mc.thePlayer.getFoodStats()
                .getFoodLevel();
            float futureFood = Math.min(currentFood + foodHealAmount, 20F);

            long time = System.currentTimeMillis();
            if (time % 1000 < 500) {
                float alpha = (float) (Math.sin(time * 0.01) * 0.3 + 0.7);
                int color = (int) (alpha * 255) << 24 | (OVERLAY_COLOR & 0x00FFFFFF);

                new ProgressBarAPI(rightBarX, barY, BAR_WIDTH, BAR_HEIGHT).setProgress(futureFood, 20F)
                    .setFillColor(color)
                    .setShowBackground(false)
                    .setShowBorder(false)
                    .setShowGradient(false)
                    .setShowShine(false)
                    .setAnimationStyle(AnimationStyle.NONE)
                    .setTextSide(Side.NONE)
                    .setNumberFormat(NumberFormat.NONE)
                    .render();
            }
        }
    }

    private void renderAir(int rightBarX, int barY) {
        if (mc.thePlayer.getAir() < 300) {
            float currentAir = mc.thePlayer.getAir();
            float maxAir = 300F;

            if (airBar == null) {
                airBar = new ProgressBarAPI(rightBarX, barY - BAR_HEIGHT - GAP, BAR_WIDTH, BAR_HEIGHT)
                    .setFillColor(AIR_FILL_COLOR)
                    .setBackgroundColor(AIR_BG_COLOR)
                    .setTextColor(AIR_TEXT_COLOR)
                    .setTextSide(Side.RIGHT)
                    .setNumberFormat(NumberFormat.FRACTION)
                    .setIcon(BUBBLE_ICON, ICON_SIZE, ICON_SIZE, 0, 0, 16, 16)
                    .setIconSide(Side.RIGHT)
                    .setBorderWidth(1)
                    .setAnimationStyle(AnimationStyle.SMOOTH)
                    .setAnimationSpeed(0.1f)
                    .setShowGlow(false)
                    .setShowShine(true);
            }

            airBar.setProgress(currentAir, maxAir);
            airBar.render();
        } else {
            airBar = null;
        }
    }
}
