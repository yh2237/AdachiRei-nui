package dev._0yh.adachireiNui.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.EnumMap;
import java.util.Map;

public class AdachireiNuiBlock extends Block {

    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    private final Map<Direction, VoxelShape> shapes;

    public AdachireiNuiBlock(VoxelShape shape, Identifier id) {
        super(AbstractBlock.Settings.create()
                .strength(0.5f)
                .sounds(BlockSoundGroup.WOOL)
                .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id)));

        this.shapes = generateShapes(shape);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.SOUTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public VoxelShape getOutlineShape(
            BlockState state,
            BlockView world,
            BlockPos pos,
            ShapeContext context) {
        return shapes.get(state.get(FACING));
    }

    private Map<Direction, VoxelShape> generateShapes(VoxelShape shape) {
        Map<Direction, VoxelShape> builder = new EnumMap<>(Direction.class);

        builder.put(Direction.SOUTH, shape);
        builder.put(Direction.NORTH, rotateShape(Direction.SOUTH, Direction.NORTH, shape));
        builder.put(Direction.WEST, rotateShape(Direction.SOUTH, Direction.WEST, shape));
        builder.put(Direction.EAST, rotateShape(Direction.SOUTH, Direction.EAST, shape));

        return ImmutableMap.copyOf(builder);
    }

    private static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = { shape, VoxelShapes.empty() };

        int times = (to.getHorizontalQuarterTurns()
                - from.getHorizontalQuarterTurns()
                + 4) % 4;

        for (int i = 0; i < times; i++) {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.union(
                    buffer[1],
                    VoxelShapes.cuboid(
                            1 - maxZ, minY, minX,
                            1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }
}
