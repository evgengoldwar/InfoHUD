package InfoHUD.Hud;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.MinecraftForge;

import InfoHUD.Configs.HudConfig;
import InfoHUD.Hud.Core.InfoLine;
import InfoHUD.Hud.Core.InfoLines.InfoBiome;
import InfoHUD.Hud.Core.InfoLines.InfoBloodMagic;
import InfoHUD.Hud.Core.InfoLines.InfoCountItem;
import InfoHUD.Hud.Core.InfoLines.InfoDimension;
import InfoHUD.Hud.Core.InfoLines.InfoDirection;
import InfoHUD.Hud.Core.InfoLines.InfoFPS;
import InfoHUD.Hud.Core.InfoLines.InfoJump;
import InfoHUD.Hud.Core.InfoLines.InfoMemory;
import InfoHUD.Hud.Core.InfoLines.InfoOreChunk;
import InfoHUD.Hud.Core.InfoLines.InfoPing;
import InfoHUD.Hud.Core.InfoLines.InfoPosition;
import InfoHUD.Hud.Core.InfoLines.InfoSessionTime;
import InfoHUD.Hud.Core.InfoLines.InfoSlimeChunk;
import InfoHUD.Hud.Core.InfoLines.InfoTPS;
import InfoHUD.Hud.Core.InfoLines.InfoThaumcraft;
import InfoHUD.Hud.Core.InfoLines.InfoWorldTime;
import InfoHUD.Hud.Event.BloodMagicEvent;
import InfoHUD.Hud.Event.JoinWorldEvent;
import InfoHUD.Hud.Event.TickListener;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;

public class Hud {

    public static List<InfoLine> lines = new ArrayList<>();

    public static void initEvent() {
        FMLCommonHandler.instance()
            .bus()
            .register(new TickListener());
        JoinWorldEvent joinWorldEvent = new JoinWorldEvent();
        MinecraftForge.EVENT_BUS.register(joinWorldEvent);
        FMLCommonHandler.instance()
            .bus()
            .register(joinWorldEvent);

        if (Loader.isModLoaded(HudUtils.BLOOD_MAGIC_ID)) {
            MinecraftForge.EVENT_BUS.register(new BloodMagicEvent());
        }
    }

    public static void initLines() {
        lines.clear();

        lines.add(new InfoMemory(HudConfig.hudOrder.MemoryOrder, false));
        lines.add(new InfoMemory(HudConfig.hudOrder.ServerMemoryOrder, true));
        lines.add(new InfoPing(HudConfig.hudOrder.PingOrder));
        lines.add(new InfoTPS(HudConfig.hudOrder.TpsOrder));
        lines.add(new InfoPosition(HudConfig.hudOrder.PositionOrder));
        lines.add(new InfoFPS(HudConfig.hudOrder.FpsOrder));
        lines.add(new InfoDimension(HudConfig.hudOrder.DimensionOrder));
        lines.add(new InfoBiome(HudConfig.hudOrder.BiomeOrder));
        lines.add(new InfoDirection(HudConfig.hudOrder.DirectionOrder));
        lines.add(new InfoSlimeChunk(HudConfig.hudOrder.SlimeChunkOrder));
        lines.add(new InfoWorldTime(HudConfig.hudOrder.PlayTimeOrder, false));
        lines.add(new InfoWorldTime(HudConfig.hudOrder.WorldTimeOrder, true));
        lines.add(new InfoSessionTime(HudConfig.hudOrder.SessionTimeOrder));
        lines.add(new InfoJump(20));

        lines.add(new InfoCountItem(0));

        if (Loader.isModLoaded(HudUtils.BLOOD_MAGIC_ID)) {
            lines.add(new InfoBloodMagic(HudConfig.hudOrder.LpOrder));
        }

        if (Loader.isModLoaded(HudUtils.THAUMCRAFT_ID)) {
            lines.add(new InfoThaumcraft(HudConfig.hudOrder.WarpOrder));
        }

        if (Loader.isModLoaded(HudUtils.GREG_TECH_ID)) {
            lines.add(new InfoOreChunk(HudConfig.hudOrder.OreChunkOrder));
        }
    }
}
