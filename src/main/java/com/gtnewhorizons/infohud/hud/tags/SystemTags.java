package com.gtnewhorizons.infohud.hud.tags;

import static com.gtnewhorizons.infohud.hud.tags.TagRegistry.register;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizons.infohud.mixins.early.MinecraftAccessor;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

final class SystemTags {

    private static final int FPS_SAMPLES = 10;

    private static volatile String cpuName = null;
    private static String gpuName = null;
    private static final int[] fpsSamples = new int[FPS_SAMPLES];
    private static int fpsSampleCount = 0;
    private static int fpsSampleIndex = 0;
    private static double cpuLoad = -1;
    private static int sampleTimer = 0;
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    private SystemTags() {}

    static void registerTags() {
        detectCpu();
        FMLCommonHandler.instance()
            .bus()
            .register(new Sampler());

        String cat = "system";
        register(cat, "cpu", () -> cpuName);
        register(
            cat,
            "cpu_cores",
            () -> String.valueOf(
                Runtime.getRuntime()
                    .availableProcessors()));
        register(cat, "cpu_usage", () -> cpuLoad < 0 ? null : String.valueOf(Math.round(cpuLoad * 100)));
        register(cat, "fps_min", () -> fpsSampleCount == 0 ? null : String.valueOf(minFps()));
        register(cat, "fps_avg", () -> fpsSampleCount == 0 ? null : String.valueOf(avgFps()));
        register(cat, "real_time", () -> TIME_FORMAT.format(new Date()));
        register(cat, "real_date", () -> DATE_FORMAT.format(new Date()));
        register(cat, "gpu", SystemTags::gpu);
        register(cat, "java", () -> System.getProperty("java.version"));
        register(cat, "server_ip", () -> {
            Minecraft mc = Minecraft.getMinecraft();
            ServerData server = mc.func_147104_D();
            return mc.isSingleplayer() || server == null ? null : server.serverIP;
        });
        register(
            cat,
            "players",
            () -> String.valueOf(Minecraft.getMinecraft().thePlayer.sendQueue.playerInfoList.size()));
    }

    private static int minFps() {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < fpsSampleCount; i++) {
            min = Math.min(min, fpsSamples[i]);
        }
        return min;
    }

    private static int avgFps() {
        int sum = 0;
        for (int i = 0; i < fpsSampleCount; i++) {
            sum += fpsSamples[i];
        }
        return Math.round((float) sum / fpsSampleCount);
    }

    private static double readCpuLoad() {
        try {
            OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
            if (bean instanceof com.sun.management.OperatingSystemMXBean) {
                return ((com.sun.management.OperatingSystemMXBean) bean).getProcessCpuLoad();
            }
        } catch (Throwable ignored) {}
        return -1;
    }

    public static class Sampler {

        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            if (++sampleTimer < 20) return;
            sampleTimer = 0;

            fpsSamples[fpsSampleIndex] = ((MinecraftAccessor) Minecraft.getMinecraft()).getFps();
            fpsSampleIndex = (fpsSampleIndex + 1) % FPS_SAMPLES;
            fpsSampleCount = Math.min(fpsSampleCount + 1, FPS_SAMPLES);
            cpuLoad = readCpuLoad();
        }
    }

    private static String gpu() {
        if (gpuName == null) {
            gpuName = GL11.glGetString(GL11.GL_RENDERER);
        }
        return gpuName;
    }

    private static void detectCpu() {
        Thread thread = new Thread(() -> {
            String name = null;
            try {
                name = readCpuName();
            } catch (Exception ignored) {}

            if (name == null || name.isEmpty()) {
                name = System.getenv("PROCESSOR_IDENTIFIER");
            }
            if (name == null || name.isEmpty()) {
                name = System.getProperty("os.arch");
            }
            cpuName = name.trim()
                .replaceAll("\\s+", " ");
        }, "InfoHUD CPU detection");
        thread.setDaemon(true);
        thread.start();
    }

    private static String readCpuName() throws Exception {
        String os = System.getProperty("os.name", "")
            .toLowerCase(Locale.ROOT);

        if (os.contains("win")) {
            String output = run(
                "reg",
                "query",
                "HKEY_LOCAL_MACHINE\\HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0",
                "/v",
                "ProcessorNameString");
            for (String line : output.split("\n")) {
                int index = line.indexOf("REG_SZ");
                if (line.contains("ProcessorNameString") && index >= 0) {
                    return line.substring(index + "REG_SZ".length())
                        .trim();
                }
            }
            return null;
        }

        if (os.contains("mac")) {
            return run("sysctl", "-n", "machdep.cpu.brand_string").trim();
        }

        File cpuInfo = new File("/proc/cpuinfo");
        if (cpuInfo.exists()) {
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(cpuInfo), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("model name")) {
                        return line.substring(line.indexOf(':') + 1)
                            .trim();
                    }
                }
            }
        }
        return null;
    }

    private static String run(String... command) throws Exception {
        Process process = new ProcessBuilder(command).redirectErrorStream(true)
            .start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line)
                    .append('\n');
            }
        }
        process.waitFor(5, TimeUnit.SECONDS);
        return output.toString();
    }
}
