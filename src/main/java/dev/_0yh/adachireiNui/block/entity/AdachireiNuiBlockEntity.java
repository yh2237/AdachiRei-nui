package dev._0yh.adachireiNui.block.entity;

import dev._0yh.adachireiNui.AdachireiNui;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class AdachireiNuiBlockEntity extends BlockEntity {
    public AdachireiNuiBlockEntity(BlockPos pos, BlockState state) {
        super(AdachireiNui.ADACHIREI_NUI_BLOCK_ENTITY_TYPE, pos, state);
    }
}
