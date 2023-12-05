package com.hlysine.create_connected.content.overstressclutch;

import com.hlysine.create_connected.CCBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.AbstractEncasedShaftBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public class OverstressClutchBlock extends AbstractEncasedShaftBlock implements IWrenchable, IBE<OverstressClutchBlockEntity> {
    public static final EnumProperty<ClutchState> STATE = EnumProperty.create("state", ClutchState.class);

    public OverstressClutchBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(STATE, ClutchState.COUPLED));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public Class<OverstressClutchBlockEntity> getBlockEntityClass() {
        return OverstressClutchBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends OverstressClutchBlockEntity> getBlockEntityType() {
        return CCBlockEntityTypes.OVERSTRESS_CLUTCH.get();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        withBlockEntityDo(context.getLevel(), context.getClickedPos(), OverstressClutchBlockEntity::unpowerClutch);
        return InteractionResult.SUCCESS;
    }

    public enum ClutchState implements StringRepresentable {
        COUPLED, UNCOUPLING, UNCOUPLED;

        @Override
        public @NotNull String getSerializedName() {
            return Lang.asId(name());
        }
    }
}
