package com.nova.client.ui;

import com.nova.client.NovaClient;
import com.nova.client.NovaConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class NovaSettingsScreen extends Screen {
    private enum Tab { VISUALS("Visuals"), PERFORMANCE("Performance"), CONTROLS("Controls"), MENU("Menu");
        final String label; Tab(String l) { label = l; } }

    private static Tab tab = Tab.VISUALS;
    private final Screen parent;

    public NovaSettingsScreen(Screen parent) {
        super(Text.literal("Nova Settings"));
        this.parent = parent;
    }

    @Override public void close() { client.setScreen(parent); }

    private int sx(int i) { return i % 2 == 0 ? width / 2 - 155 : width / 2 + 5; }
    private int sy(int i) { return 62 + (i / 2) * 24; }

    @Override protected void init() {
        GameOptions o = client.options;
        NovaConfig c = NovaConfig.get();

        Tab[] ts = Tab.values();
        int tw = 74, total = ts.length * tw + (ts.length - 1) * 4, x0 = width / 2 - total / 2;
        for (int i = 0; i < ts.length; i++) {
            Tab t = ts[i];
            ButtonWidget b = ButtonWidget.builder(Text.literal(t.label), btn -> { tab = t; clearAndInit(); })
                    .dimensions(x0 + i * (tw + 4), 32, tw, 20).build();
            b.active = t != tab;
            addDrawableChild(b);
        }

        switch (tab) {
            case VISUALS -> {
                addDrawableChild(toggle(0, "Fullbright", () -> c.fullbright, v -> c.fullbright = v));
                addDrawableChild(toggle(1, "Enchant Glint", () -> c.glint, v -> c.glint = v));
                addDrawableChild(toggle(2, "Hurt Cam Shake", () -> c.hurtCam, v -> c.hurtCam = v));
                addDrawableChild(o.getFov().createWidget(o, sx(3), sy(3), 150));
                addDrawableChild(o.getParticles().createWidget(o, sx(4), sy(4), 150));
                addDrawableChild(o.getCloudRenderMode().createWidget(o, sx(5), sy(5), 150));
                addDrawableChild(o.getAo().createWidget(o, sx(6), sy(6), 150));
                addDrawableChild(o.getEntityShadows().createWidget(o, sx(7), sy(7), 150));
                addDrawableChild(o.getBobView().createWidget(o, sx(8), sy(8), 150));
                addDrawableChild(o.getGamma().createWidget(o, sx(9), sy(9), 150));
                addDrawableChild(slider(10, "Zoom", 2, 10, "x", () -> c.zoomLevel, v -> c.zoomLevel = v));
                addDrawableChild(toggle(11, "Smooth Zoom", () -> c.zoomSmooth, v -> c.zoomSmooth = v));
            }
            case PERFORMANCE -> {
                addDrawableChild(ButtonWidget.builder(Text.literal("\u26A1 Apply Best FPS Preset"), b -> {
                    NovaClient.applyBestPreset(client);
                    clearAndInit();
                }).dimensions(width / 2 - 155, 62, 310, 20).build());
                addDrawableChild(o.getMaxFps().createWidget(o, sx(2), sy(2) + 0, 150));
                addDrawableChild(o.getEnableVsync().createWidget(o, sx(3), sy(3), 150));
                addDrawableChild(o.getViewDistance().createWidget(o, sx(4), sy(4), 150));
                addDrawableChild(o.getSimulationDistance().createWidget(o, sx(5), sy(5), 150));
                addDrawableChild(o.getEntityDistanceScaling().createWidget(o, sx(6), sy(6), 150));
                addDrawableChild(o.getGraphicsMode().createWidget(o, sx(7), sy(7), 150));
                addDrawableChild(o.getBiomeBlendRadius().createWidget(o, sx(8), sy(8), 150));
                addDrawableChild(o.getMipmapLevels().createWidget(o, sx(9), sy(9), 150));
            }
            case CONTROLS -> {
                addDrawableChild(o.getRawMouseInput().createWidget(o, sx(0), sy(0), 150));
                addDrawableChild(o.getMouseSensitivity().createWidget(o, sx(1), sy(1), 150));
                addDrawableChild(o.getAutoJump().createWidget(o, sx(2), sy(2), 150));
                addDrawableChild(o.getAttackIndicator().createWidget(o, sx(3), sy(3), 150));
                addDrawableChild(toggle(4, "FPS HUD", () -> c.fpsHud, v -> c.fpsHud = v));
                addDrawableChild(toggle(5, "Coords HUD", () -> c.coordsHud, v -> c.coordsHud = v));
            }
            case MENU -> {
                ButtonWidget theme = ButtonWidget.builder(themeLabel(), b -> {
                    c.theme = (c.theme + 1) % Cosmos.NAMES.length;
                    NovaConfig.save();
                    b.setMessage(themeLabel());
                }).dimensions(sx(0), sy(0), 150, 20).build();
                addDrawableChild(theme);
                addDrawableChild(toggle(1, "Custom Menus", () -> c.customMenus, v -> c.customMenus = v));
            }
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(width / 2 - 100, height - 28, 200, 20).build());
    }

    private Text themeLabel() {
        return Text.literal("Theme: " + Cosmos.NAMES[Math.floorMod(NovaConfig.get().theme, Cosmos.NAMES.length)]);
    }

    private static Text onOff(String name, boolean on) {
        return Text.literal(name + ": ").append(Text.literal(on ? "ON" : "OFF").formatted(on ? Formatting.GREEN : Formatting.RED));
    }

    private ButtonWidget toggle(int slot, String name, BooleanSupplier get, Consumer<Boolean> set) {
        return ButtonWidget.builder(onOff(name, get.getAsBoolean()), b -> {
            boolean v = !get.getAsBoolean();
            set.accept(v);
            NovaConfig.save();
            b.setMessage(onOff(name, v));
        }).dimensions(sx(slot), sy(slot), 150, 20).build();
    }

    private ClickableWidget slider(int slot, String name, int min, int max, String suffix, IntSupplier get, IntConsumer set) {
        return new SliderWidget(sx(slot), sy(slot), 150, 20, Text.empty(), (get.getAsInt() - min) / (double) (max - min)) {
            { updateMessage(); }
            private int cur() { return min + (int) Math.round(value * (max - min)); }
            @Override protected void updateMessage() { setMessage(Text.literal(name + ": " + cur() + suffix)); }
            @Override protected void applyValue() { set.accept(cur()); NovaConfig.save(); }
        };
    }

    @Override public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
        Cosmos.render(ctx, width, height, mx, my, true);
    }

    @Override public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, "NOVA SETTINGS", width / 2, 14, Cosmos.accent());

        if (tab == Tab.PERFORMANCE) {
            String[][] mods = {{"sodium", "Sodium"}, {"lithium", "Lithium"}, {"ferritecore", "FerriteCore"},
                    {"immediatelyfast", "ImmediatelyFast"}, {"entityculling", "Entity Culling"}, {"krypton", "Krypton"}};
            ctx.drawTextWithShadow(textRenderer, "Boost mods (drop the jars in /mods for big FPS gains):",
                    width / 2 - 155, height - 62, 0xFFB0B6CC);
            for (int i = 0; i < mods.length; i++) {
                boolean on = FabricLoader.getInstance().isModLoaded(mods[i][0]);
                int x = width / 2 - 155 + (i % 3) * 105, y = height - 50 + (i / 3) * 10;
                ctx.drawTextWithShadow(textRenderer, mods[i][1] + (on ? " +" : " -"), x, y, on ? 0xFF55FF55 : 0xFFFF6666);
            }
        }
    }
}
