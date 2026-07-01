$clientTemplate = @"
package dev._0yh.adachireiNui.client;

import dev._0yh.adachireiNui.AdachireiNui;
import dev._0yh.adachireiNui.client.render.AdachireiNuiBlockEntityRenderer;
import dev._0yh.adachireiNui.config.AdachireiConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class AdachireiNuiClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoConfig.register(AdachireiConfig.class, GsonConfigSerializer::new);

        BlockRenderLayerMap.putBlocks(
                BlockRenderLayer.CUTOUT,
                AdachireiNui.ADACHI_BLOCK_5,
                AdachireiNui.ADACHI_BLOCK_6,
                AdachireiNui.ADACHI_BLOCK_7);

        BlockEntityRendererRegistry.register(
                AdachireiNui.ADACHIREI_NUI_BLOCK_ENTITY_TYPE,
                AdachireiNuiBlockEntityRenderer::new);

        if (AdachireiConfig.get().enableCustomSplash) {
            AdachireiSplashFetcher.fetchAsync();
        }
    }
}
"@

$branches = @("fabric/1.21.6","fabric/1.21.7","fabric/1.21.9","fabric/1.21.10","fabric/1.21.11")
$clientPath = "src/client/java/dev/_0yh/adachireiNui/client/AdachireiNuiClient.java"
$rendererPath = "src/client/java/dev/_0yh/adachireiNui/client/render/AdachireiNuiBlockEntityRenderer.java"

foreach ($b in $branches) {
    git checkout $b

    # Write clean client file
    Set-Content $clientPath -Value $clientTemplate -NoNewline

    # Fix renderer: add Vec3d
    $rc = Get-Content $rendererPath -Raw
    $rc = $rc -replace "import net\.minecraft\.util\.math\.RotationAxis;([\r\n]+)", "import net.minecraft.util.math.RotationAxis;`nimport net.minecraft.util.math.Vec3d;`n"
    $rc = $rc -replace "public void render\(AdachireiNuiBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay\) \{", "public void render(AdachireiNuiBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {"
    Set-Content $rendererPath -Value $rc -NoNewline

    git add -A
    git commit -m "Fabric API 0.128.x互換の修正"
    git tag -f "$b-v1.1.3"
    Write-Output "=== Fixed $b ==="
}
