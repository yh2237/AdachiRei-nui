package dev._0yh.adachireiNui.item;

import dev._0yh.adachireiNui.client.render.AdachireiNuiBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Vector3f;


public class AdachireiNuiBlockItem extends BlockItem {
    private static final Vector3f OFFSET_GUI = new Vector3f(1.65F, 0.5F, 0.5F);
    private static final Vector3f OFFSET_FIRST_PERSON_RIGHT = new Vector3f(1.0F, 0.7F, 0.5F);
    private static final Vector3f OFFSET_FIRST_PERSON_LEFT = new Vector3f(0.25F, 0.7F, 1.6F);
    private static final Vector3f OFFSET_THIRD_PERSON_RIGHT = new Vector3f(0.8F, 2.6F, 0.3F);
    private static final Vector3f OFFSET_THIRD_PERSON_LEFT = new Vector3f(-1.3F, 1.75F, 1.2F);
    private static final Vector3f OFFSET_GROUND = new Vector3f(0.5F, 0.5F, 0.5F);
    private static final Vector3f OFFSET_FIXED = new Vector3f(-1.0F, 0.0F, -1.0F);
    private static final Vector3f OFFSET_HEAD = new Vector3f(-1.05F, 1.0F, -1.0F);

    private static final ItemTransform TRANSFORM_THIRD_PERSON = transform(
            20.93F, -15.16F, -15.41F, 0.0F, 0.0F, 0.0F, 0.3F);
    private static final ItemTransform TRANSFORM_FIRST_PERSON = transform(
            -15.75F, -30.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F);
    private static final ItemTransform TRANSFORM_GROUND = transform(
            0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F);
    private static final ItemTransform TRANSFORM_GUI = transform(
            15.0F, -21.0F, 1.0F, -1.0F, 0.0F, 0.0F, 0.9F);
    private static final ItemTransform TRANSFORM_HEAD = transform(
            0.0F, -180.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.9F);
    private static final ItemTransform TRANSFORM_FIXED = transform(
            0.0F, -180.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);

    private final ResourceLocation modelId;

    public AdachireiNuiBlockItem(Block block, Properties props, ResourceLocation modelId) {
        super(block, props);
        this.modelId = modelId;
    }

    public IClientItemExtensions createClientExtensions() {
        return new IClientItemExtensions() {
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
                            applyBlockbenchDisplayTransform(mode, matrices);
                            applyModelOriginOffset(mode, matrices);
                            AdachireiNuiBlockEntityRenderer.renderParsedModel(
                                    modelId, Direction.SOUTH, matrices, vertexConsumers, light, overlay);
                            matrices.popPose();
                        }
                    };
                }
                return renderer;
            }
        };
    }

    private static ItemTransform transform(float rotationX, float rotationY, float rotationZ,
            float translationX, float translationY, float translationZ, float scale) {
        return new ItemTransform(
                new Vector3f(rotationX, rotationY, rotationZ),
                new Vector3f(translationX, translationY, translationZ),
                new Vector3f(scale, scale, scale));
    }

    private static void applyBlockbenchDisplayTransform(ItemDisplayContext mode, PoseStack matrices) {
        ItemTransform transform = switch (mode) {
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> TRANSFORM_THIRD_PERSON;
            case FIRST_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND -> TRANSFORM_FIRST_PERSON;
            case GROUND -> TRANSFORM_GROUND;
            case GUI -> TRANSFORM_GUI;
            case HEAD -> TRANSFORM_HEAD;
            case FIXED -> TRANSFORM_FIXED;
            default -> ItemTransform.NO_TRANSFORM;
        };
        boolean leftHanded = mode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || mode == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
        transform.apply(leftHanded, matrices);
    }

    private static void applyModelOriginOffset(ItemDisplayContext mode, PoseStack matrices) {
        switch (mode) {
            case GUI -> translate(matrices, OFFSET_GUI);
            case FIRST_PERSON_RIGHT_HAND -> translate(matrices, OFFSET_FIRST_PERSON_RIGHT);
            case FIRST_PERSON_LEFT_HAND -> translateMirroredX(matrices, OFFSET_FIRST_PERSON_LEFT);
            case THIRD_PERSON_RIGHT_HAND -> translate(matrices, OFFSET_THIRD_PERSON_RIGHT);
            case THIRD_PERSON_LEFT_HAND -> translateMirroredX(matrices, OFFSET_THIRD_PERSON_LEFT);
            case GROUND -> translate(matrices, OFFSET_GROUND);
            case FIXED -> translate(matrices, OFFSET_FIXED);
            case HEAD, NONE -> translate(matrices, OFFSET_HEAD);
        }
    }

    private static void translate(PoseStack matrices, Vector3f offset) {
        matrices.translate(offset.x, offset.y, offset.z);
    }

    private static void translateMirroredX(PoseStack matrices, Vector3f offset) {
        matrices.translate(-offset.x, offset.y, offset.z);
    }
}
