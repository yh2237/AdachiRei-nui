package dev._0yh.adachireiNui.client;

import dev._0yh.adachireiNui.AdachireiNui;
import dev._0yh.adachireiNui.client.render.AdachireiNuiBlockEntityRenderer;
import dev._0yh.adachireiNui.config.AdachireiConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class AdachireiNuiClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoConfig.register(AdachireiConfig.class, GsonConfigSerializer::new);

        BlockEntityRendererRegistry.register(
                AdachireiNui.ADACHIREI_NUI_BLOCK_ENTITY_TYPE,
                AdachireiNuiBlockEntityRenderer::new);

        if (AdachireiConfig.get().enableCustomSplash) {
            AdachireiSplashFetcher.fetchAsync();
        }
    }
}