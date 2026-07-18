package dev._0yh.adachireiNui.block.entity;

import dev._0yh.adachireiNui.AdachireiNui;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AdachireiNuiBlockEntity extends BlockEntity {
    public AdachireiNuiBlockEntity(BlockPos pos, BlockState state) {
        super(AdachireiNui.ADACHIREI_NUI_BLOCK_ENTITY_TYPE.get(), pos, state);
    }
}