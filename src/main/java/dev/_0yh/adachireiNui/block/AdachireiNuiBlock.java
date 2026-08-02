package dev._0yh.adachireiNui.block;

import com.google.common.collect.ImmutableMap;
import dev._0yh.adachireiNui.block.entity.AdachireiNuiBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

public class AdachireiNuiBlock extends Block implements EntityBlock {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private final Map<Direction, VoxelShape> shapes;
    private final boolean renderAsBlockEntity;

    public AdachireiNuiBlock(VoxelShape shape, boolean renderAsBlockEntity) {
        super(Properties.of()
                .strength(0.5f)
                .sound(SoundType.WOOL));

        this.shapes = generateShapes(shape);
        this.renderAsBlockEntity = renderAsBlockEntity;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction facing = ctx.getHorizontalDirection();
        if (this.renderAsBlockEntity) {
            facing = facing.getOpposite();
        }
        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        return shapes.get(shapeDirection(state));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        return shapes.get(shapeDirection(state));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (!this.renderAsBlockEntity) {
            return null;
        }
        return new AdachireiNuiBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return this.renderAsBlockEntity ? RenderShape.INVISIBLE : RenderShape.MODEL;
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
        VoxelShape[] buffer = { shape, Shapes.empty() };

        int times = (horizontalQuarterTurns(to)
                - horizontalQuarterTurns(from)
                + 4) % 4;

        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.joinUnoptimized(
                    buffer[1],
                    Shapes.box(
                            1 - maxZ, minY, minX,
                            1 - minZ, maxY, maxX),
                    BooleanOp.OR));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    private static int horizontalQuarterTurns(Direction direction) {
        return switch (direction) {
            case SOUTH -> 0;
            case WEST -> 1;
            case NORTH -> 2;
            case EAST -> 3;
            default -> 0;
        };
    }

    private Direction shapeDirection(BlockState state) {
        Direction direction = state.getValue(FACING);
        return this.renderAsBlockEntity ? direction.getOpposite() : direction;
    }
}
