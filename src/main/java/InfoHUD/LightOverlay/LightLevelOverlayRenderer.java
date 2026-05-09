package InfoHUD.LightOverlay;

import static InfoHUD.Utils.Utils.tr;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.lwjgl.opengl.GL11;

import InfoHUD.Configs.LightOverlayConfig;

public class LightLevelOverlayRenderer {

    public static final int MODE_OFF = 0;
    public static final int MODE_ALL = 1;
    public static final int MODE_SPAWNABLE = 2;
    public static final int MODE_SKY = 3;
    public static final int MODE_CAN_SPAWN = 4;

    public static int currentMode = MODE_OFF;

    private static final int RADIUS = 8;
    private static final int HEIGHT = 12;

    private static Entity dummyEntity;
    private static AxisAlignedBB aabb;

    public static void render(float partialTicks) {
        if (currentMode == MODE_OFF) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        World world = mc.theWorld;
        if (player == null || world == null) return;

        if (dummyEntity == null) {
            dummyEntity = new EntityPig(null);
        }
        if (aabb == null) {
            aabb = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
        }

        double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        GL11.glPushMatrix();
        GL11.glTranslated(-px, -py, -pz);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        FontRenderer fr = mc.fontRenderer;

        float snapYaw;
        if (LightOverlayConfig.DisableFollowNumber) {
            snapYaw = 180;
        } else {
            float yaw = mc.renderViewEntity.rotationYaw;
            while (yaw < 0) yaw += 360;
            yaw = yaw % 360;

            if (yaw >= 315 || yaw < 45) snapYaw = 0;
            else if (yaw < 135) snapYaw = 270;
            else if (yaw < 225) snapYaw = 180;
            else snapYaw = 90;
        }

        int bx = (int) Math.floor(player.posX);
        int by = (int) Math.floor(player.posY);
        int bz = (int) Math.floor(player.posZ);

        for (int x = bx - RADIUS; x <= bx + RADIUS; x++) {
            for (int z = bz - RADIUS; z <= bz + RADIUS; z++) {
                for (int y = by - HEIGHT; y <= by + HEIGHT; y++) {

                    if (y < 0 || y > 255) continue;

                    Block block = world.getBlock(x, y, z);
                    Block blockAbove = world.getBlock(x, y + 1, z);

                    if (block == null || !block.getMaterial()
                        .isSolid()) continue;
                    if (blockAbove == null || blockAbove.getMaterial()
                        .isSolid()) continue;

                    String text;
                    int color;

                    if (currentMode == MODE_CAN_SPAWN) {
                        byte spawnMode = getSpawnMode(world, x, y + 1, z);

                        if (spawnMode == 0) continue;

                        text = "✗";
                        color = 0xFFFF0000;
                    } else {
                        int light;
                        if (currentMode == MODE_SKY) {
                            light = world.getSavedLightValue(EnumSkyBlock.Sky, x, y + 1, z);
                        } else {
                            light = world.getSavedLightValue(EnumSkyBlock.Block, x, y + 1, z);
                        }

                        if (currentMode == MODE_SPAWNABLE && light > 7) continue;

                        text = String.valueOf(light);
                        color = getColor(light);
                    }

                    GL11.glPushMatrix();
                    GL11.glTranslated(x + 0.5, y + 1.005, z + 0.5);
                    GL11.glRotatef(snapYaw, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(90F, 1F, 0F, 0F);
                    GL11.glScalef(-0.05F, -0.05F, 0.05F);

                    int w = fr.getStringWidth(text);
                    fr.drawString(text, -w / 2, -fr.FONT_HEIGHT / 2, color);

                    GL11.glPopMatrix();
                }
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    private static byte getSpawnMode(World world, int x, int y, int z) {
        if (y >= world.getHeight()) {
            return 0;
        }

        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        int chunkX = x & 15;
        int chunkZ = z & 15;

        if (chunk.getSavedLightValue(EnumSkyBlock.Block, chunkX, y, chunkZ) >= 8
            || !SpawnerAnimals.canCreatureTypeSpawnAtLocation(EnumCreatureType.monster, world, x, y, z)) {
            return 0;
        }

        aabb.minX = x + 0.2;
        aabb.maxX = x + 0.8;
        aabb.minY = y + 0.01;
        aabb.maxY = y + 1.8;
        aabb.minZ = z + 0.2;
        aabb.maxZ = z + 0.8;

        if (!world.getCollidingBoundingBoxes(dummyEntity, aabb)
            .isEmpty() || world.isAnyLiquid(aabb)) {
            return 0;
        }

        return (byte) (chunk.getSavedLightValue(EnumSkyBlock.Sky, chunkX, y, chunkZ) >= 8 ? 1 : 2);
    }

    public static void toggleMode() {
        currentMode++;
        if (currentMode > MODE_CAN_SPAWN) {
            currentMode = MODE_OFF;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            String modeName = switch (currentMode) {
                case MODE_OFF -> tr("infohud.light_overlay.mode_off");
                case MODE_ALL -> tr("infohud.light_overlay.mode_all");
                case MODE_SPAWNABLE -> tr("infohud.light_overlay.mode_spawnable");
                case MODE_SKY -> tr("infohud.light_overlay.mode_sky");
                case MODE_CAN_SPAWN -> tr("infohud.light_overlay.mode_can_spawn");
                default -> "";
            };
            mc.thePlayer.addChatMessage(new ChatComponentText(tr("infohud.light_overlay.chat") + modeName));
        }
    }

    private static int getColor(int light) {
        if (light <= 7) return 0xFFFF0000;
        if (light <= 11) return 0xFFFFFF00;
        return 0xFF00FF00;
    }
}
