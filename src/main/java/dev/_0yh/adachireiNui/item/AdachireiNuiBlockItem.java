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
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
                            renderItemModel(mode, matrices);
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

    private static void renderItemModel(ItemDisplayContext mode, PoseStack matrices) {
        boolean leftHand = mode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || mode == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
        float scale = 0.5F;

        switch (mode) {
            case GUI:
                matrices.translate(1.65F, 0.5F, 0.5F);
                matrices.scale(0.5F, 0.5F, 0.5F);
                matrices.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-30));
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                matrices.translate(leftHand ? -1.0F : 1.0F, 0.7F, 0.5F);
                matrices.scale(0.4F, 0.4F, 0.4F);
                matrices.mulPose(com.mojang.math.Axis.YP.rotationDegrees(35));
                break;
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                matrices.translate(leftHand ? -0.8F : 0.8F, 2.0F, 0.3F);
                matrices.scale(0.3F, 0.3F, 0.3F);
                break;
            case GROUND:
                matrices.translate(0.5F, 0.5F, 0.5F);
                matrices.scale(0.3F, 0.3F, 0.3F);
                break;
            case FIXED:
                matrices.translate(-1.0F, 0.0F, -1.0F);
                break;
            case HEAD:
                matrices.translate(-1.05F, 1.0F, -1.0F);
                matrices.scale(0.6F, 0.6F, 0.6F);
                break;
            case NONE:
                matrices.translate(-1.05F, 1.0F, -1.0F);
                break;
        }
    }
}