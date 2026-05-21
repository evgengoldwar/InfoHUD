package InfoHUD.Configs;

import static InfoHUD.InfoHUD.MODID;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = MODID, category = "hud", configSubDirectory = "InfoHUD", filename = "HudConfig")
@Config.LangKey("minecraftimprovements.config.hud.name")
public class HudConfig {

    public static final HudGeneral hudGeneral = new HudGeneral();
    public static final HudOrder hudOrder = new HudOrder();
    public static final HudItems hudItems = new HudItems();
    public static final HudEnabled hudEnabled = new HudEnabled();
    public static final HudPotion hudPotion = new HudPotion();

    @Config.LangKey("minecraftimprovements.config.hud.hud_general.name")
    public static class HudGeneral {

        @Config.DefaultInt(0)
        @Config.Order(1)
        public int HudX;

        @Config.DefaultInt(0)
        @Config.Order(2)
        public int HudY;

        @Config.DefaultFloat(1.0F)
        @Config.RangeFloat(min = 0.5F, max = 2.0F)
        @Config.Order(3)
        public float HudScale;

        @Config.DefaultBoolean(false)
        @Config.Order(4)
        public boolean HudDisable;
    }

    @Config.LangKey("minecraftimprovements.config.hud.hud_potion.name")
    public static class HudPotion {

        @Config.DefaultBoolean(true)
        @Config.Order(1)
        public boolean PotionEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(2)
        public boolean TimeEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(3)
        public boolean LevelEnable;
    }

    @Config.LangKey("minecraftimprovements.config.hud.hud_order.name")
    public static class HudOrder {

        @Config.DefaultInt(1)
        @Config.RangeInt(min = 1, max = 100)
        @Config.Order(1)
        public int FpsOrder;

        @Config.DefaultInt(4)
        @Config.RangeInt(min = 1, max = 100)
        @Config.Order(2)
        public int MemoryOrder;

        @Config.DefaultInt(5)
        @Config.RangeInt(min = 1, max = 100)
        @Config.Order(3)
        public int ServerMemoryOrder;

        @Config.DefaultInt(2)
        @Config.RangeInt(min = 1, max = 100)
        @Config.Order(4)
        public int PingOrder;

        @Config.DefaultInt(6)
        @Config.RangeInt(min = 1, max = 100)
        @Config.Order(5)
        public int PositionOrder;

        @Config.DefaultInt(3)
        @Config.RangeInt(min = 1, max = 100)
        @Config.Order(6)
        public int TpsOrder;

        @Config.DefaultInt(7)
        @Config.RangeInt(min = 1, max = 100)
        @Config.Order(7)
        public int LpOrder;

        @Config.DefaultInt(8)
        @Config.RangeInt(min = 1, max = 100)
        @Config.Order(8)
        public int WarpOrder;

        @Config.DefaultInt(9)
        @Config.RangeInt(min = 1, max = 100)
        @Config.Order(9)
        public int DimensionOrder;

        @Config.DefaultInt(10)
        @Config.RangeInt(min = 1, max = 100)
        @Config.Order(10)
        public int BiomeOrder;

        @Config.DefaultInt(11)
        @Config.RangeInt(min = 1, max = 100)
        @Config.Order(11)
        public int DirectionOrder;

        @Config.DefaultInt(12)
        @Config.RangeInt(min = 1, max = 100)
        @Config.Order(12)
        public int OreChunkOrder;

        @Config.DefaultInt(13)
        @Config.RangeInt(min = 1, max = 100)
        @Config.Order(13)
        public int SlimeChunkOrder;

        @Config.DefaultInt(14)
        @Config.RangeInt(min = 1, max = 100)
        @Config.Order(14)
        public int WorldTimeOrder;

        @Config.DefaultInt(15)
        @Config.RangeInt(min = 1, max = 100)
        @Config.Order(15)
        public int PlayTimeOrder;

        @Config.DefaultInt(16)
        @Config.RangeInt(min = 1, max = 100)
        @Config.Order(16)
        public int SessionTimeOrder;

        @Config.DefaultInt(17)
        @Config.RangeInt(min = 1, max = 100)
        @Config.Order(17)
        public int JumpOrder;

    }

    @Config.LangKey("minecraftimprovements.config.hud.hud_icons.name")
    public static class HudItems {

        @Config.DefaultString("minecraft:emerald")
        @Config.Order(1)
        public String FpsItem;

        @Config.DefaultString("minecraft:redstone")
        @Config.Order(2)
        public String MemoryItem;

        @Config.DefaultString("minecraft:repeater")
        @Config.Order(3)
        public String ServerMemoryItem;

        @Config.DefaultString("minecraft:stone")
        @Config.Order(4)
        public String PingItem;

        @Config.DefaultString("minecraft:grass")
        @Config.Order(5)
        public String PositionItem;

        @Config.DefaultString("minecraft:command_block")
        @Config.Order(6)
        public String TpsItem;

        @Config.DefaultString("AWWayofTime:weakBloodOrb")
        @Config.Order(7)
        public String LpItem;

        @Config.DefaultString("Thaumcraft:ItemEldritchObject")
        @Config.Order(8)
        public String WarpItem;

        @Config.DefaultString("minecraft:ender_eye")
        @Config.Order(9)
        public String DimensionItem;

        @Config.DefaultString("minecraft:sapling")
        @Config.Order(10)
        public String BiomeItem;

        @Config.DefaultString("minecraft:compass")
        @Config.Order(11)
        public String DirectionItem;

        @Config.DefaultString("minecraft:iron_ore")
        @Config.Order(12)
        public String OreChunkItem;

        @Config.DefaultString("minecraft:slime_ball")
        @Config.Order(13)
        public String SlimeChunkItem;

        @Config.DefaultString("minecraft:clock")
        @Config.Order(14)
        public String WorldTimeItem;

        @Config.DefaultString("minecraft:noteblock")
        @Config.Order(15)
        public String PlayTimeItem;

        @Config.DefaultString("minecraft:golden_apple/1")
        @Config.Order(16)
        public String SessionTimeItem;

        @Config.DefaultString("minecraft:slime_ball")
        @Config.Order(17)
        public String JumpItem;
    }

    @Config.LangKey("minecraftimprovements.config.hud.hud_toggles.name")
    public static class HudEnabled {

        @Config.DefaultBoolean(true)
        @Config.Order(1)
        public boolean FpsEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(2)
        public boolean MemoryEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(3)
        public boolean ServerMemoryEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(4)
        public boolean PingEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(5)
        public boolean PositionEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(6)
        public boolean TpsEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(7)
        public boolean LpEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(8)
        public boolean WarpEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(9)
        public boolean DimensionEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(10)
        public boolean BiomeEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(11)
        public boolean DirectionEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(12)
        public boolean OreChunkEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(13)
        public boolean SlimeChunkEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(14)
        public boolean WorldTimeEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(15)
        public boolean PlayTimeEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(16)
        public boolean SessionTimeEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(17)
        public boolean CountItemEnable;

        @Config.DefaultBoolean(true)
        @Config.Order(18)
        public boolean JumpEnable;
    }

}
