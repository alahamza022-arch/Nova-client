package com.nova.client.mixin;

import com.nova.client.NovaConfig;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "hasGlint", at = @At("HEAD"), cancellable = true)
    private void nova$glint(CallbackInfoReturnable<Boolean> cir) {
        if (!NovaConfig.get().glint) cir.setReturnValue(false);
    }
}
