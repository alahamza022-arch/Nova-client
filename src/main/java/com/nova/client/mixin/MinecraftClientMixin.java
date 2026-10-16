package com.nova.client.mixin;

import com.nova.client.NovaConfig;
import com.nova.client.ui.NovaPauseScreen;
import com.nova.client.ui.NovaTitleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    /** Swaps the vanilla title + ESC menu for Nova's versions. */
    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
    private Screen nova$swap(Screen screen) {
        if (screen == null || !NovaConfig.get().customMenus) return screen;
        if (screen.getClass() == TitleScreen.class) return new NovaTitleScreen();
        if (screen.getClass() == GameMenuScreen.class) return new NovaPauseScreen();
        return screen;
    }
}
