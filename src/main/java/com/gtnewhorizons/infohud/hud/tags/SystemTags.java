package com.gtnewhorizons.infohud.hud.tags;

import static com.gtnewhorizons.infohud.hud.tags.TagRegistry.register;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import org.lwjgl.opengl.GL11;

final class SystemTags {

    private static volatile String cpuName = null;
    private static String gpuName = null;

    private SystemTags() {}

    static void registerTags() {
        detectCpu();

        String cat = "system";
        register(cat, "cpu", () -> cpuName);
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
