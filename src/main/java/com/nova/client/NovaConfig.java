package com.nova.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class NovaConfig {
    public boolean firstRun = true;
    public boolean customMenus = true;
    public int theme = 0;

    public boolean glint = true;
    public boolean fullbright = false;
    public boolean hurtCam = true;
    public boolean fpsHud = true;
    public boolean coordsHud = false;
    public int zoomLevel = 4;
    public boolean zoomSmooth = true;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("nova_client.json");
    private static NovaConfig instance;

    public static NovaConfig get() {
        if (instance == null) load();
        return instance;
    }

    public static void load() {
        try {
            if (Files.exists(FILE)) instance = GSON.fromJson(Files.readString(FILE), NovaConfig.class);
        } catch (Exception ignored) {}
        if (instance == null) instance = new NovaConfig();
    }

    public static void save() {
        try { Files.writeString(FILE, GSON.toJson(get())); } catch (Exception ignored) {}
    }
}
