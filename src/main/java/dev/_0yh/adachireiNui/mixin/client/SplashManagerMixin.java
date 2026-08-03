package dev._0yh.adachireiNui.mixin.client;

import dev._0yh.adachireiNui.client.AdachireiSplashFetcher;
import dev._0yh.adachireiNui.config.AdachireiConfig;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.resources.SplashManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SplashManager.class)
public class SplashManagerMixin {
    @Inject(method = "getSplash", at = @At("HEAD"), cancellable = true)
    private void adachireiNui$getSplash(CallbackInfoReturnable<SplashRenderer> callback) {
        if (!AdachireiConfig.get().enableCustomSplash) {
            return;
        }

        String apiText = AdachireiSplashFetcher.getCachedSplash();
        if (apiText != null) {
            callback.setReturnValue(new SplashRenderer(apiText));
        }

        AdachireiSplashFetcher.fetchAsync();
    }
}
