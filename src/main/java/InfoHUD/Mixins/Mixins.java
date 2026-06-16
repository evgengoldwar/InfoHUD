package InfoHUD.Mixins;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

public enum Mixins implements IMixins {

    MINECRAFT_LATE(new MixinBuilder("Minecraft Late").addClientMixins("ArmorBarRenderMixin")
        .setPhase(Phase.LATE)),

    MINECRAFT_EARLY(new MixinBuilder("Minecraft Early")
        .addClientMixins(
            "InventoryEffectRendererMixin",
            "NetHandlerPlayerClientMixin",
            "GuiIngameMixin",
            "MinecraftAccessor")
        .addServerMixins("MinecraftServerMixin", "NetHandlerPlayServerMixin", "NetHandlerPlayServerDisconnectMixin")
        .setPhase(Phase.EARLY));

    private final MixinBuilder builder;

    Mixins(MixinBuilder builder) {
        this.builder = builder;
    }

    @Nonnull
    @Override
    public MixinBuilder getBuilder() {
        return builder;
    }
}
