package InfoHUD.Hud;

import java.util.Random;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import baubles.api.BaublesApi;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.events.EventHandlerRunic;

public class HudUtils {

    private static final Random RANDOM = new Random();
    public static final String BLOOD_MAGIC_ID = "AWWayofTime";
    public static final String THAUMCRAFT_ID = "Thaumcraft";
    public static final String GREG_TECH_ID = "gregtech";
    public static final String FORESTRY_ID = "Forestry";
    public static final String NUTRIENT_ID = "nutrition";

    private static NBTTagCompound getPersistentDataTag(EntityPlayer player) {
        NBTTagCompound forgeData = player.getEntityData()
            .getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        return forgeData.getCompoundTag("BloodMagic");
    }

    public static int getPlayerLPTag(EntityPlayer player) {
        NBTTagCompound data = getPersistentDataTag(player);
        if (data.hasKey("BM:StoredLP")) {
            return data.getInteger("BM:StoredLP");
        }

        return 0;
    }

    public static int getPlayerMaxLPTag(EntityPlayer player) {
        NBTTagCompound data = getPersistentDataTag(player);
        if (data.hasKey("BM:MaxStoredLP")) {
            return data.getInteger("BM:MaxStoredLP");
        }

        return 0;
    }

    public static int getWarpPerm(EntityPlayer player) {
        return Thaumcraft.proxy.getPlayerKnowledge()
            .getWarpPerm(player.getCommandSenderName());
    }

    public static int getWarpSticky(EntityPlayer player) {
        return Thaumcraft.proxy.getPlayerKnowledge()
            .getWarpSticky(player.getCommandSenderName());
    }

    public static int getWarpTemp(EntityPlayer player) {
        return Thaumcraft.proxy.getPlayerKnowledge()
            .getWarpTemp(player.getCommandSenderName());
    }

    public static int getWarpTotal(EntityPlayer player) {
        return Thaumcraft.proxy.getPlayerKnowledge()
            .getWarpTotal(player.getCommandSenderName()) + getWarpFromGear(player);
    }

    private static int getWarpFromGear(EntityPlayer player) {
        int w = EventHandlerRunic.getFinalWarp(player.getCurrentEquippedItem(), player);

        for (int a = 0; a < 4; ++a) {
            w += EventHandlerRunic.getFinalWarp(player.inventory.armorItemInSlot(a), player);
        }

        IInventory baubles = BaublesApi.getBaubles(player);

        for (int a = 0; a < 4; ++a) {
            w += EventHandlerRunic.getFinalWarp(baubles.getStackInSlot(a), player);
        }

        return w;
    }

    public static boolean isSlimeChunk(long seed, EntityClientPlayerMP playerMP, World world) {
        int x = MathHelper.floor_double(playerMP.posX) >> 4;
        int z = MathHelper.floor_double(playerMP.posZ) >> 4;
        // noinspection IntegerMultiplicationImplicitCastToLong - we want to do the casts as integers and *then* cast
        RANDOM.setSeed(
            seed + (long) (x * x * 0x4c1906) + (long) (x * 0x5ac0db) + (long) (z * z) * 0x4307a7L + (long) (z * 0x5f24f)
                ^ 0x3ad8025fL);
        return RANDOM.nextInt(10) == 0 || world.getBiomeGenForCoords(
            MathHelper.floor_double(playerMP.posX),
            MathHelper.floor_double(playerMP.posZ)).biomeID == BiomeGenBase.swampland.biomeID;
    }
}
