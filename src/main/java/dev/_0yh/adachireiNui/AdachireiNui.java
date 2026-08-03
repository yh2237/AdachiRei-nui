package dev._0yh.adachireiNui;

import dev._0yh.adachireiNui.block.AdachireiNuiBlock;
import dev._0yh.adachireiNui.block.entity.AdachireiNuiBlockEntity;
import dev._0yh.adachireiNui.client.render.AdachireiNuiBlockEntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

@Mod(AdachireiNui.MOD_ID)
public class AdachireiNui {
    // Forge mod ids cannot contain hyphens. Content ids can, so keep the
    // established Fabric namespace to make commands and saved registry ids portable.
    public static final String MOD_ID = "adachirei_nui";
    public static final String CONTENT_NAMESPACE = "adachirei-nui";

    private static final VoxelShape ADACHI_NUI_SHAPE = Shapes.box(0.2, 0, 0.2, 0.8, 0.9, 0.9);

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK.key(), CONTENT_NAMESPACE);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM.key(), CONTENT_NAMESPACE);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), CONTENT_NAMESPACE);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), CONTENT_NAMESPACE);

    public static final ResourceLocation MODEL_ADACHI_NUI = ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "custom/adachi-nui.json");
    public static final ResourceLocation MODEL_ADACHI_NUI_NEW = ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "custom/adachi-nui_new.json");
    public static final ResourceLocation MODEL_ADACHI_NUI_VOCALOID = ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "custom/adachi-nui_vocaloid.json");

    public static final Supplier<Block> ADACHI_BLOCK_4 = registerBlock("adachirei-nui_box", Shapes.block(), false);
    public static final Supplier<Block> ADACHI_BLOCK_5 = registerBlock(
            "adachi-nui", ADACHI_NUI_SHAPE, true);
    public static final Supplier<Block> ADACHI_BLOCK_6 = registerBlock(
            "adachi-nui_new", ADACHI_NUI_SHAPE, true);
    public static final Supplier<Block> ADACHI_BLOCK_7 = registerBlock(
            "adachi-nui_vocaloid", ADACHI_NUI_SHAPE, true);

    public static final Supplier<BlockEntityType<AdachireiNuiBlockEntity>> ADACHIREI_NUI_BLOCK_ENTITY_TYPE = BLOCK_ENTITIES.register("adachirei_nui",
            () -> new BlockEntityType<>(AdachireiNuiBlockEntity::new,
                    Set.of(ADACHI_BLOCK_5.get(), ADACHI_BLOCK_6.get(), ADACHI_BLOCK_7.get())));

    public static final Supplier<CreativeModeTab> ADACHIREI_ITEM_GROUP = CREATIVE_MODE_TABS.register("item_group",
            () -> CreativeModeTab.builder()
                    .title(net.minecraft.network.chat.Component.translatable("itemGroup." + CONTENT_NAMESPACE))
                    .icon(() -> new ItemStack(ADACHI_BLOCK_5.get()))
                    .displayItems((params, output) -> {
                        output.accept(ADACHI_BLOCK_4.get());
                        output.accept(ADACHI_BLOCK_5.get());
                        output.accept(ADACHI_BLOCK_6.get());
                        output.accept(ADACHI_BLOCK_7.get());
                    })
                    .build());

    public AdachireiNui(IEventBus modBus, ModContainer modContainer) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        CREATIVE_MODE_TABS.register(modBus);

        dev._0yh.adachireiNui.config.AdachireiConfig.init(modContainer);

        modBus.addListener(this::commonSetup);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modContainer.registerExtensionPoint(
                    IConfigScreenFactory.class,
                    (IConfigScreenFactory) (container, parent) ->
                            new dev._0yh.adachireiNui.client.AdachireiConfigScreen(parent));
            modBus.addListener(this::clientSetup);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        dev._0yh.adachireiNui.client.AdachireiNuiClient.onClientSetup(event);
    }

    private static Supplier<Block> registerBlock(String name, VoxelShape shape, boolean renderAsBlockEntity) {
        Supplier<Block> block = BLOCKS.register(name, () -> new AdachireiNuiBlock(shape, renderAsBlockEntity));
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

}
