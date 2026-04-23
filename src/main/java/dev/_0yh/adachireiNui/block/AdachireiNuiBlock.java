package dev._0yh.adachireiNui.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import dev._0yh.adachireiNui.block.entity.AdachireiNuiBlockEntity;

import java.util.EnumMap;
import java.util.Map;

public class AdachireiNuiBlock extends Block implements BlockEntityProvider {

	public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
	private final Map<Direction, VoxelShape> shapes;
	private final boolean renderAsBlockEntity;

	public AdachireiNuiBlock(VoxelShape shape, boolean renderAsBlockEntity) {
		super(AbstractBlock.Settings.create()
				.strength(0.5f)
				.sounds(BlockSoundGroup.WOOL));

		this.shapes = generateShapes(shape);
		this.renderAsBlockEntity = renderAsBlockEntity;
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.SOUTH));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction facing = ctx.getHorizontalPlayerFacing();
		if (this.renderAsBlockEntity) {
			facing = facing.getOpposite();
		}
		return this.getDefaultState().with(FACING, facing);
	}

	@Override
	public VoxelShape getOutlineShape(
			BlockState state,
			BlockView world,
			BlockPos pos,
			ShapeContext context) {
		return shapes.get(shapeDirection(state));
	}

	@Override
	public VoxelShape getCollisionShape(
			BlockState state,
			BlockView world,
			BlockPos pos,
			ShapeContext context) {
		return shapes.get(shapeDirection(state));
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		if (!this.renderAsBlockEntity) {
			return null;
		}
		return new AdachireiNuiBlockEntity(pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return this.renderAsBlockEntity ? BlockRenderType.INVISIBLE : BlockRenderType.MODEL;
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

		int times = (horizontalQuarterTurns(to)
				- horizontalQuarterTurns(from)
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
		Direction direction = state.get(FACING);
		return this.renderAsBlockEntity ? direction.getOpposite() : direction;
	}
}
