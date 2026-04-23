package dev._0yh.adachireiNui;

import dev._0yh.adachireiNui.block.AdachireiNuiBlock;
import dev._0yh.adachireiNui.block.entity.AdachireiNuiBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
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

        public static final Identifier BLOCK_ID_4 = Identifier.of(MOD_ID, "adachirei-nui_box");
        public static final Identifier BLOCK_ID_5 = Identifier.of(MOD_ID, "adachi-nui");
        public static final Identifier BLOCK_ID_6 = Identifier.of(MOD_ID, "adachi-nui_new");
        public static final Identifier BLOCK_ID_7 = Identifier.of(MOD_ID, "adachi-nui_vocaloid");
        public static final Identifier BLOCK_ENTITY_ID = Identifier.of(MOD_ID, "adachirei_nui");
        public static final Identifier ITEM_GROUP_ID = Identifier.of(MOD_ID, "item_group");

        private static final VoxelShape ADACHI_NUI_SHAPE = VoxelShapes.cuboid(0.2, 0, 0.2, 0.8, 0.9, 0.9);

        public static Block ADACHI_BLOCK_4;
        public static Block ADACHI_BLOCK_5;
        public static Block ADACHI_BLOCK_6;
        public static Block ADACHI_BLOCK_7;
        public static BlockEntityType<AdachireiNuiBlockEntity> ADACHIREI_NUI_BLOCK_ENTITY_TYPE;

        @Override
        public void onInitialize() {
                ADACHI_BLOCK_4 = registerBlock(BLOCK_ID_4, VoxelShapes.fullCube(), false);
                ADACHI_BLOCK_5 = registerBlock(BLOCK_ID_5, ADACHI_NUI_SHAPE, true);
                ADACHI_BLOCK_6 = registerBlock(BLOCK_ID_6, ADACHI_NUI_SHAPE, true);
                ADACHI_BLOCK_7 = registerBlock(BLOCK_ID_7, ADACHI_NUI_SHAPE, true);

                ADACHIREI_NUI_BLOCK_ENTITY_TYPE = Registry.register(
                                Registries.BLOCK_ENTITY_TYPE,
                                BLOCK_ENTITY_ID,
                                FabricBlockEntityTypeBuilder.create(
                                                AdachireiNuiBlockEntity::new,
                                                ADACHI_BLOCK_5,
                                                ADACHI_BLOCK_6,
                                                ADACHI_BLOCK_7)
                                                .build());

                Registry.register(Registries.ITEM_GROUP, RegistryKey.of(RegistryKeys.ITEM_GROUP, ITEM_GROUP_ID),
                                FabricItemGroup.builder()
                                                .displayName(Text.translatable("itemGroup." + MOD_ID))
                                                .icon(() -> new ItemStack(ADACHI_BLOCK_5))
                                                .entries((context, entries) -> {
                                                        entries.add(ADACHI_BLOCK_4);
                                                        entries.add(ADACHI_BLOCK_5);
                                                        entries.add(ADACHI_BLOCK_6);
                                                        entries.add(ADACHI_BLOCK_7);
                                                })
                                                .build());
        }

        private static Block registerBlock(Identifier id, VoxelShape shape, boolean renderAsBlockEntity) {
                RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
                Block block = Registry.register(
                                Registries.BLOCK,
                                blockKey,
                                new AdachireiNuiBlock(shape, blockKey, renderAsBlockEntity));

                RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
                Registry.register(
                                Registries.ITEM,
                                itemKey,
                                new BlockItem(block, new Item.Settings().registryKey(itemKey)
                                                .useBlockPrefixedTranslationKey()));

                return block;
        }
}
