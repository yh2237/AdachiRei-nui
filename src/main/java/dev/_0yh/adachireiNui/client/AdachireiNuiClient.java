package dev._0yh.adachireiNui.client;

import dev._0yh.adachireiNui.AdachireiNui;
import dev._0yh.adachireiNui.client.render.AdachireiNuiBlockEntityRenderer;
import dev._0yh.adachireiNui.config.AdachireiConfig;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class AdachireiNuiClient {

    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Block[] cutoutBlocks = {
                    AdachireiNui.ADACHI_BLOCK_5.get(),
                    AdachireiNui.ADACHI_BLOCK_6.get(),
                    AdachireiNui.ADACHI_BLOCK_7.get()
            };
            for (Block block : cutoutBlocks) {
                ItemBlockRenderTypes.setRenderLayer(block, ChunkSectionLayer.CUTOUT);
            }

            BlockEntityRenderers.register(
                    AdachireiNui.ADACHIREI_NUI_BLOCK_ENTITY_TYPE.get(),
                    AdachireiNuiBlockEntityRenderer::new);

            if (AdachireiConfig.get().enableCustomSplash) {
                AdachireiSplashFetcher.fetchAsync();
            }
        });
    }
}
