package com.gtnewhorizons.infohud;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

import com.gtnewhorizons.infohud.classicbar.ClassicBar;
import com.gtnewhorizons.infohud.commands.CommandHUD;
import com.gtnewhorizons.infohud.hud.Hud;
import com.gtnewhorizons.infohud.keybinds.KeyBindHandler;
import com.gtnewhorizons.infohud.lightoverlay.OverlayEventHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        Hud.initEvent();
        ClassicBar.initEvent();

        ClientCommandHandler.instance.registerCommand(new CommandHUD());

        MinecraftForge.EVENT_BUS.register(new OverlayEventHandler());
        FMLCommonHandler.instance()
            .bus()
            .register(new KeyBindHandler());
    }
}
