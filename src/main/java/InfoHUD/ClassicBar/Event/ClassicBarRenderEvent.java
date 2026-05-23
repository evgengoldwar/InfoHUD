package InfoHUD.ClassicBar.Event;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import InfoHUD.ClassicBar.Enum.IconPosition;
import InfoHUD.ClassicBar.Enum.TextPosition;
import InfoHUD.ClassicBar.Renderer.ProgressBar;
import InfoHUD.InfoHUD;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClassicBarRenderEvent {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final ResourceLocation ICON_HEART = getIcon("heart");

    private static ResourceLocation getIcon(String name) {
        return new ResourceLocation("infohud", "textures/gui/" + name + ".png");
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        int width = event.resolution.getScaledWidth();
        int height = event.resolution.getScaledHeight();

        int barX = width / 2 - 91;
        int barY = height - 32 - 10;
        int barWidth = 81;
        int barHeight = 10;

        float currentHP = mc.thePlayer.getHealth();
        float maxHP = mc.thePlayer.getMaxHealth();

        float currentArmor = mc.thePlayer.getTotalArmorValue();
        float maxArmor = 20F;

        float currentFood = mc.thePlayer.getFoodStats()
            .getFoodLevel();
        float maxFood = 20F;

        if (currentArmor > 0F) {
            ProgressBar.drawBar(
                barX,
                barY - barHeight - 2,
                barWidth,
                barHeight,
                currentArmor,
                maxArmor,
                false,
                0xFFAAAAAA,
                0xFF333333,
                null,
                8,
                IconPosition.LEFT,
                TextPosition.LEFT);
        }

        ProgressBar.drawBar(
            barX,
            barY,
            barWidth,
            barHeight,
            currentHP,
            maxHP,
            true,
            0xFFFF0000,
            0xFF333333,
            null,
            16,
            IconPosition.LEFT,
            TextPosition.LEFT);

        int rightBarX = width / 2 + 10;

        ProgressBar.drawBar(
            rightBarX,
            barY,
            barWidth,
            barHeight,
            currentFood,
            maxFood,
            true,
            0xFFCC8800,
            0xFF333333,
            null,
            16,
            IconPosition.RIGHT,
            TextPosition.RIGHT);

        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (heldItem != null && heldItem.getItem() instanceof ItemFood) {
            ItemFood food = (ItemFood) heldItem.getItem();
            int foodHealAmount = food.func_150905_g(heldItem);
            float futureFood = Math.min(currentFood + foodHealAmount, maxFood);

            long time = System.currentTimeMillis() % 1000;
            if (time < 500) {
                ProgressBar
                    .drawOverlay(rightBarX, barY, barWidth, barHeight, currentFood, futureFood, maxFood, 0xFFFFFF00);
            }
        }

        if (mc.thePlayer.getAir() < 300) {
            float currentAir = mc.thePlayer.getAir();
            float maxAir = 300F;

            ProgressBar.drawBar(
                rightBarX,
                barY - barHeight - 2,
                barWidth,
                barHeight,
                currentAir,
                maxAir,
                true,
                0xFF4488FF,
                0xFF333333,
                null,
                16,
                IconPosition.RIGHT,
                TextPosition.RIGHT);
        }
    }
}
