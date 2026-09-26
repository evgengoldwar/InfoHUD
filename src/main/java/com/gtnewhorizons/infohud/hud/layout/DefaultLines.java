package com.gtnewhorizons.infohud.hud.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.util.StatCollector;

import com.gtnewhorizons.infohud.hud.HudUtils;
import com.gtnewhorizons.infohud.hud.tags.LineTemplate;

import cpw.mods.fml.common.Loader;

public final class DefaultLines {

    public static final class Preset {

        public final String id;
        private final String template;
        public final String icon;
        public final String modId;

        Preset(String id, String template, String icon, String modId) {
            this.id = id;
            this.template = template;
            this.icon = icon;
            this.modId = modId;
        }

        public String getTemplate() {
            String key = "infohud.default_line." + id;
            String text = StatCollector.canTranslate(key) ? StatCollector.translateToLocal(key) : template;
            return icon.isEmpty() ? text : LineTemplate.iconTag(icon) + text;
        }

        public String getName() {
            String key = "infohud.default_line_name." + id;
            return StatCollector.canTranslate(key) ? StatCollector.translateToLocal(key) : id;
        }

        public boolean isAvailable() {
            return modId == null || Loader.isModLoaded(modId);
        }

        public HudLine create() {
            return new HudLine(id, getTemplate());
        }
    }

    private static final List<Preset> PRESETS = Collections.unmodifiableList(
        Arrays.asList(
            new Preset("fps", "&6{player} &rFPS: &a{fps}", "minecraft:emerald", null),
            new Preset("ping", "Ping: &a{ping} ms", "minecraft:stone", null),
            new Preset(
                "tps",
                "TPS: {tps_color}{tps} &rMSPT: {mspt_color}{mspt}",
                "minecraft:command_block",
                null),
            new Preset("memory", "RAM: &a{mem_used}MB &r/ &a{mem_max}MB", "minecraft:redstone", null),
            new Preset(
                "server_memory",
                "Server RAM: &a{server_mem_used}MB &r/ &a{server_mem_max}MB",
                "minecraft:repeater",
                null),
            new Preset("position", "Pos: {x} {y} {z}", "minecraft:grass", null),
            new Preset("blood_magic", "LP: &6{lp} / {lp_max}", "AWWayofTime:weakBloodOrb", HudUtils.BLOOD_MAGIC_ID),
            new Preset(
                "warp",
                "Warp: &6{warp_total} &r(P: &c{warp_perm} &r| S: &c{warp_sticky} &r| T: &c{warp_temp}&r)",
                "Thaumcraft:ItemEldritchObject",
                HudUtils.THAUMCRAFT_ID),
            new Preset("dimension", "Dim: &6{dim_name} &a{dim_id}", "minecraft:ender_eye", null),
            new Preset("biome", "Biome: &6{biome} &rHumidity: &a{humidity}%", "minecraft:sapling", null),
            new Preset("direction", "Facing: &6{direction}", "minecraft:compass", null),
            new Preset(
                "ore_chunk",
                "{ore_chunk}This is an &a&nore chunk&r",
                "minecraft:iron_ore",
                HudUtils.GREG_TECH_ID),
            new Preset("slime_chunk", "{slime_chunk}This is a &a&nslime chunk&r", "minecraft:slime_ball", null),
            new Preset(
                "world_time",
                "World time: &6{world_age} &rTime: &6{time}&r{night}",
                "minecraft:clock",
                null),
            new Preset("play_time", "Play time: &6{play_time}", "minecraft:noteblock", null),
            new Preset("session_time", "Session: &6{session_time}", "minecraft:golden_apple/1", null),
            new Preset("jumps", "Jumps: &6{jumps}", "minecraft:slime_ball", null),
            new Preset(
                "forestry",
                "{forestry_temp_name}: &6{forestry_temp}%&r {forestry_humidity_name}: &6{forestry_humidity}%&r",
                "Forestry:beeDroneGE",
                HudUtils.FORESTRY_ID),
            new Preset("nutrients", "{nutrients}", "minecraft:milk_bucket", HudUtils.NUTRIENT_ID)));

    private DefaultLines() {}

    public static List<Preset> available() {
        List<Preset> result = new ArrayList<>();
        for (Preset preset : PRESETS) {
            if (preset.isAvailable()) result.add(preset);
        }
        return result;
    }

    public static Preset get(String id) {
        if (id == null) return null;
        for (Preset preset : PRESETS) {
            if (preset.id.equals(id)) return preset;
        }
        return null;
    }
}
