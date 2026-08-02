package dev._0yh.adachireiNui.client.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev._0yh.adachireiNui.AdachireiNui;
import dev._0yh.adachireiNui.block.AdachireiNuiBlock;
import dev._0yh.adachireiNui.block.entity.AdachireiNuiBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AdachireiNuiBlockEntityRenderer implements BlockEntityRenderer<AdachireiNuiBlockEntity> {

    private static final Map<ResourceLocation, ParsedModel> MODEL_CACHE = new HashMap<>();

    public AdachireiNuiBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AdachireiNuiBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        BlockState state = entity.getBlockState();
        Block block = state.getBlock();

        ResourceLocation modelId = modelIdFor(block);
        if (modelId == null) {
            return;
        }

        renderParsedModel(modelId, state.getValue(AdachireiNuiBlock.FACING), matrices, vertexConsumers, light, overlay);
    }

    public static void renderParsedModel(ResourceLocation modelId, Direction facing, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        ParsedModel model = getOrLoadModel(modelId);
        if (model == null) {
            return;
        }

        matrices.pushPose();
        matrices.translate(0.5F, 0.0F, 0.5F);
        matrices.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-horizontalQuarterTurns(facing) * 90.0F));
        matrices.translate(-0.5F, 0.0F, -0.5F);

        PoseStack.Pose entry = matrices.last();
        Matrix4f position = entry.pose();

        for (ModelElement element : model.elements) {
            float minX = element.from.x / 16.0F;
            float minY = element.from.y / 16.0F;
            float minZ = element.from.z / 16.0F;
            float maxX = element.to.x / 16.0F;
            float maxY = element.to.y / 16.0F;
            float maxZ = element.to.z / 16.0F;

            for (Map.Entry<Direction, ModelFace> faceEntry : element.faces.entrySet()) {
                Direction faceDir = faceEntry.getKey();
                ModelFace face = faceEntry.getValue();

                ResourceLocation textureId = resolveTexture(model, face.textureRef);
                if (textureId == null) {
                    continue;
                }

                VertexConsumer consumer = vertexConsumers.getBuffer(RenderType.entityCutout(textureId));
                Vector3f[] vertices = faceVertices(faceDir, minX, minY, minZ, maxX, maxY, maxZ);
                rotateElementVertices(vertices, element.rotation);

                Vector3f normal = new Vector3f(faceDir.getStepX(), faceDir.getStepY(), faceDir.getStepZ());
                rotateNormal(normal, element.rotation);

                float[] uv0 = faceUv(face.uv, face.rotation, 0);
                float[] uv1 = faceUv(face.uv, face.rotation, 1);
                float[] uv2 = faceUv(face.uv, face.rotation, 2);
                float[] uv3 = faceUv(face.uv, face.rotation, 3);
                vertex(consumer, position, entry, vertices[0], uv0[0], uv0[1], light, overlay, normal);
                vertex(consumer, position, entry, vertices[1], uv1[0], uv1[1], light, overlay, normal);
                vertex(consumer, position, entry, vertices[2], uv2[0], uv2[1], light, overlay, normal);
                vertex(consumer, position, entry, vertices[3], uv3[0], uv3[1], light, overlay, normal);
            }
        }

        matrices.popPose();
    }

    private static ResourceLocation modelIdFor(Block block) {
        if (block == AdachireiNui.ADACHI_BLOCK_5.get()) {
            return AdachireiNui.MODEL_ADACHI_NUI;
        }
        if (block == AdachireiNui.ADACHI_BLOCK_6.get()) {
            return AdachireiNui.MODEL_ADACHI_NUI_NEW;
        }
        if (block == AdachireiNui.ADACHI_BLOCK_7.get()) {
            return AdachireiNui.MODEL_ADACHI_NUI_VOCALOID;
        }
        return null;
    }

    public static ParsedModel getOrLoadModel(ResourceLocation modelId) {
        ParsedModel cached = MODEL_CACHE.get(modelId);
        if (cached != null) {
            return cached;
        }

        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        Optional<Resource> optionalResource = resourceManager.getResource(modelId);
        if (optionalResource.isEmpty()) {
            return null;
        }

        try (InputStreamReader reader = new InputStreamReader(optionalResource.get().open(), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            ParsedModel model = parseModel(root);
            MODEL_CACHE.put(modelId, model);
            return model;
        } catch (IOException e) {
            return null;
        }
    }

    private static ParsedModel parseModel(JsonObject root) {
        Map<String, ResourceLocation> textures = new HashMap<>();
        JsonObject texturesObject = root.getAsJsonObject("textures");
        if (texturesObject != null) {
            for (Map.Entry<String, JsonElement> textureEntry : texturesObject.entrySet()) {
                String key = textureEntry.getKey();
                String value = textureEntry.getValue().getAsString();
                if (!value.startsWith("#")) {
                    textures.put(key, toDirectTextureId(ResourceLocation.fromNamespaceAndPath(value.split(":")[0], value.split(":")[1])));
                }
            }
        }

        List<ModelElement> elements = new ArrayList<>();
        if (root.has("elements")) {
            for (JsonElement elementValue : root.getAsJsonArray("elements")) {
                JsonObject elementObject = elementValue.getAsJsonObject();
                Vector3f from = readVec3(elementObject.getAsJsonArray("from"));
                Vector3f to = readVec3(elementObject.getAsJsonArray("to"));

                ElementRotation rotation = null;
                if (elementObject.has("rotation")) {
                    JsonObject rotationObject = elementObject.getAsJsonObject("rotation");
                    rotation = new ElementRotation(
                            readVec3(rotationObject.getAsJsonArray("origin")),
                            Axis.valueOf(rotationObject.get("axis").getAsString().toUpperCase()),
                            rotationObject.get("angle").getAsFloat());
                }

                Map<Direction, ModelFace> faces = new EnumMap<>(Direction.class);
                if (elementObject.has("faces")) {
                    JsonObject faceObject = elementObject.getAsJsonObject("faces");
                    for (Map.Entry<String, JsonElement> faceEntry : faceObject.entrySet()) {
                        Direction direction = directionByName(faceEntry.getKey());
                        if (direction == null) continue;

                        JsonObject faceData = faceEntry.getValue().getAsJsonObject();
                        JsonArray uvArray = faceData.getAsJsonArray("uv");
                        float u1 = uvArray.get(0).getAsFloat() / 16.0F;
                        float v1 = uvArray.get(1).getAsFloat() / 16.0F;
                        float u2 = uvArray.get(2).getAsFloat() / 16.0F;
                        float v2 = uvArray.get(3).getAsFloat() / 16.0F;
                        String textureRef = faceData.get("texture").getAsString();
                        int faceRotation = faceData.has("rotation") ? faceData.get("rotation").getAsInt() : 0;
                        faces.put(direction, new ModelFace(textureRef, new float[]{u1, v1, u2, v2}, faceRotation));
                    }
                }

                elements.add(new ModelElement(from, to, rotation, faces));
            }
        }

        Map<String, DisplayData> display = new HashMap<>();
        if (root.has("display")) {
            JsonObject displayObject = root.getAsJsonObject("display");
            for (Map.Entry<String, JsonElement> entry : displayObject.entrySet()) {
                String key = entry.getKey();
                JsonObject data = entry.getValue().getAsJsonObject();
                Vector3f rotation = data.has("rotation") ? readVec3(data.getAsJsonArray("rotation")) : new Vector3f(0, 0, 0);
                Vector3f translation = data.has("translation") ? readVec3(data.getAsJsonArray("translation")) : new Vector3f(0, 0, 0);
                Vector3f scale = data.has("scale") ? readVec3(data.getAsJsonArray("scale")) : new Vector3f(1, 1, 1);
                display.put(key, new DisplayData(rotation, translation, scale));
            }
        }

        return new ParsedModel(textures, elements, display);
    }

    private static Vector3f[] faceVertices(Direction direction, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return switch (direction) {
            case NORTH -> new Vector3f[]{
                    new Vector3f(maxX, maxY, minZ),
                    new Vector3f(maxX, minY, minZ),
                    new Vector3f(minX, minY, minZ),
                    new Vector3f(minX, maxY, minZ)
            };
            case SOUTH -> new Vector3f[]{
                    new Vector3f(minX, maxY, maxZ),
                    new Vector3f(minX, minY, maxZ),
                    new Vector3f(maxX, minY, maxZ),
                    new Vector3f(maxX, maxY, maxZ)
            };
            case WEST -> new Vector3f[]{
                    new Vector3f(minX, maxY, minZ),
                    new Vector3f(minX, minY, minZ),
                    new Vector3f(minX, minY, maxZ),
                    new Vector3f(minX, maxY, maxZ)
            };
            case EAST -> new Vector3f[]{
                    new Vector3f(maxX, maxY, maxZ),
                    new Vector3f(maxX, minY, maxZ),
                    new Vector3f(maxX, minY, minZ),
                    new Vector3f(maxX, maxY, minZ)
            };
            case UP -> new Vector3f[]{
                    new Vector3f(minX, maxY, minZ),
                    new Vector3f(minX, maxY, maxZ),
                    new Vector3f(maxX, maxY, maxZ),
                    new Vector3f(maxX, maxY, minZ)
            };
            case DOWN -> new Vector3f[]{
                    new Vector3f(minX, minY, maxZ),
                    new Vector3f(minX, minY, minZ),
                    new Vector3f(maxX, minY, minZ),
                    new Vector3f(maxX, minY, maxZ)
            };
        };
    }

    private static void rotateElementVertices(Vector3f[] vertices, ElementRotation rotation) {
        if (rotation == null || rotation.angle == 0.0F) {
            return;
        }

        Vector3f origin = new Vector3f(rotation.origin).div(16.0F);
        float radians = (float) Math.toRadians(rotation.angle);
        float sin = Mth.sin(radians);
        float cos = Mth.cos(radians);

        for (Vector3f vertex : vertices) {
            vertex.sub(origin);
            rotateVector(vertex, rotation.axis, sin, cos);
            vertex.add(origin);
        }
    }

    private static void rotateNormal(Vector3f normal, ElementRotation rotation) {
        if (rotation == null || rotation.angle == 0.0F) {
            return;
        }

        float radians = (float) Math.toRadians(rotation.angle);
        rotateVector(normal, rotation.axis, Mth.sin(radians), Mth.cos(radians));
    }

    private static void rotateVector(Vector3f vector, Axis axis, float sin, float cos) {
        float x = vector.x;
        float y = vector.y;
        float z = vector.z;

        switch (axis) {
            case X -> {
                vector.y = y * cos - z * sin;
                vector.z = y * sin + z * cos;
            }
            case Y -> {
                vector.x = x * cos + z * sin;
                vector.z = -x * sin + z * cos;
            }
            case Z -> {
                vector.x = x * cos - y * sin;
                vector.y = x * sin + y * cos;
            }
        }
    }

    private static float[] faceUv(float[] uv, int rotation, int vertexIndex) {
        float[][] corners = new float[][]{
                {uv[0], uv[1]},
                {uv[0], uv[3]},
                {uv[2], uv[3]},
                {uv[2], uv[1]}
        };
        int turns = ((rotation % 360) + 360) % 360 / 90;
        int sourceIndex = Math.floorMod(vertexIndex - turns, 4);
        return corners[sourceIndex];
    }

    private static void vertex(VertexConsumer consumer, Matrix4f position, PoseStack.Pose entry, Vector3f vertex, float u, float v, int light, int overlay, Vector3f normal) {
        consumer.addVertex(position, vertex.x, vertex.y, vertex.z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(overlay == 0 ? OverlayTexture.NO_OVERLAY : overlay)
                .setLight(light)
                .setNormal(entry, normal.x, normal.y, normal.z);
    }

    private static Direction directionByName(String name) {
        return switch (name) {
            case "north" -> Direction.NORTH;
            case "south" -> Direction.SOUTH;
            case "west" -> Direction.WEST;
            case "east" -> Direction.EAST;
            case "up" -> Direction.UP;
            case "down" -> Direction.DOWN;
            default -> null;
        };
    }

    private static int horizontalQuarterTurns(Direction direction) {
        return switch (direction) {
            case SOUTH -> 0;
            case WEST -> 1;
            case NORTH -> 2;
            case EAST -> 3;
            default -> 0;
        };
    }

    private static ResourceLocation resolveTexture(ParsedModel model, String textureRef) {
        String key = textureRef.startsWith("#") ? textureRef.substring(1) : textureRef;
        return model.textures.get(key);
    }

    private static ResourceLocation toDirectTextureId(ResourceLocation modelTextureId) {
        return ResourceLocation.fromNamespaceAndPath(modelTextureId.getNamespace(), "textures/" + modelTextureId.getPath() + ".png");
    }

    private static Vector3f readVec3(JsonArray array) {
        return new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
    }

    private enum Axis {
        X, Y, Z
    }

    private record ParsedModel(Map<String, ResourceLocation> textures, List<ModelElement> elements, Map<String, DisplayData> display) {
    }

    private record DisplayData(Vector3f rotation, Vector3f translation, Vector3f scale) {
    }

    public static void applyDisplayTransform(ParsedModel model, String displayKey, PoseStack matrices) {
        if (model.display == null) return;
        DisplayData data = model.display.get(displayKey);
        if (data == null) return;

        matrices.translate(data.translation.x / 16.0F, data.translation.y / 16.0F, data.translation.z / 16.0F);
        matrices.mulPose(com.mojang.math.Axis.XP.rotationDegrees(data.rotation.x()));
        matrices.mulPose(com.mojang.math.Axis.YP.rotationDegrees(data.rotation.y()));
        matrices.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(data.rotation.z()));
        matrices.scale(data.scale.x(), data.scale.y(), data.scale.z());
    }

    public static String displayKeyFor(ItemDisplayContext mode) {
        return switch (mode) {
            case THIRD_PERSON_RIGHT_HAND -> "thirdperson_righthand";
            case THIRD_PERSON_LEFT_HAND -> "thirdperson_lefthand";
            case FIRST_PERSON_RIGHT_HAND -> "firstperson_righthand";
            case FIRST_PERSON_LEFT_HAND -> "firstperson_lefthand";
            case GROUND -> "ground";
            case GUI -> "gui";
            case HEAD -> "head";
            case FIXED -> "fixed";
            default -> null;
        };
    }

    private record ModelElement(Vector3f from, Vector3f to, ElementRotation rotation, Map<Direction, ModelFace> faces) {
    }

    private record ElementRotation(Vector3f origin, Axis axis, float angle) {
    }

    private record ModelFace(String textureRef, float[] uv, int rotation) {
    }
}
