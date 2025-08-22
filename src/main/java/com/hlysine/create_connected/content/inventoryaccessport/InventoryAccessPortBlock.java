package com.hlysine.create_connected.content.inventoryaccessport;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.redstone.DirectedDirectionalBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class InventoryAccessPortBlock extends DirectedDirectionalBlock implements IBE<InventoryAccessPortBlockEntity>, IWrenchable, IBlockExtension {

    public static BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;

    public InventoryAccessPortBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(ATTACHED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(ATTACHED));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        BlockCapability<IItemHandler, Direction> itemCap = Capabilities.ItemHandler.BLOCK;

        Direction preferredFacing = null;
        for (Direction face : context.getNearestLookingDirections()) {
            BlockEntity be = context.getLevel()
                    .getBlockEntity(context.getClickedPos()
                            .relative(face));
            if (be != null && context.getLevel().getCapability(itemCap, be.getBlockPos(), null) != null) {
                preferredFacing = face;
                break;
            }
        }

        if (preferredFacing == null) {
            Direction facing = context.getNearestLookingDirection();
            preferredFacing = context.getPlayer() != null && context.getPlayer()
                    .isShiftKeyDown() ? facing : facing.getOpposite();
        }

        if (preferredFacing.getAxis() == Axis.Y) {
            state = state.setValue(TARGET, preferredFacing == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR);
            preferredFacing = context.getHorizontalDirection();
        }

        return state.setValue(FACING, preferredFacing);
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        withBlockEntityDo(worldIn, pos, InventoryAccessPortBlockEntity::updateConnectedInventory);
    }

    @Override
    public void onNeighborChange(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);
        withBlockEntityDo(level, pos, InventoryAccessPortBlockEntity::updateConnectedInventory);
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState blockState, @NotNull Level worldIn, @NotNull BlockPos pos) {
        if (!blockState.getValue(ATTACHED)) return 0;
        BlockPos targetPos = pos.relative(DirectedDirectionalBlock.getTargetDirection(blockState));
        BlockState targetState = worldIn.getBlockState(targetPos);
        if (targetState.is(this)) return 0;
        return targetState.hasAnalogOutputSignal() ? targetState.getAnalogOutputSignal(worldIn, targetPos) : 0;
    }

    @Override
    public Class<InventoryAccessPortBlockEntity> getBlockEntityClass() {
        return InventoryAccessPortBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends InventoryAccessPortBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.INVENTORY_ACCESS_PORT.get();
    }

}

