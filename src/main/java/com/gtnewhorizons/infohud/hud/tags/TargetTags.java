package com.gtnewhorizons.infohud.hud.tags;

import static com.gtnewhorizons.infohud.hud.tags.TagRegistry.register;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

final class TargetTags {

    private TargetTags() {}

    static void registerTags() {
        String cat = "target";
        register(cat, "look_block", TargetTags::blockName);
        register(cat, "look_x", () -> block() == null ? null : String.valueOf(block().blockX));
        register(cat, "look_y", () -> block() == null ? null : String.valueOf(block().blockY));
        register(cat, "look_z", () -> block() == null ? null : String.valueOf(block().blockZ));
        register(cat, "look_entity", () -> entity() == null ? null : entity().getCommandSenderName());
        register(cat, "look_entity_hp", () -> {
            Entity entity = entity();
            if (!(entity instanceof EntityLivingBase)) return null;
            return String.format("%.0f", ((EntityLivingBase) entity).getHealth());
        });
    }

    private static MovingObjectPosition block() {
        MovingObjectPosition target = Minecraft.getMinecraft().objectMouseOver;
        if (target == null || target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return null;
        return target;
    }

    private static Entity entity() {
        MovingObjectPosition target = Minecraft.getMinecraft().objectMouseOver;
        if (target == null || target.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) return null;
        return target.entityHit;
    }

    private static String blockName() {
        MovingObjectPosition target = block();
        if (target == null) return null;

        World world = Minecraft.getMinecraft().theWorld;
        Block block = world.getBlock(target.blockX, target.blockY, target.blockZ);

        try {
            Item item = Item.getItemFromBlock(block);
            if (item != null) {
                int meta = block.getDamageValue(world, target.blockX, target.blockY, target.blockZ);
                String name = new ItemStack(item, 1, meta).getDisplayName();
                if (name != null && !name.isEmpty()) return name;
            }
        } catch (Exception ignored) {}

        return block.getLocalizedName();
    }
}
