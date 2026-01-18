package dev._0yh.adachireiNui.client;

import dev._0yh.adachireiNui.AdachireiNui;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class AdachireiNuiClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(AdachireiNui.ADACHI_BLOCK, RenderLayer.getCutout());
    }
}
