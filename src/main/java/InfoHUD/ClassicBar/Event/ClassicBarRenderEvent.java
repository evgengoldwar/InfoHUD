package InfoHUD.ClassicBar.Event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import InfoHUD.ClassicBar.Core.PlayerStats;
import InfoHUD.ClassicBar.Renderer.ProgressBarBuilder;
import InfoHUD.ClassicBar.Renderer.ProgressBarBuilder.AnimationStyle;
import InfoHUD.ClassicBar.Renderer.ProgressBarBuilder.NumberFormat;
import InfoHUD.ClassicBar.Renderer.ProgressBarBuilder.Side;
import InfoHUD.Configs.ClassicBarConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClassicBarRenderEvent {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final int BAR_WIDTH = 81;
    private static final int BAR_HEIGHT = 10;
    private static final int ICON_SIZE = 9;
    private static final int GAP = 5;
    private static final int HEALTH_SEGMENT_SIZE = 20;
    private static final ResourceLocation VANILLA_ICONS = new ResourceLocation("textures/gui/icons.png");
    private static final int ICON_W = 8;
    private static final int ICON_H = 8;

    private ProgressBarBuilder healthBar;
    private ProgressBarBuilder armorBar;
    private ProgressBarBuilder foodBar;
    private ProgressBarBuilder airBar;
    private ProgressBarBuilder foodPreviewBar;
    private ProgressBarBuilder saturationBar;
    private ProgressBarBuilder saturationPreviewBar;
    private int lastWidth;
    private int lastHeight;
    private boolean foodPreviewVisible;
    private final PlayerStats playerStats = new PlayerStats();

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (!ClassicBarConfig.ClassicBarEnable) return;

        EntityClientPlayerMP playerMP = mc.thePlayer;
        World world = mc.theWorld;

        if (playerMP == null || world == null) return;

        if (playerMP.capabilities.isCreativeMode) return;

        int width = event.resolution.getScaledWidth();
        int height = event.resolution.getScaledHeight();
        playerStats.updateStats(playerMP);

        if (healthBar == null || width != lastWidth || height != lastHeight) {
            lastWidth = width;
            lastHeight = height;
            createAllBars();
        }

        updateAllProgress();
        renderAllBars();
    }

    private void createAllBars() {
        int leftX = lastWidth / 2 - 91;
        int rightX = lastWidth / 2 + 10;
        int barY = lastHeight - 32 - 10;

        armorBar = new ProgressBarBuilder(leftX, barY - BAR_HEIGHT - GAP, BAR_WIDTH, BAR_HEIGHT)
            .setFillColor(0xFF9999AA)
            .setBackgroundColor(0xFF252533)
            .setTextColor(0xFFCCCCDD)
            .setIcon(VANILLA_ICONS, ICON_SIZE, ICON_SIZE, 34, 9, 34 + ICON_W, 9 + ICON_H)
            .setIconSide(Side.LEFT)
            .setTextSide(Side.LEFT)
            .setNumberFormat(NumberFormat.CURRENT)
            .setBorderWidth(1)
            .setShowGradient(true)
            .setAnimationStyle(AnimationStyle.ELASTIC);

        healthBar = new ProgressBarBuilder(leftX, barY, BAR_WIDTH, BAR_HEIGHT).setFillColor(0xFFFF5555)
            .setBackgroundColor(0xFF222222)
            .setTextColor(0xFFFF5555)
            .setIcon(VANILLA_ICONS, ICON_SIZE, ICON_SIZE, 52, 0, 52 + ICON_W, ICON_H)
            .setIconSide(Side.LEFT)
            .setTextSide(Side.LEFT)
            .setNumberFormat(NumberFormat.FRACTION)
            .setBorderWidth(1)
            .setShowGradient(true)
            .setAnimationStyle(AnimationStyle.BOUNCE)
            .setAnimationSpeed(0.15f);

        foodBar = new ProgressBarBuilder(rightX, barY, BAR_WIDTH, BAR_HEIGHT).setFillColor(0xFFFFAA00)
            .setBackgroundColor(0xFF222222)
            .setTextColor(0xFFFFAA00)
            .setIcon(VANILLA_ICONS, ICON_SIZE, ICON_SIZE, 52, 27, 52 + ICON_W, 27 + ICON_H)
            .setIconSide(Side.RIGHT)
            .setTextSide(Side.RIGHT)
            .setNumberFormat(NumberFormat.CURRENT)
            .setBorderWidth(1)
            .setShowGradient(true)
            .setAnimationStyle(AnimationStyle.SMOOTH)
            .setAnimationSpeed(0.12f);

        airBar = new ProgressBarBuilder(rightX, barY - BAR_HEIGHT - GAP, BAR_WIDTH, BAR_HEIGHT).setFillColor(0xFF4488FF)
            .setBackgroundColor(0xFF222222)
            .setTextColor(0xFF88CCFF)
            .setIcon(VANILLA_ICONS, ICON_SIZE, ICON_SIZE, 16, 18, 16 + ICON_W, 18 + ICON_H)
            .setIconSide(Side.RIGHT)
            .setTextSide(Side.RIGHT)
            .setNumberFormat(NumberFormat.FRACTION)
            .setBorderWidth(1)
            .setShowGradient(true)
            .setEnableDamageFlash(false);

        foodPreviewBar = new ProgressBarBuilder(rightX, barY, BAR_WIDTH, BAR_HEIGHT).setFillColor(0xFFFFAA00)
            .setShowBackground(false)
            .setShowBorder(false)
            .setShowGradient(true)
            .setAnimationStyle(AnimationStyle.SMOOTH)
            .setAnimationSpeed(0.1f)
            .setTextSide(Side.NONE)
            .setNumberFormat(NumberFormat.NONE)
            .setFade(true)
            .setAlpha(0f);

        saturationBar = new ProgressBarBuilder(rightX, barY, BAR_WIDTH, BAR_HEIGHT).setFillColor(0xFFFFEE44)
            .setShowBackground(false)
            .setShowBorder(false)
            .setShowGradient(true)
            .setTextSide(Side.NONE)
            .setAnimationStyle(AnimationStyle.SMOOTH)
            .setAnimationSpeed(0.1f)
            .setAlpha(0.3f);

        saturationPreviewBar = new ProgressBarBuilder(rightX, barY, BAR_WIDTH, BAR_HEIGHT).setFillColor(0xFFFFEE44)
            .setShowBackground(false)
            .setShowBorder(false)
            .setShowGradient(true)
            .setAnimationStyle(AnimationStyle.SMOOTH)
            .setAnimationSpeed(0.1f)
            .setTextSide(Side.NONE)
            .setNumberFormat(NumberFormat.NONE)
            .setFade(true)
            .setAlpha(0f);
    }

    private void updateAllProgress() {
        float currentHealth = playerStats.getCurrentHealth();
        float maxHealth = playerStats.getMaxHealth();

        int healthColor = getColorHealth(maxHealth);
        healthBar.setFillColor(healthColor);
        healthBar.setTextColor(healthColor);
        healthBar.setProgress(currentHealth, maxHealth);

        foodBar.setProgress(playerStats.getFood(), 20F);
        saturationBar.setProgress(playerStats.getSaturation(), 20F);

        float armor = playerStats.getArmor();
        if (armor > 0) {
            armorBar.setProgress(armor, 20F);
        }

        int air = playerStats.getAir();
        if (air < 300 && air >= 0) {
            airBar.setProgress(air, 300F);
        }

        ItemStack held = playerStats.getHeldItem();
        boolean shouldShow = held != null && held.getItem() instanceof ItemFood
            && System.currentTimeMillis() % 1000 < 500;

        if (shouldShow) {
            ItemFood food = (ItemFood) held.getItem();

            float currentFood = playerStats.getFood();
            float futureFood = Math.min(currentFood + food.func_150905_g(held), 20F);

            float currentSaturation = playerStats.getSaturation();
            int healAmount = food.func_150905_g(held);
            float saturationModifier = food.func_150906_h(held);

            float rawSaturationGain = healAmount * saturationModifier * 2.0F;
            float futureSaturationRaw = currentSaturation + rawSaturationGain;
            float futureSaturation = Math.min(futureSaturationRaw, futureFood);

            foodPreviewBar.setMinProgress(currentFood / 20F);
            foodPreviewBar.setProgress(futureFood, 20F);

            saturationPreviewBar.setMinProgress(currentSaturation / 20F);
            saturationPreviewBar.setProgress(futureSaturation, 20F);
        }

        if (shouldShow && !foodPreviewVisible) {
            foodPreviewBar.setTargetAlpha(0.5f);
            saturationPreviewBar.setTargetAlpha(0.5f);
            foodPreviewVisible = true;
        } else if (!shouldShow && foodPreviewVisible) {
            foodPreviewBar.setTargetAlpha(0f);
            saturationPreviewBar.setTargetAlpha(0f);
            foodPreviewVisible = false;
        }
    }

    private void renderAllBars() {
        if (playerStats.getArmor() > 0) {
            armorBar.render();
        }

        healthBar.render();
        foodBar.render();
        foodPreviewBar.render();
        saturationBar.render();
        saturationPreviewBar.render();

        if (playerStats.getAir() < 300) {
            airBar.render();
        }
    }

    private int getColorHealth(float maxHealth) {
        if (maxHealth <= 20) {
            return 0xFFFF5555;
        }

        if (maxHealth >= 240 && maxHealth <= 260) {
            return 0xFFFFFFFF;
        }

        int segment = (int) Math.ceil(maxHealth / HEALTH_SEGMENT_SIZE) - 1;

        int cycledSegment = ((segment - 1) % 10) + 1;
        return switch (cycledSegment) {
            case 1 -> 0xFFFF8844;
            case 2 -> 0xFFFFDD44;
            case 3 -> 0xFF44FF44;
            case 4 -> 0xFF44FFFF;
            case 5 -> 0xFFAA44FF;
            case 6 -> 0xFFFF66CC;
            case 7 -> 0xFFCC4444;
            case 8 -> 0xFF44CCCC;
            case 9 -> 0xFF888888;
            case 10 -> 0xFFFFFFFF;
            default -> 0xFFFF5555;
        };
    }
}
