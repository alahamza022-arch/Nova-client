package com.nova.client.mixin;

import com.nova.client.NovaClient;
import com.nova.client.NovaConfig;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void nova$zoom(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        if (NovaClient.zoom > 1.001) cir.setReturnValue(cir.getReturnValue() / NovaClient.zoom);
    }

    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void nova$noHurtCam(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (!NovaConfig.get().hurtCam) ci.cancel();
    }
}
