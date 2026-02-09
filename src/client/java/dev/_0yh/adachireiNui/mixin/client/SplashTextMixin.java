package dev._0yh.adachireiNui.mixin.client;

import dev._0yh.adachireiNui.client.AdachireiSplashFetcher;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextMixin {

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void onGet(CallbackInfoReturnable<SplashTextRenderer> cir) {
        String apiText = AdachireiSplashFetcher.consumeCachedSplash();
        if (apiText != null) {
            cir.setReturnValue(new SplashTextRenderer(apiText));
        }
        AdachireiSplashFetcher.fetchAsync();
    }
}
