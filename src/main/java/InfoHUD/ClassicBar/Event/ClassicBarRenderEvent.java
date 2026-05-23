package InfoHUD.ClassicBar.Event;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import InfoHUD.ClassicBar.Renderer.ProgressBarBuilder;
import InfoHUD.ClassicBar.Renderer.ProgressBarBuilder.AnimationStyle;
import InfoHUD.ClassicBar.Renderer.ProgressBarBuilder.NumberFormat;
import InfoHUD.ClassicBar.Renderer.ProgressBarBuilder.Side;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClassicBarRenderEvent {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final ResourceLocation ICON_ARMOR = new ResourceLocation("infohud", "textures/gui/armor.png");
    private static final ResourceLocation ICON_HEART = new ResourceLocation("infohud", "textures/gui/heart.png");
    private static final ResourceLocation ICON_FOOD = new ResourceLocation("infohud", "textures/gui/food.png");
    private static final ResourceLocation ICON_BUBBLE = new ResourceLocation("infohud", "textures/gui/bubble.png");

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        int width = event.resolution.getScaledWidth();
        int height = event.resolution.getScaledHeight();

        int barX = width / 2 - 120;
        int barY = height - 32 - 10;
        int barWidth = 81;
        int barHeight = 10;
        int iconSize = 9;

        float currentHP = mc.thePlayer.getHealth();
        float maxHP = mc.thePlayer.getMaxHealth();
        float currentArmor = mc.thePlayer.getTotalArmorValue();
        float maxArmor = 20F;
        float currentFood = mc.thePlayer.getFoodStats().getFoodLevel();
        float maxFood = 20F;

        if (currentArmor > 0F) {
            new ProgressBarBuilder(barX, barY - barHeight - 2, barWidth, barHeight)
                .setProgress(currentArmor, maxArmor)
                .setFillColor(0xFF9999AA)
                .setBackgroundColor(0xFF252533)
                .setTextColor(0xFFCCCCDD)
                .setIcon(ICON_ARMOR, iconSize, iconSize, 0, 0, 16, 16)
                .setIconSide(Side.LEFT)
                .setTextSide(Side.LEFT)
                .setNumberFormat(NumberFormat.CURRENT)
                .setBorderWidth(1)
                .setShowGradient(true)
                .setAnimationStyle(AnimationStyle.NONE)
                .render();
        }

        new ProgressBarBuilder(barX, barY, barWidth, barHeight)
            .setProgress(currentHP, maxHP)
            .setFillColor(0xFFFF5555)
            .setBackgroundColor(0xFF222222)
            .setTextColor(0xFFFF5555)
            .setIcon(ICON_HEART, iconSize, iconSize, 0, 0, 16, 16)
            .setIconSide(Side.LEFT)
            .setTextSide(Side.LEFT)
            .setNumberFormat(NumberFormat.CURRENT)
            .setBorderWidth(1)
            .setShowGradient(true)
            .setAnimationStyle(AnimationStyle.BOUNCE)
            .setAnimationSpeed(0.15f)
            .render();

        int rightBarX = width / 2 + 10;

        new ProgressBarBuilder(rightBarX, barY, barWidth, barHeight)
            .setProgress(currentFood, maxFood)
            .setFillColor(0xFFFFAA00)
            .setBackgroundColor(0xFF222222)
            .setTextColor(0xFFFFAA00)
            .setIcon(ICON_FOOD, iconSize, iconSize, 0, 0, 16, 16)
            .setIconSide(Side.RIGHT)
            .setTextSide(Side.RIGHT)
            .setNumberFormat(NumberFormat.CURRENT)
            .setBorderWidth(1)
            .setShowGradient(true)
            .setAnimationStyle(AnimationStyle.SMOOTH)
            .setAnimationSpeed(0.12f)
            .render();

        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (heldItem != null && heldItem.getItem() instanceof ItemFood) {
            ItemFood food = (ItemFood) heldItem.getItem();
            int foodHealAmount = food.func_150905_g(heldItem);
            float futureFood = Math.min(currentFood + foodHealAmount, maxFood);

            long time = System.currentTimeMillis() % 1000;
            if (time < 500) {
                new ProgressBarBuilder(rightBarX, barY, barWidth, barHeight)
                    .setProgress(futureFood, maxFood)
                    .setFillColor(0x88FFFF44)
                    .setShowBackground(false)
                    .setShowBorder(false)
                    .setShowGradient(false)
                    .setAnimationStyle(AnimationStyle.NONE)
                    .setTextSide(Side.NONE)
                    .setNumberFormat(NumberFormat.NONE)
                    .render();
            }
        }

        if (mc.thePlayer.getAir() < 300) {
            float currentAir = mc.thePlayer.getAir();
            float maxAir = 300F;

            new ProgressBarBuilder(rightBarX, barY - barHeight - 2, barWidth, barHeight)
                .setProgress(currentAir, maxAir)
                .setFillColor(0xFF4488FF)
                .setBackgroundColor(0xFF222222)
                .setTextColor(0xFF88CCFF)
                .setIcon(ICON_BUBBLE, iconSize, iconSize, 0, 0, 16, 16)
                .setIconSide(Side.RIGHT)
                .setTextSide(Side.RIGHT)
                .setNumberFormat(NumberFormat.FRACTION)
                .setBorderWidth(1)
                .setShowGradient(true)
                .setAnimationStyle(AnimationStyle.SMOOTH)
                .setAnimationSpeed(0.1f)
                .render();
        }
    }
}
