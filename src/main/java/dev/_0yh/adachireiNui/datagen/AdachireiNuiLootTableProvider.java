package dev._0yh.adachireiNui.datagen;

import dev._0yh.adachireiNui.AdachireiNui;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class AdachireiNuiLootTableProvider extends FabricBlockLootTableProvider {
    public AdachireiNuiLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        // addDrop(AdachireiNui.ADACHI_BLOCK);
        // addDrop(AdachireiNui.ADACHI_BLOCK_2);
        // addDrop(AdachireiNui.ADACHI_BLOCK_3);
        addDrop(AdachireiNui.ADACHI_BLOCK_4);
        addDrop(AdachireiNui.ADACHI_BLOCK_5);
        addDrop(AdachireiNui.ADACHI_BLOCK_6);
        addDrop(AdachireiNui.ADACHI_BLOCK_7);
    }
}
