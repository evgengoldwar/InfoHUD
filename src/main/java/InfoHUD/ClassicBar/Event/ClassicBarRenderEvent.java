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
    private static final int BAR_WIDTH = 81, BAR_HEIGHT = 10, ICON_SIZE = 9, GAP = 5;

    private static final ResourceLocation ICON_ARMOR = new ResourceLocation("infohud", "textures/gui/armor.png");
    private static final ResourceLocation ICON_HEART = new ResourceLocation("infohud", "textures/gui/heart.png");
    private static final ResourceLocation ICON_FOOD = new ResourceLocation("infohud", "textures/gui/food.png");
    private static final ResourceLocation ICON_BUBBLE = new ResourceLocation("infohud", "textures/gui/bubble.png");

    private ProgressBarBuilder healthBar, armorBar, foodBar, airBar, foodPreviewBar;
    private int lastWidth, lastHeight;

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        int width = event.resolution.getScaledWidth();
        int height = event.resolution.getScaledHeight();

        if (healthBar == null || width != lastWidth || height != lastHeight) {
            lastWidth = width;
            lastHeight = height;
            createAllBars();
        }

        updateAllProgress();
        renderAllBars();
    }

    private void createAllBars() {
        int leftX = lastWidth / 2 - 118;
        int rightX = lastWidth / 2 + 10;
        int barY = lastHeight - 32 - 10;

        armorBar = new ProgressBarBuilder(leftX, barY - BAR_HEIGHT - GAP, BAR_WIDTH, BAR_HEIGHT)
            .setFillColor(0xFF9999AA)
            .setBackgroundColor(0xFF252533)
            .setTextColor(0xFFCCCCDD)
            .setIcon(ICON_ARMOR, ICON_SIZE, ICON_SIZE, 0, 0, 16, 16)
            .setIconSide(Side.LEFT)
            .setTextSide(Side.LEFT)
            .setNumberFormat(NumberFormat.CURRENT)
            .setBorderWidth(1)
            .setShowGradient(true)
            .setAnimationStyle(AnimationStyle.ELASTIC);

        healthBar = new ProgressBarBuilder(leftX, barY, BAR_WIDTH, BAR_HEIGHT).setFillColor(0xFFFF5555)
            .setBackgroundColor(0xFF222222)
            .setTextColor(0xFFFF5555)
            .setIcon(ICON_HEART, ICON_SIZE, ICON_SIZE, 0, 0, 16, 16)
            .setIconSide(Side.LEFT)
            .setTextSide(Side.LEFT)
            .setNumberFormat(NumberFormat.CURRENT)
            .setBorderWidth(1)
            .setShowGradient(true)
            .setAnimationStyle(AnimationStyle.BOUNCE)
            .setAnimationSpeed(0.15f);

        foodBar = new ProgressBarBuilder(rightX, barY, BAR_WIDTH, BAR_HEIGHT).setFillColor(0xFFFFAA00)
            .setBackgroundColor(0xFF222222)
            .setTextColor(0xFFFFAA00)
            .setIcon(ICON_FOOD, ICON_SIZE, ICON_SIZE, 0, 0, 16, 16)
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
            .setIcon(ICON_BUBBLE, ICON_SIZE, ICON_SIZE, 0, 0, 16, 16)
            .setIconSide(Side.RIGHT)
            .setTextSide(Side.RIGHT)
            .setNumberFormat(NumberFormat.FRACTION)
            .setBorderWidth(1)
            .setShowGradient(true)
            .setEnableDamageFlash(false);

        foodPreviewBar = new ProgressBarBuilder(rightX, barY, BAR_WIDTH, BAR_HEIGHT).setFillColor(0x88FFFF44)
            .setShowBackground(false)
            .setShowBorder(false)
            .setShowGradient(false)
            .setAnimationStyle(AnimationStyle.NONE)
            .setTextSide(Side.NONE)
            .setNumberFormat(NumberFormat.NONE);
    }

    private void updateAllProgress() {
        healthBar.setProgress(mc.thePlayer.getHealth(), mc.thePlayer.getMaxHealth());
        foodBar.setProgress(
            mc.thePlayer.getFoodStats()
                .getFoodLevel(),
            20F);

        float armor = mc.thePlayer.getTotalArmorValue();
        if (armor > 0) armorBar.setProgress(armor, 20F);

        if (mc.thePlayer.getAir() < 300) airBar.setProgress(mc.thePlayer.getAir(), 300F);

        ItemStack held = mc.thePlayer.getHeldItem();
        if (held != null && held.getItem() instanceof ItemFood) {
            ItemFood food = (ItemFood) held.getItem();
            float currentFood = mc.thePlayer.getFoodStats()
                .getFoodLevel();
            float futureFood = Math.min(currentFood + food.func_150905_g(held), 20F);
            foodPreviewBar.setMinProgress(currentFood / 20F);
            foodPreviewBar.setProgress(futureFood, 20F);
        }
    }

    private void renderAllBars() {
        float armor = mc.thePlayer.getTotalArmorValue();
        if (armor > 0) armorBar.render();
        healthBar.render();
        foodBar.render();

        ItemStack held = mc.thePlayer.getHeldItem();
        if (held != null && held.getItem() instanceof ItemFood && System.currentTimeMillis() % 1000 < 500) {
            foodPreviewBar.render();
        }

        if (mc.thePlayer.getAir() < 300) airBar.render();
    }
}
