package dev._0yh.adachireiNui.client.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev._0yh.adachireiNui.AdachireiNui;
import dev._0yh.adachireiNui.block.AdachireiNuiBlock;
import dev._0yh.adachireiNui.block.entity.AdachireiNuiBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdachireiNuiBlockEntityRenderer implements BlockEntityRenderer<AdachireiNuiBlockEntity, AdachireiNuiRenderState> {
    public static final Identifier MODEL_ADACHI_NUI = Identifier.of(AdachireiNui.MOD_ID, "models/block/adachi-nui.json");
    public static final Identifier MODEL_ADACHI_NUI_NEW = Identifier.of(AdachireiNui.MOD_ID, "models/block/adachi-nui_new.json");
    public static final Identifier MODEL_ADACHI_NUI_VOCALOID = Identifier.of(AdachireiNui.MOD_ID, "models/block/adachi-nui_vocaloid.json");

    private static final Map<Identifier, ParsedModel> MODEL_CACHE = new HashMap<>();

    public AdachireiNuiBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public AdachireiNuiRenderState createRenderState() {
        return new AdachireiNuiRenderState();
    }

    @Override
    public void updateRenderState(AdachireiNuiBlockEntity entity, AdachireiNuiRenderState state, float tickDelta, Vec3d cameraPos, net.minecraft.client.render.command.ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderState.updateBlockEntityRenderState(entity, state, crumblingOverlay);
        state.modelId = modelIdFor(entity.getCachedState().getBlock());
        state.facing = entity.getCachedState().get(AdachireiNuiBlock.FACING);
    }

    @Override
    public void render(AdachireiNuiRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
        if (state.modelId == null) return;
        renderParsedModel(state.modelId, state.facing, matrices, queue, state.lightmapCoordinates);
    }

    public static void renderParsedModel(Identifier modelId, Direction facing, MatrixStack matrices, OrderedRenderCommandQueue queue, int light) {
        ParsedModel model = getOrLoadModel(modelId);
        if (model == null) return;

        matrices.push();
        matrices.translate(0.5F, 0.0F, 0.5F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-horizontalQuarterTurns(facing) * 90.0F));
        matrices.translate(-0.5F, 0.0F, -0.5F);

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

                Identifier textureId = resolveTexture(model, face.textureRef);
                if (textureId == null) continue;

                RenderLayer renderLayer = RenderLayers.entityCutout(textureId);
                Vector3f[] vertices = faceVertices(faceDir, minX, minY, minZ, maxX, maxY, maxZ);
                rotateElementVertices(vertices, element.rotation);

                Vector3f normal = new Vector3f(faceDir.getOffsetX(), faceDir.getOffsetY(), faceDir.getOffsetZ());
                rotateNormal(normal, element.rotation);

                float[] uv0 = faceUv(face.uv, face.rotation, 0);
                float[] uv1 = faceUv(face.uv, face.rotation, 1);
                float[] uv2 = faceUv(face.uv, face.rotation, 2);
                float[] uv3 = faceUv(face.uv, face.rotation, 3);

                queue.submitCustom(matrices, renderLayer, (entry, consumer) -> {
                    Matrix4f position = entry.getPositionMatrix();
                    vertex(consumer, position, entry, vertices[0], uv0[0], uv0[1], light, normal);
                    vertex(consumer, position, entry, vertices[1], uv1[0], uv1[1], light, normal);
                    vertex(consumer, position, entry, vertices[2], uv2[0], uv2[1], light, normal);
                    vertex(consumer, position, entry, vertices[3], uv3[0], uv3[1], light, normal);
                });
            }
        }

        matrices.pop();
    }

    private static Identifier modelIdFor(Block block) {
        if (block == AdachireiNui.ADACHI_BLOCK_5) {
            return MODEL_ADACHI_NUI;
        }
        if (block == AdachireiNui.ADACHI_BLOCK_6) {
            return MODEL_ADACHI_NUI_NEW;
        }
        if (block == AdachireiNui.ADACHI_BLOCK_7) {
            return MODEL_ADACHI_NUI_VOCALOID;
        }
        return null;
    }

    private static ParsedModel getOrLoadModel(Identifier modelId) {
        ParsedModel cached = MODEL_CACHE.get(modelId);
        if (cached != null) {
            return cached;
        }

        var resourceManager = MinecraftClient.getInstance().getResourceManager();
        var optionalResource = resourceManager.getResource(modelId);
        if (optionalResource.isEmpty()) {
            return null;
        }

        try (InputStreamReader reader = new InputStreamReader(optionalResource.get().getInputStream(), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            ParsedModel model = parseModel(root);
            MODEL_CACHE.put(modelId, model);
            return model;
        } catch (IOException e) {
            return null;
        }
    }

    private static ParsedModel parseModel(JsonObject root) {
        Map<String, Identifier> textures = new HashMap<>();
        JsonObject texturesObject = root.getAsJsonObject("textures");
        for (Map.Entry<String, JsonElement> textureEntry : texturesObject.entrySet()) {
            String key = textureEntry.getKey();
            String value = textureEntry.getValue().getAsString();
            if (!value.startsWith("#")) {
                textures.put(key, toDirectTextureId(Identifier.of(value)));
            }
        }

        List<ModelElement> elements = new ArrayList<>();
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
            JsonObject faceObject = elementObject.getAsJsonObject("faces");
            for (Map.Entry<String, JsonElement> faceEntry : faceObject.entrySet()) {
                Direction direction = directionByName(faceEntry.getKey());
                if (direction == null) {
                    continue;
                }

                JsonObject faceData = faceEntry.getValue().getAsJsonObject();
                JsonArray uvArray = faceData.getAsJsonArray("uv");
                float u1 = uvArray.get(0).getAsFloat() / 16.0F;
                float v1 = uvArray.get(1).getAsFloat() / 16.0F;
                float u2 = uvArray.get(2).getAsFloat() / 16.0F;
                float v2 = uvArray.get(3).getAsFloat() / 16.0F;
                String textureRef = faceData.get("texture").getAsString();
                int faceRotation = faceData.has("rotation") ? faceData.get("rotation").getAsInt() : 0;
                faces.put(direction, new ModelFace(textureRef, new float[] { u1, v1, u2, v2 }, faceRotation));
            }

            elements.add(new ModelElement(from, to, rotation, faces));
        }

        return new ParsedModel(textures, elements);
    }

    private static Vector3f[] faceVertices(Direction direction, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return switch (direction) {
            case NORTH -> new Vector3f[] {
                    new Vector3f(maxX, maxY, minZ),
                    new Vector3f(maxX, minY, minZ),
                    new Vector3f(minX, minY, minZ),
                    new Vector3f(minX, maxY, minZ)
            };
            case SOUTH -> new Vector3f[] {
                    new Vector3f(minX, maxY, maxZ),
                    new Vector3f(minX, minY, maxZ),
                    new Vector3f(maxX, minY, maxZ),
                    new Vector3f(maxX, maxY, maxZ)
            };
            case WEST -> new Vector3f[] {
                    new Vector3f(minX, maxY, minZ),
                    new Vector3f(minX, minY, minZ),
                    new Vector3f(minX, minY, maxZ),
                    new Vector3f(minX, maxY, maxZ)
            };
            case EAST -> new Vector3f[] {
                    new Vector3f(maxX, maxY, maxZ),
                    new Vector3f(maxX, minY, maxZ),
                    new Vector3f(maxX, minY, minZ),
                    new Vector3f(maxX, maxY, minZ)
            };
            case UP -> new Vector3f[] {
                    new Vector3f(minX, maxY, minZ),
                    new Vector3f(minX, maxY, maxZ),
                    new Vector3f(maxX, maxY, maxZ),
                    new Vector3f(maxX, maxY, minZ)
            };
            case DOWN -> new Vector3f[] {
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
        float sin = (float) Math.sin(radians);
        float cos = (float) Math.cos(radians);

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
        rotateVector(normal, rotation.axis, (float) Math.sin(radians), (float) Math.cos(radians));
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
        float[][] corners = new float[][] {
                { uv[0], uv[1] },
                { uv[0], uv[3] },
                { uv[2], uv[3] },
                { uv[2], uv[1] }
        };
        int turns = ((rotation % 360) + 360) % 360 / 90;
        int sourceIndex = Math.floorMod(vertexIndex - turns, 4);
        return corners[sourceIndex];
    }

    private static void vertex(VertexConsumer consumer, Matrix4f position, MatrixStack.Entry entry, Vector3f vertex, float u, float v, int light, Vector3f normal) {
        consumer.vertex(position, vertex.x, vertex.y, vertex.z)
                .color(255, 255, 255, 255)
                .texture(u, v)
                .light(light)
                .normal(entry, normal.x, normal.y, normal.z);
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

    private static Identifier resolveTexture(ParsedModel model, String textureRef) {
        String key = textureRef.startsWith("#") ? textureRef.substring(1) : textureRef;
        return model.textures.get(key);
    }

    private static Identifier toDirectTextureId(Identifier modelTextureId) {
        return Identifier.of(modelTextureId.getNamespace(), "textures/" + modelTextureId.getPath() + ".png");
    }

    private static Vector3f readVec3(JsonArray array) {
        return new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
    }

    private enum Axis {
        X,
        Y,
        Z
    }

    private record ParsedModel(Map<String, Identifier> textures, List<ModelElement> elements) {
    }

    private record ModelElement(Vector3f from, Vector3f to, ElementRotation rotation, Map<Direction, ModelFace> faces) {
    }

    private record ElementRotation(Vector3f origin, Axis axis, float angle) {
    }

    private record ModelFace(String textureRef, float[] uv, int rotation) {
    }

}
