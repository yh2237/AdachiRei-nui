package dev._0yh.adachireiNui;

import dev._0yh.adachireiNui.block.AdachireiNuiBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class AdachireiNui implements ModInitializer {
        public static final String MOD_ID = "adachirei-nui";

        // public static final Identifier BLOCK_ID = Identifier.of(MOD_ID, "adachirei-nui_block");
        // public static final Identifier BLOCK_ID_2 = Identifier.of(MOD_ID, "adachirei-nui_block_smile");
        // public static final Identifier BLOCK_ID_3 = Identifier.of(MOD_ID, "adachirei-nui_block_newmodel");
        public static final Identifier BLOCK_ID_4 = Identifier.of(MOD_ID, "adachirei-nui_box");
        public static final Identifier BLOCK_ID_5 = Identifier.of(MOD_ID, "adachi-nui");
        public static final Identifier BLOCK_ID_6 = Identifier.of(MOD_ID, "adachi-nui_new");
        public static final Identifier BLOCK_ID_7 = Identifier.of(MOD_ID, "adachi-nui_vocaloid");

        public static final Identifier ITEM_GROUP_ID = Identifier.of(MOD_ID, "item_group");

        // public static Block ADACHI_BLOCK;
        // public static Block ADACHI_BLOCK_2;
        // public static Block ADACHI_BLOCK_3;
        public static Block ADACHI_BLOCK_4;
        public static Block ADACHI_BLOCK_5;
        public static Block ADACHI_BLOCK_6;
        public static Block ADACHI_BLOCK_7;

        @Override
        public void onInitialize() {
                // ADACHI_BLOCK = registerBlock(BLOCK_ID, VoxelShapes.cuboid(0.1, 0, 0.1, 0.9, 0.9, 0.9));
                // ADACHI_BLOCK_2 = registerBlock(BLOCK_ID_2, VoxelShapes.cuboid(0.1, 0, 0.1, 0.9, 0.9, 0.9));
                // ADACHI_BLOCK_3 = registerBlock(BLOCK_ID_3, VoxelShapes.cuboid(0.1, 0, 0.1, 0.9, 0.9, 0.9));
                ADACHI_BLOCK_4 = registerBlock(BLOCK_ID_4, VoxelShapes.fullCube());
                ADACHI_BLOCK_5 = registerBlock(BLOCK_ID_5, VoxelShapes.cuboid(0.2, 0, 0.2, 0.8, 0.9, 0.9));
                ADACHI_BLOCK_6 = registerBlock(BLOCK_ID_6, VoxelShapes.cuboid(0.2, 0, 0.2, 0.8, 0.9, 0.9));
                ADACHI_BLOCK_7 = registerBlock(BLOCK_ID_7, VoxelShapes.cuboid(0.2, 0, 0.2, 0.8, 0.9, 0.9));

                Registry.register(Registries.ITEM_GROUP, RegistryKey.of(RegistryKeys.ITEM_GROUP, ITEM_GROUP_ID),
                                FabricItemGroup.builder()
                                                .displayName(Text.translatable("itemGroup." + MOD_ID))
                                                .icon(() -> new ItemStack(ADACHI_BLOCK_5))
                                                .entries((context, entries) -> {
                                                        // entries.add(ADACHI_BLOCK);
                                                        // entries.add(ADACHI_BLOCK_2);
                                                        // entries.add(ADACHI_BLOCK_3);
                                                        entries.add(ADACHI_BLOCK_4);
                                                        entries.add(ADACHI_BLOCK_5);
                                                        entries.add(ADACHI_BLOCK_6);
                                                        entries.add(ADACHI_BLOCK_7);
                                                })
                                                .build());
        }

        private static Block registerBlock(Identifier id, VoxelShape shape) {
                Block block = Registry.register(
                                Registries.BLOCK,
                                id,
                                new AdachireiNuiBlock(shape, id));

                Registry.register(
                                Registries.ITEM,
                                id,
                                new BlockItem(block, new Item.Settings()
                                                .registryKey(RegistryKey.of(RegistryKeys.ITEM, id))
                                                .useBlockPrefixedTranslationKey()));

                return block;
        }
}
