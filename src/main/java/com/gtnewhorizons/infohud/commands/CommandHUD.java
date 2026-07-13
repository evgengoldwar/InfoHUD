package com.gtnewhorizons.infohud.commands;

import static com.gtnewhorizons.infohud.InfoHUD.MODID;
import static com.gtnewhorizons.infohud.InfoHUD.MODNAME;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import com.gtnewhorizon.gtnhlib.config.SimpleGuiConfig;
import com.gtnewhorizons.infohud.hud.event.DelayedGuiDisplayTicker;

public class CommandHUD extends CommandBase {

    @Override
    public String getCommandName() {
        return "hud";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/hud";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        try {
            DelayedGuiDisplayTicker.create(new SimpleGuiConfig(null, MODID, MODNAME), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
