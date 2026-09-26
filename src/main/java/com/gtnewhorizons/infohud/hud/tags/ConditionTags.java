package com.gtnewhorizons.infohud.hud.tags;

import static com.gtnewhorizons.infohud.hud.tags.TagRegistry.registerCondition;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

final class ConditionTags {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private ConditionTags() {}

    static void registerTags() {
        String cat = "conditions";
        TagRegistry.register(cat, "disable", () -> null);

        registerCondition(cat, "is_day", () -> worldTime() < 13000);
        registerCondition(cat, "is_night", () -> worldTime() >= 13000);
        registerCondition(cat, "is_full_moon", () -> world().getMoonPhase() == 0);
        registerCondition(cat, "is_raining", () -> world().isRaining());
        registerCondition(cat, "is_thundering", () -> world().isThundering());
        registerCondition(cat, "can_see_sky", ConditionTags::canSeeSky);
        registerCondition(cat, "is_underground", () -> !canSeeSky() && feetY() < 60);

        registerCondition(cat, "in_overworld", () -> world().provider.dimensionId == 0);
        registerCondition(cat, "in_nether", () -> world().provider.dimensionId == -1);
        registerCondition(cat, "in_end", () -> world().provider.dimensionId == 1);
        registerCondition(cat, "slime_chunk", VanillaTags::isSlimeChunk);

        registerCondition(cat, "is_singleplayer", mc::isSingleplayer);
        registerCondition(cat, "is_multiplayer", () -> !mc.isSingleplayer());

        registerCondition(cat, "in_water", () -> player().isInWater());
        registerCondition(cat, "in_lava", () -> player().handleLavaMovement());
        registerCondition(cat, "is_burning", () -> player().isBurning());
        registerCondition(cat, "is_sneaking", () -> player().isSneaking());
        registerCondition(cat, "is_sprinting", () -> player().isSprinting());
        registerCondition(cat, "is_flying", () -> player().capabilities.isFlying);
        registerCondition(cat, "on_ground", () -> player().onGround);
        registerCondition(cat, "is_riding", () -> player().isRiding());
        registerCondition(cat, "is_creative", () -> player().capabilities.isCreativeMode);

        registerCondition(cat, "is_hurt", () -> player().getHealth() < player().getMaxHealth());
        registerCondition(cat, "low_health", () -> player().getHealth() <= 6);
        registerCondition(
            cat,
            "is_hungry",
            () -> player().getFoodStats()
                .getFoodLevel() < 20);
        registerCondition(
            cat,
            "has_effects",
            () -> !player().getActivePotionEffects()
                .isEmpty());
        registerCondition(cat, "holding_item", () -> player().getHeldItem() != null);
        registerCondition(cat, "looking_at_block", () -> hit(MovingObjectPosition.MovingObjectType.BLOCK));
        registerCondition(cat, "looking_at_entity", () -> hit(MovingObjectPosition.MovingObjectType.ENTITY));
    }

    private static EntityClientPlayerMP player() {
        return mc.thePlayer;
    }

    private static World world() {
        return player().worldObj;
    }

    private static long worldTime() {
        return world().getWorldTime() % 24000L;
    }

    private static int feetY() {
        return MathHelper.floor_double(player().boundingBox.minY);
    }

    private static boolean canSeeSky() {
        return world().canBlockSeeTheSky(
            MathHelper.floor_double(player().posX),
            feetY() + 1,
            MathHelper.floor_double(player().posZ));
    }

    private static boolean hit(MovingObjectPosition.MovingObjectType type) {
        MovingObjectPosition target = mc.objectMouseOver;
        return target != null && target.typeOfHit == type;
    }
}
