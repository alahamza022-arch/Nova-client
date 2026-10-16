package com.nova.client;

import com.nova.client.mixin.SimpleOptionAccessor;
import com.nova.client.ui.Cosmos;
import com.nova.client.ui.NovaSettingsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.*;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class NovaClient implements ClientModInitializer {
    public static double zoom = 1.0;
    private static KeyBinding zoomKey, settingsKey;
    private static boolean fbActive = false;
    private static double origGamma = 1.0;

    @Override
    public void onInitializeClient() {
        NovaConfig.load();
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.nova.zoom",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "category.nova"));
        settingsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.nova.settings",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.nova"));

        // Auto-apply the best FPS preset the first time the game launches.
        ClientLifecycleEvents.CLIENT_STARTED.register(mc -> {
            NovaConfig c = NovaConfig.get();
            if (c.firstRun) {
                applyBestPreset(mc);
                c.firstRun = false;
                NovaConfig.save();
            }
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(NovaClient::restoreGamma);
        ClientTickEvents.END_CLIENT_TICK.register(NovaClient::tick);
        HudRenderCallback.EVENT.register(NovaClient::hud);
    }

    /** One-click "max FPS, min delay" preset. */
    public static void applyBestPreset(MinecraftClient mc) {
        GameOptions o = mc.options;
        o.getViewDistance().setValue(10);
        o.getSimulationDistance().setValue(8);
        o.getEntityDistanceScaling().setValue(0.75);
        o.getGraphicsMode().setValue(GraphicsMode.FAST);
        o.getCloudRenderMode().setValue(CloudRenderMode.OFF);
        o.getParticles().setValue(ParticlesMode.MINIMAL);
        o.getAo().setValue(false);
        o.getBiomeBlendRadius().setValue(1);
        o.getEntityShadows().setValue(false);
        o.getEnableVsync().setValue(false);   // no vsync = lowest input delay
        o.getMaxFps().setValue(260);          // 260 = unlimited
        o.getRawMouseInput().setValue(true);  // raw mouse = no OS acceleration lag
        o.write();
    }

    @SuppressWarnings("unchecked")
    private static void tick(MinecraftClient mc) {
        NovaConfig cfg = NovaConfig.get();

        double target = zoomKey.isPressed() ? cfg.zoomLevel : 1.0;
        zoom = cfg.zoomSmooth ? zoom + (target - zoom) * 0.4 : target;
        if (Math.abs(zoom - target) < 0.01) zoom = target;

        while (settingsKey.wasPressed()) {
            if (mc.currentScreen == null) mc.setScreen(new NovaSettingsScreen(null));
        }

        SimpleOption<Double> gamma = mc.options.getGamma();
        if (cfg.fullbright) {
            if (!fbActive) { origGamma = Math.min(gamma.getValue(), 1.0); fbActive = true; }
            if (gamma.getValue() != 15.0) ((SimpleOptionAccessor<Double>) (Object) gamma).nova$setValue(15.0);
        } else if (fbActive) {
            fbActive = false;
            gamma.setValue(origGamma);
        }
    }

    private static void restoreGamma(MinecraftClient mc) {
        if (fbActive) { fbActive = false; mc.options.getGamma().setValue(origGamma); mc.options.write(); }
    }

    private static void hud(DrawContext ctx, RenderTickCounter tc) {
        MinecraftClient mc = MinecraftClient.getInstance();
        NovaConfig c = NovaConfig.get();
        if (mc.options.hudHidden || mc.player == null) return;
        var tr = mc.textRenderer;
        int y = 4, accent = Cosmos.accent();

        if (c.fpsHud) {
            String fps = mc.getCurrentFps() + " FPS";
            int w = tr.getWidth("NOVA  " + fps) + 10;
            ctx.fill(4, y, 4 + w, y + 14, 0x99000000);
            ctx.fill(4, y, 6, y + 14, accent);
            ctx.drawTextWithShadow(tr, "NOVA", 10, y + 3, accent);
            ctx.drawTextWithShadow(tr, fps, 10 + tr.getWidth("NOVA  "), y + 3, 0xFFFFFFFF);
            y += 16;
        }
        if (c.coordsHud) {
            String s = String.format("XYZ  %.1f  %.1f  %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
            ctx.fill(4, y, 4 + tr.getWidth(s) + 10, y + 14, 0x99000000);
            ctx.fill(4, y, 6, y + 14, accent);
            ctx.drawTextWithShadow(tr, s, 10, y + 3, 0xFFFFFFFF);
        }
    }
}
