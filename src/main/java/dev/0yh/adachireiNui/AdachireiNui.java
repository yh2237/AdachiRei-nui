package dev._0yh.adachireiNui;

import dev._0yh.adachireiNui.block.AdachireiNuiBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class AdachireiNui implements ModInitializer {
    public static final String MOD_ID = "adachirei-nui";

    public static final VoxelShape ADACHI_SHAPE = VoxelShapes.cuboid(0.1, 0, 0.1, 0.9, 0.9, 0.9);
    public static final Block ADACHI_BLOCK = new AdachireiNuiBlock(ADACHI_SHAPE);
    public static final Identifier BLOCK_ID = new Identifier(MOD_ID, "adachirei-nui_block");

    public static final VoxelShape ADACHI_SHAPE_2 = VoxelShapes.cuboid(0.1, 0, 0.1, 0.9, 0.9, 0.9);
    public static final Block ADACHI_BLOCK_2 = new AdachireiNuiBlock(ADACHI_SHAPE_2);
    public static final Identifier BLOCK_ID_2 = new Identifier(MOD_ID, "adachirei-nui_block_smile");

    public static final VoxelShape ADACHI_SHAPE_3 = VoxelShapes.cuboid(0.1, 0, 0.1, 0.9, 0.9, 0.9);
    public static final Block ADACHI_BLOCK_3 = new AdachireiNuiBlock(ADACHI_SHAPE_3);
    public static final Identifier BLOCK_ID_3 = new Identifier(MOD_ID, "adachirei-nui_block_newmodel");

    public static final VoxelShape ADACHI_SHAPE_4 = VoxelShapes.fullCube();
    public static final Block ADACHI_BLOCK_4 = new AdachireiNuiBlock(ADACHI_SHAPE_4);
    public static final Identifier BLOCK_ID_4 = new Identifier(MOD_ID, "adachirei-nui_box");

    @Override
    public void onInitialize() {
        Registry.register(Registries.BLOCK, BLOCK_ID, ADACHI_BLOCK);
        Registry.register(Registries.ITEM, BLOCK_ID, new BlockItem(ADACHI_BLOCK, new FabricItemSettings()));

        Registry.register(Registries.BLOCK, BLOCK_ID_2, ADACHI_BLOCK_2);
        Registry.register(Registries.ITEM, BLOCK_ID_2, new BlockItem(ADACHI_BLOCK_2, new FabricItemSettings()));

        Registry.register(Registries.BLOCK, BLOCK_ID_3, ADACHI_BLOCK_3);
        Registry.register(Registries.ITEM, BLOCK_ID_3, new BlockItem(ADACHI_BLOCK_3, new FabricItemSettings()));

        Registry.register(Registries.BLOCK, BLOCK_ID_4, ADACHI_BLOCK_4);
        Registry.register(Registries.ITEM, BLOCK_ID_4, new BlockItem(ADACHI_BLOCK_4, new FabricItemSettings()));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(ADACHI_BLOCK);
            entries.add(ADACHI_BLOCK_2);
            entries.add(ADACHI_BLOCK_3);
            entries.add(ADACHI_BLOCK_4);
        });
    }
}