package InfoHUD.LightOverlay;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class LightLevelOverlayRenderer {

    public static boolean enabled = false;

    private static final int RADIUS = 8;
    private static final int HEIGHT = 12;

    public static void render(float partialTicks) {
        if (!enabled) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        World world = mc.theWorld;
        if (player == null || world == null) return;

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

        int bx = (int) Math.floor(player.posX);
        int by = (int) Math.floor(player.posY);
        int bz = (int) Math.floor(player.posZ);

        for (int x = bx - RADIUS; x <= bx + RADIUS; x++) {
            for (int z = bz - RADIUS; z <= bz + RADIUS; z++) {
                for (int y = by - HEIGHT; y <= by + HEIGHT; y++) {

                    if (y < 0 || y > 255) continue;

                    Block block = world.getBlock(x, y, z);
                    Block blockAbove = world.getBlock(x, y + 1, z);

                    if (block == null || !block.getMaterial().isSolid()) continue;
                    if (blockAbove == null || blockAbove.getMaterial().isSolid()) continue;

                    int blockLight = world.getSavedLightValue(EnumSkyBlock.Block, x, y + 1, z);

                    String text = String.valueOf(blockLight);
                    int color = getColor(blockLight);

                    GL11.glPushMatrix();
                    GL11.glTranslated(x + 0.5, y + 1.005, z + 0.5);

                    GL11.glRotatef(90F, 1F, 0F, 0F);
                    GL11.glRotatef(180F, 0F, 0F, 1F);

                    GL11.glScalef(-0.08F, -0.08F, 0.08F);

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

    private static int getColor(int light) {
        if (light <= 7) return 0xFFFF0000;
        if (light <= 11) return 0xFFFFFF00;
        return 0xFF00FF00;
    }
}
