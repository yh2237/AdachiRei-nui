package dev._0yh.adachireiNui.client;

import dev._0yh.adachireiNui.AdachireiNui;
import dev._0yh.adachireiNui.client.render.AdachireiNuiBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

public class AdachireiNuiClient implements ClientModInitializer {
        private static final Vector3f OFFSET_GUI = new Vector3f(1.65F, 0.5F, 0.5F);
        private static final Vector3f OFFSET_FIRST_PERSON_RIGHT = new Vector3f(1.0F, 0.7F, 0.5F);
        private static final Vector3f OFFSET_FIRST_PERSON_LEFT = new Vector3f(0.25F, 0.7F, 1.6F);
        private static final Vector3f OFFSET_THIRD_PERSON_RIGHT = new Vector3f(0.8F, 2.6F, 0.3F);
        private static final Vector3f OFFSET_THIRD_PERSON_LEFT = new Vector3f(-1.3F, 1.75F, 1.2F);
        private static final Vector3f OFFSET_GROUND = new Vector3f(0.5F, 0.5F, 0.5F);
        private static final Vector3f OFFSET_FIXED = new Vector3f(-1.0F, 0.0F, -1.0F);
        private static final Vector3f OFFSET_HEAD = new Vector3f(-1.05F, 1.0F, -1.0F);

        private static final Transformation TRANSFORM_THIRD_PERSON = new Transformation(
                        new Vector3f(20.93F, -15.16F, -15.41F),
                        new Vector3f(0.0F, 0.0F, 0.0F),
                        new Vector3f(0.3F, 0.3F, 0.3F));
        private static final Transformation TRANSFORM_FIRST_PERSON = new Transformation(
                        new Vector3f(-15.75F, -30.75F, 0.0F),
                        new Vector3f(0F, 0F, 0F),
                        new Vector3f(0.5F, 0.5F, 0.5F));
        private static final Transformation TRANSFORM_GROUND = new Transformation(
                        new Vector3f(0.0F, 0.0F, 0.0F),
                        new Vector3f(0F, 0.0F, 0.0F),
                        new Vector3f(0.5F, 0.5F, 0.5F));
        private static final Transformation TRANSFORM_GUI = new Transformation(
                        new Vector3f(15.0F, -21.0F, 1.0F),
                        new Vector3f(-1.0F, 0.0F, 0.0F),
                        new Vector3f(0.9F, 0.9F, 0.9F));
        private static final Transformation TRANSFORM_HEAD = new Transformation(
                        new Vector3f(0.0F, -180.0F, 0.0F),
                        new Vector3f(0.0F, 0F, 0.0F),
                        new Vector3f(0.9F, 0.9F, 0.9F));
        private static final Transformation TRANSFORM_FIXED = new Transformation(
                        new Vector3f(0.0F, -180.0F, 0.0F),
                        new Vector3f(0.0F, 0.0F, 0.0F),
                        new Vector3f(1.0F, 1.0F, 1.0F));

        @Override
        public void onInitializeClient() {
                BlockRenderLayerMap.INSTANCE.putBlocks(
                                RenderLayer.getCutout(),
                                // AdachireiNui.ADACHI_BLOCK,
                                // AdachireiNui.ADACHI_BLOCK_2,
                                // AdachireiNui.ADACHI_BLOCK_3,
                                AdachireiNui.ADACHI_BLOCK_5,
                                AdachireiNui.ADACHI_BLOCK_6,
                                AdachireiNui.ADACHI_BLOCK_7);

                BlockEntityRendererRegistry.register(
                                AdachireiNui.ADACHIREI_NUI_BLOCK_ENTITY_TYPE,
                                AdachireiNuiBlockEntityRenderer::new);

                BuiltinItemRendererRegistry.INSTANCE.register(AdachireiNui.ADACHI_BLOCK_5,
                                (stack, mode, matrices, vertexConsumers, light, overlay) -> {
                                        renderNuiItem(
                                                        AdachireiNuiBlockEntityRenderer.MODEL_ADACHI_NUI,
                                                        mode,
                                                        matrices,
                                                        vertexConsumers,
                                                        light,
                                                        overlay);
                                });

                BuiltinItemRendererRegistry.INSTANCE.register(AdachireiNui.ADACHI_BLOCK_6,
                                (stack, mode, matrices, vertexConsumers, light, overlay) -> {
                                        renderNuiItem(
                                                        AdachireiNuiBlockEntityRenderer.MODEL_ADACHI_NUI_NEW,
                                                        mode,
                                                        matrices,
                                                        vertexConsumers,
                                                        light,
                                                        overlay);
                                });

                BuiltinItemRendererRegistry.INSTANCE.register(AdachireiNui.ADACHI_BLOCK_7,
                                (stack, mode, matrices, vertexConsumers, light, overlay) -> {
                                        renderNuiItem(
                                                        AdachireiNuiBlockEntityRenderer.MODEL_ADACHI_NUI_VOCALOID,
                                                        mode,
                                                        matrices,
                                                        vertexConsumers,
                                                        light,
                                                        overlay);
                                });

                AdachireiSplashFetcher.fetchAsync();
        }

        private static void renderNuiItem(Identifier modelId,
                        ModelTransformationMode mode,
                        net.minecraft.client.util.math.MatrixStack matrices,
                        net.minecraft.client.render.VertexConsumerProvider vertexConsumers,
                        int light,
                        int overlay) {
                matrices.push();
                applyBlockbenchDisplayTransform(mode, matrices);
                applyModelOriginOffset(mode, matrices);
                AdachireiNuiBlockEntityRenderer.renderParsedModel(modelId, Direction.SOUTH, matrices, vertexConsumers,
                                light, overlay);
                matrices.pop();
        }

        private static void applyModelOriginOffset(ModelTransformationMode mode,
                        net.minecraft.client.util.math.MatrixStack matrices) {
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

        private static void translate(net.minecraft.client.util.math.MatrixStack matrices, Vector3f offset) {
                matrices.translate(offset.x, offset.y, offset.z);
        }

        private static void translateMirroredX(net.minecraft.client.util.math.MatrixStack matrices, Vector3f offset) {
                matrices.translate(-offset.x, offset.y, offset.z);
        }

        private static void applyBlockbenchDisplayTransform(ModelTransformationMode mode,
                        net.minecraft.client.util.math.MatrixStack matrices) {
                Transformation transform = switch (mode) {
                        case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> TRANSFORM_THIRD_PERSON;
                        case FIRST_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND -> TRANSFORM_FIRST_PERSON;
                        case GROUND -> TRANSFORM_GROUND;
                        case GUI -> TRANSFORM_GUI;
                        case HEAD -> TRANSFORM_HEAD;
                        case FIXED -> TRANSFORM_FIXED;
                        default -> Transformation.IDENTITY;
                };
                boolean leftHanded = mode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND
                                || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND;
                transform.apply(leftHanded, matrices);
        }
}
