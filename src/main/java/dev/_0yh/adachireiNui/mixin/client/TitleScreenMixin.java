package dev._0yh.adachireiNui.mixin.client;

import dev._0yh.adachireiNui.client.AdachireiSplashFetcher;
import dev._0yh.adachireiNui.config.AdachireiConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Shadow(remap = false)
    private SplashRenderer splash;

    @Unique
    private boolean adachireiNui$customSplashApplied;

    @Inject(method = "render", at = @At("HEAD"), remap = false)
    private void adachireiNui$updateSplash(GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
            CallbackInfo callback) {
        if (adachireiNui$customSplashApplied || !AdachireiConfig.get().enableCustomSplash) {
            return;
        }

        String apiText = AdachireiSplashFetcher.getCachedSplash();
        if (apiText != null) {
            splash = new SplashRenderer(apiText);
            adachireiNui$customSplashApplied = true;
        } else {
            AdachireiSplashFetcher.fetchAsync();
        }
    }
}
