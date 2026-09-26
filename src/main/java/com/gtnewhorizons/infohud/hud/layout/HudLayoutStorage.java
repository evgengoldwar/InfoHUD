package com.gtnewhorizons.infohud.hud.layout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gtnewhorizons.infohud.InfoHUD;

public final class HudLayoutStorage {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .disableHtmlEscaping()
        .create();
    private static final String FILE_NAME = "Layout.json";
    private static final String LEGACY_FILE_NAME = "Positions.json";

    private static File configDir;
    private static HudLayout layout;

    private HudLayoutStorage() {}

    public static void init(File dir) {
        configDir = dir;
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
    }

    public static HudLayout get() {
        if (layout == null) {
            load();
        }
        return layout;
    }

    public static void load() {
        File file = new File(configDir, FILE_NAME);

        if (file.exists()) {
            try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                HudLayout loaded = GSON.fromJson(reader, HudLayout.class);
                if (loaded != null) {
                    layout = loaded.sanitize();
                    return;
                }
            } catch (Exception e) {
                InfoHUD.LOG.error("Failed to read HUD layout, using defaults", e);
            }
        }

        layout = HudLayout.createDefault();
        migrateLegacy(layout);
        save(layout);
    }

    public static void save(HudLayout newLayout) {
        layout = newLayout.sanitize();

        File file = new File(configDir, FILE_NAME);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            GSON.toJson(layout, writer);
        } catch (Exception e) {
            InfoHUD.LOG.error("Failed to save HUD layout", e);
        }
    }

    private static void migrateLegacy(HudLayout target) {
        File legacy = new File(configDir, LEGACY_FILE_NAME);
        if (!legacy.exists()) return;

        try (Reader reader = new InputStreamReader(new FileInputStream(legacy), StandardCharsets.UTF_8)) {
            JsonElement root = new JsonParser().parse(reader);
            if (!root.isJsonObject()) return;
            JsonObject obj = root.getAsJsonObject();

            if (obj.has("scale")) target.setScale(
                obj.get("scale")
                    .getAsFloat());
            if (obj.has("effectX")) target.potionX = obj.get("effectX")
                .getAsInt();
            if (obj.has("effectY")) target.potionY = obj.get("effectY")
                .getAsInt();
            if (obj.has("countItemX")) target.countItemX = obj.get("countItemX")
                .getAsInt();
            if (obj.has("countItemY")) target.countItemY = obj.get("countItemY")
                .getAsInt();
        } catch (Exception ignored) {}
    }
}
