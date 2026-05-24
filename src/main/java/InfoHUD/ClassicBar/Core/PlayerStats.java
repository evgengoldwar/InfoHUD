package InfoHUD.ClassicBar.Core;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;

public class PlayerStats {

    private float currentHealth;
    private float maxHealth;
    private int armor;
    private int air;
    private int food;
    private float saturation;
    private ItemStack heldItem;

    public void updateStats(EntityClientPlayerMP playerMP) {
        currentHealth = playerMP.getHealth();
        maxHealth = playerMP.getMaxHealth();
        armor = playerMP.getTotalArmorValue();
        air = playerMP.getAir();
        food = playerMP.getFoodStats()
            .getFoodLevel();
        saturation = playerMP.getFoodStats()
            .getSaturationLevel();
        heldItem = playerMP.getHeldItem();
    }

    public float getCurrentHealth() {
        return currentHealth;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getSaturation() {
        return saturation;
    }

    public int getAir() {
        return air;
    }

    public int getArmor() {
        return armor;
    }

    public int getFood() {
        return food;
    }

    public ItemStack getHeldItem() {
        return heldItem;
    }
}
