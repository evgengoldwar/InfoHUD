package com.gtnewhorizons.infohud.hud.core.infolines;

import com.gtnewhorizons.infohud.configs.HudConfig;
import com.gtnewhorizons.infohud.hud.core.DataStorage;
import com.gtnewhorizons.infohud.hud.core.InfoLine;

public class InfoMemory extends InfoLine {

    private static final int MB = 1048576;
    private final boolean isServer;

    public InfoMemory(int order, boolean server) {
        super(order);
        isServer = server;
    }

    @Override
    public boolean canRender() {
        return isServer ? HudConfig.hudEnabled.ServerMemoryEnable && !(DataStorage.serverMemUsed == -1)
            : HudConfig.hudEnabled.MemoryEnable;
    }

    @Override
    public String getLineString() {
        return isServer ? tr("info_server_memory", DataStorage.serverMemUsed, DataStorage.serverMemMax)
            : tr("info_memory", getUsedMemory(), getMaxMemory());
    }

    public int getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return Math.toIntExact((runtime.totalMemory() - runtime.freeMemory()) / (long) MB);
    }

    public int getAllocatedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return Math.toIntExact(runtime.totalMemory() / (long) MB);
    }

    public int getMaxMemory() {
        Runtime runtime = Runtime.getRuntime();
        return Math.toIntExact(runtime.maxMemory() / (long) MB);
    }

    @Override
    public String getItemName() {
        return isServer ? HudConfig.hudItems.ServerMemoryItem : HudConfig.hudItems.MemoryItem;
    }
}
