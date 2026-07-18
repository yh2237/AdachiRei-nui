package dev._0yh.adachireiNui;

import dev._0yh.adachireiNui.block.AdachireiNuiBlock;
import dev._0yh.adachireiNui.block.entity.AdachireiNuiBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(AdachireiNui.MOD_ID)
public class AdachireiNui {
    public static final String MOD_ID = "adachirei_nui";

    public static final ResourceLocation BLOCK_ID_4 = new ResourceLocation(MOD_ID, "adachirei-nui_box");
    public static final ResourceLocation BLOCK_ID_5 = new ResourceLocation(MOD_ID, "adachi-nui");
    public static final ResourceLocation BLOCK_ID_6 = new ResourceLocation(MOD_ID, "adachi-nui_new");
    public static final ResourceLocation BLOCK_ID_7 = new ResourceLocation(MOD_ID, "adachi-nui_vocaloid");
    public static final ResourceLocation BLOCK_ENTITY_ID = new ResourceLocation(MOD_ID, "adachirei-nui");

    private static final VoxelShape ADACHI_NUI_SHAPE = Shapes.box(0.2, 0, 0.2, 0.8, 0.9, 0.9);

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), MOD_ID);

    public static Block ADACHI_BLOCK_4;
    public static Block ADACHI_BLOCK_5;
    public static Block ADACHI_BLOCK_6;
    public static Block ADACHI_BLOCK_7;
    public static BlockEntityType<AdachireiNuiBlockEntity> ADACHIREI_NUI_BLOCK_ENTITY_TYPE;

    public AdachireiNui() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        CREATIVE_MODE_TABS.register(modBus);

        ADACHI_BLOCK_4 = registerBlock("adachirei-nui_box", Shapes.block(), false);
        ADACHI_BLOCK_5 = registerBlock("adachi-nui", ADACHI_NUI_SHAPE, true);
        ADACHI_BLOCK_6 = registerBlock("adachi-nui_new", ADACHI_NUI_SHAPE, true);
        ADACHI_BLOCK_7 = registerBlock("adachi-nui_vocaloid", ADACHI_NUI_SHAPE, true);

        BLOCK_ENTITIES.register("adachirei-nui",
                () -> BlockEntityType.Builder.of(AdachireiNuiBlockEntity::new,
                        ADACHI_BLOCK_5, ADACHI_BLOCK_6, ADACHI_BLOCK_7).build(null));

        CREATIVE_MODE_TABS.register("item_group",
                () -> CreativeModeTab.builder()
                        .title(net.minecraft.network.chat.Component.translatable("itemGroup." + MOD_ID))
                        .icon(() -> new ItemStack(ADACHI_BLOCK_5))
                        .displayItems((params, output) -> {
                            output.accept(ADACHI_BLOCK_4);
                            output.accept(ADACHI_BLOCK_5);
                            output.accept(ADACHI_BLOCK_6);
                            output.accept(ADACHI_BLOCK_7);
                        })
                        .build());

        modBus.addListener(this::commonSetup);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modBus.addListener(this::clientSetup);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        dev._0yh.adachireiNui.client.AdachireiNuiClient.onClientSetup(event);
    }

    private static Block registerBlock(String name, VoxelShape shape, boolean renderAsBlockEntity) {
        Block block = new AdachireiNuiBlock(shape, renderAsBlockEntity);
        BLOCKS.register(name, () -> block);
        ITEMS.register(name, () -> new BlockItem(block, new Item.Properties()));
        return block;
    }
}