package com.nova.client.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class NovaTitleScreen extends Screen {
    public NovaTitleScreen() { super(Text.literal("Nova Client")); }

    @Override protected void init() {
        int cx = width / 2, y = height / 2 - 10;
        addDrawableChild(ButtonWidget.builder(Text.literal("Singleplayer"),
                b -> client.setScreen(new SelectWorldScreen(this))).dimensions(cx - 100, y, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Multiplayer"),
                b -> client.setScreen(new MultiplayerScreen(this))).dimensions(cx - 100, y + 24, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("\u2726 Nova Settings"),
                b -> client.setScreen(new NovaSettingsScreen(this))).dimensions(cx - 100, y + 48, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Options"),
                b -> client.setScreen(new OptionsScreen(this, client.options))).dimensions(cx - 100, y + 72, 98, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Quit"),
                b -> client.scheduleStop()).dimensions(cx + 2, y + 72, 98, 20).build());
    }

    @Override public boolean shouldCloseOnEsc() { return false; }

    @Override public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
        Cosmos.render(ctx, width, height, mx, my, false);
    }

    @Override public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        double t = Util.getMeasuringTimeMs();
        String word = "NOVA";
        int spacing = 48, cx = width / 2, top = height / 2 - 95;
        for (int i = 0; i < word.length(); i++) {
            float pulse = (float) (Math.sin(t / 500.0 + i * 0.9) + 1) / 2f;
            int color = Cosmos.lerp(Cosmos.accent(), Cosmos.accent2(), pulse);
            float bob = (float) Math.sin(t / 700.0 + i) * 2f;
            float x = cx - (word.length() * spacing) / 2f + spacing / 2f + i * spacing;
            var m = ctx.getMatrices();
            m.push();
            m.translate(x, top + bob, 0);
            m.scale(5f, 5f, 1f);
            ctx.drawCenteredTextWithShadow(textRenderer, String.valueOf(word.charAt(i)), 0, 0, color);
            m.pop();
        }
        ctx.drawCenteredTextWithShadow(textRenderer, "C  L  I  E  N  T", cx, top + 52, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, "Nova Client 1.0  |  Minecraft 1.21.1", 6, height - 12, 0xFF9AA0B5);
        String tag = "Lag less. Play more.";
        ctx.drawTextWithShadow(textRenderer, tag, width - textRenderer.getWidth(tag) - 6, height - 12, Cosmos.accent());
    }
}
