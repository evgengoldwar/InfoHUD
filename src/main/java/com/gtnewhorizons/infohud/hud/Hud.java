package com.gtnewhorizons.infohud.hud;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.MinecraftForge;

import com.gtnewhorizons.infohud.configs.HudConfig;
import com.gtnewhorizons.infohud.hud.core.InfoLine;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoBiome;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoBloodMagic;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoCountItem;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoDimension;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoDirection;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoFPS;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoForestry;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoJump;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoMemory;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoNutrient;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoOreChunk;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoPing;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoPosition;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoSessionTime;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoSlimeChunk;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoTPS;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoThaumcraft;
import com.gtnewhorizons.infohud.hud.core.infolines.InfoWorldTime;
import com.gtnewhorizons.infohud.hud.event.BloodMagicEvent;
import com.gtnewhorizons.infohud.hud.event.JoinWorldEvent;
import com.gtnewhorizons.infohud.hud.event.TickListener;
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
        lines.add(new InfoJump(HudConfig.hudOrder.JumpOrder));

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

        if (Loader.isModLoaded(HudUtils.FORESTRY_ID)) {
            lines.add(new InfoForestry(HudConfig.hudOrder.ForestryOrder));
        }

        if (Loader.isModLoaded(HudUtils.NUTRIENT_ID)) {
            lines.add(new InfoNutrient(HudConfig.hudOrder.NutrientOrder));
        }
    }
}
