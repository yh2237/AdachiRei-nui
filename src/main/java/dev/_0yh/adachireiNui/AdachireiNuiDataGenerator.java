package dev._0yh.adachireiNui;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import dev._0yh.adachireiNui.datagen.AdachireiNuiLootTableProvider;

public class AdachireiNuiDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(AdachireiNuiLootTableProvider::new);
	}
}
