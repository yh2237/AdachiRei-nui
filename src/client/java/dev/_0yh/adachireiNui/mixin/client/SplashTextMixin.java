package dev._0yh.adachireiNui.mixin.client;

import dev._0yh.adachireiNui.client.AdachireiSplashFetcher;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextMixin {

    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("adachirei-nui");

    @Unique
    private static Constructor<SplashTextRenderer> cachedCtor;

    @Unique
    private static boolean usesTextArg;

    @Unique
    private static Method createMethod;

    @Unique
    private static boolean resolved = false;

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void onGet(CallbackInfoReturnable<SplashTextRenderer> cir) {
        String apiText = AdachireiSplashFetcher.consumeCachedSplash();
        if (apiText != null) {
            SplashTextRenderer renderer = createRenderer(apiText);
            if (renderer != null) {
                cir.setReturnValue(renderer);
            }
        }
        AdachireiSplashFetcher.fetchAsync();
    }

    @Unique
    private static SplashTextRenderer createRenderer(String text) {
        try {
            resolve();
            if (cachedCtor == null)
                return null;

            if (usesTextArg) {
                Text styledText = null;
                if (createMethod != null) {
                    styledText = (Text) createMethod.invoke(null, text);
                }
                if (styledText == null) {
                    styledText = Text.literal(text);
                }
                return cachedCtor.newInstance(styledText);
            } else {
                return cachedCtor.newInstance(text);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to create SplashTextRenderer: {}", e.getMessage());
        }
        return null;
    }

    @Unique
    private static void resolve() {
        if (resolved)
            return;
        resolved = true;

        try {
            createMethod = SplashTextResourceSupplier.class.getDeclaredMethod("create", String.class);
            createMethod.setAccessible(true);
        } catch (NoSuchMethodException ignored) {
        }

        try {
            cachedCtor = SplashTextRenderer.class.getDeclaredConstructor(Text.class);
            cachedCtor.setAccessible(true);
            usesTextArg = true;
            return;
        } catch (NoSuchMethodException ignored) {
        }

        try {
            cachedCtor = SplashTextRenderer.class.getDeclaredConstructor(String.class);
            cachedCtor.setAccessible(true);
            usesTextArg = false;
        } catch (NoSuchMethodException e) {
            LOGGER.error("Could not find SplashTextRenderer constructor (String or Text)");
        }
    }
}
