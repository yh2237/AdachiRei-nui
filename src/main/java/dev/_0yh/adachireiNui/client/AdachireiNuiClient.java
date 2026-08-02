package dev._0yh.adachireiNui.client;

import dev._0yh.adachireiNui.AdachireiNui;
import dev._0yh.adachireiNui.client.render.AdachireiNuiBlockEntityRenderer;
import dev._0yh.adachireiNui.config.AdachireiConfig;
import dev._0yh.adachireiNui.item.AdachireiNuiBlockItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@OnlyIn(Dist.CLIENT)
public class AdachireiNuiClient {

    public static void onRegisterClientExtensions(final RegisterClientExtensionsEvent event) {
        for (String name : new String[] {"adachi-nui", "adachi-nui_new", "adachi-nui_vocaloid"}) {
            var item = BuiltInRegistries.ITEM.getValue(ResourceLocation.fromNamespaceAndPath(AdachireiNui.CONTENT_NAMESPACE, name));
            if (item instanceof AdachireiNuiBlockItem customItem) {
                event.registerItem(customItem.createClientExtensions(), customItem);
            }
        }
    }

    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Block[] cutoutBlocks = {
                    AdachireiNui.ADACHI_BLOCK_5.get(),
                    AdachireiNui.ADACHI_BLOCK_6.get(),
                    AdachireiNui.ADACHI_BLOCK_7.get()
            };
            for (Block block : cutoutBlocks) {
                ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
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
