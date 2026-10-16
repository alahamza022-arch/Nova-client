package com.nova.client.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

/** Replacement for the vanilla ESC menu. */
public class NovaPauseScreen extends Screen {
    public NovaPauseScreen() { super(Text.literal("Game Menu")); }

    @Override protected void init() {
        int cx = width / 2, y = height / 2 - 40;
        addDrawableChild(ButtonWidget.builder(Text.literal("Back to Game"),
                b -> close()).dimensions(cx - 100, y, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Advancements"),
                b -> client.setScreen(new AdvancementsScreen(client.player.networkHandler.getAdvancementHandler())))
                .dimensions(cx - 100, y + 24, 98, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Statistics"),
                b -> client.setScreen(new StatsScreen(this, client.player.getStatHandler())))
                .dimensions(cx + 2, y + 24, 98, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("\u2726 Nova Settings"),
                b -> client.setScreen(new NovaSettingsScreen(this))).dimensions(cx - 100, y + 48, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Options"),
                b -> client.setScreen(new OptionsScreen(this, client.options))).dimensions(cx - 100, y + 72, 98, 20).build());
        boolean local = client.isInSingleplayer();
        addDrawableChild(ButtonWidget.builder(Text.literal(local ? "Save & Quit" : "Disconnect"),
                b -> disconnect()).dimensions(cx + 2, y + 72, 98, 20).build());
    }

    private void disconnect() {
        boolean local = client.isInSingleplayer();
        client.world.disconnect();
        if (local) client.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
        else client.disconnect();
        if (local) client.setScreen(new TitleScreen());
        else client.setScreen(new MultiplayerScreen(new TitleScreen()));
    }

    @Override public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
        Cosmos.render(ctx, width, height, mx, my, true);
    }

    @Override public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        double t = Util.getMeasuringTimeMs();
        int color = Cosmos.lerp(Cosmos.accent(), Cosmos.accent2(), (float) (Math.sin(t / 600.0) + 1) / 2f);
        var m = ctx.getMatrices();
        m.push();
        m.translate(width / 2f, height / 2f - 85, 0);
        m.scale(3f, 3f, 1f);
        ctx.drawCenteredTextWithShadow(textRenderer, "NOVA", 0, 0, color);
        m.pop();
        ctx.drawCenteredTextWithShadow(textRenderer, "Game Paused", width / 2, height / 2 - 58, 0xFFB0B6CC);
    }
}
