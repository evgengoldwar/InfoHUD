package com.gtnewhorizons.infohud.commands;

import static com.gtnewhorizons.infohud.InfoHUD.MODID;
import static com.gtnewhorizons.infohud.InfoHUD.MODNAME;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import com.gtnewhorizon.gtnhlib.config.SimpleGuiConfig;
import com.gtnewhorizons.infohud.hud.event.DelayedGuiDisplayTicker;
import com.gtnewhorizons.infohud.hud.gui.GuiHudEditor;

public class CommandHUD extends CommandBase {

    @Override
    public String getCommandName() {
        return "hud";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/hud [edit]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("edit")) {
            DelayedGuiDisplayTicker.create(new GuiHudEditor(), 0);
            return;
        }

        try {
            DelayedGuiDisplayTicker.create(new SimpleGuiConfig(null, MODID, MODNAME), 0);
        } catch (Exception ignored) {}
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "edit");
        }
        return Collections.emptyList();
    }
}
