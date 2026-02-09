package dev._0yh.adachireiNui.client;

import dev._0yh.adachireiNui.AdachireiNui;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;

public class AdachireiNuiClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.putBlocks(
                BlockRenderLayer.CUTOUT,
                AdachireiNui.ADACHI_BLOCK,
                AdachireiNui.ADACHI_BLOCK_2,
                AdachireiNui.ADACHI_BLOCK_3,
                AdachireiNui.ADACHI_BLOCK_5,
                AdachireiNui.ADACHI_BLOCK_6,
                AdachireiNui.ADACHI_BLOCK_7);

        AdachireiSplashFetcher.fetchAsync();
    }
}
