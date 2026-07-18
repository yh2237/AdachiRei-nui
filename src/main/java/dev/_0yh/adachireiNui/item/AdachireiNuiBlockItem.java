package dev._0yh.adachireiNui.item;

import dev._0yh.adachireiNui.AdachireiNui;
import dev._0yh.adachireiNui.client.render.AdachireiNuiBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.function.Consumer;

public class AdachireiNuiBlockItem extends BlockItem {
    private final ResourceLocation modelId;

    public AdachireiNuiBlockItem(Block block, Properties props, ResourceLocation modelId) {
        super(block, props);
        this.modelId = modelId;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new BlockEntityWithoutLevelRenderer(
                            Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                            Minecraft.getInstance().getEntityModels()) {
                        @Override
                        public void renderByItem(ItemStack stack, ItemDisplayContext mode,
                                PoseStack matrices, MultiBufferSource vertexConsumers,
                                int light, int overlay) {
                            matrices.pushPose();
                            String displayKey = AdachireiNuiBlockEntityRenderer.displayKeyFor(mode);
                            if (displayKey != null) {
                                var model = AdachireiNuiBlockEntityRenderer.getOrLoadModel(modelId);
                                if (model != null) {
                                    AdachireiNuiBlockEntityRenderer.applyDisplayTransform(model, displayKey, matrices);
                                }
                            }
                            matrices.translate(-0.5F, -0.5F, -0.5F);
                            AdachireiNuiBlockEntityRenderer.renderParsedModel(
                                    modelId, Direction.SOUTH, matrices, vertexConsumers, light, overlay);
                            matrices.popPose();
                        }
                    };
                }
                return renderer;
            }
        });
    }
}