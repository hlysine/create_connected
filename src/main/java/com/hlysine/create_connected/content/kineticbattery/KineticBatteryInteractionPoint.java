package com.hlysine.create_connected.content.kineticbattery;

import com.hlysine.create_connected.CCBlocks;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class KineticBatteryInteractionPoint extends AllArmInteractionPointTypes.DepositOnlyArmInteractionPoint {
    public KineticBatteryInteractionPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    public ItemStack insert(ItemStack stack, boolean simulate) {
        ItemStack input = stack.copy();
        InteractionResultHolder<ItemStack> res =
                KineticBatteryBlock.tryInsert(cachedState, level, pos, input, false, false, simulate);
        ItemStack remainder = res.getObject();
        if (input.isEmpty()) {
            return remainder;
        } else {
            if (!simulate)
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), remainder);
            return input;
        }
    }

    public static class Type extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return CCBlocks.KINETIC_BATTERY.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new KineticBatteryInteractionPoint(this, level, pos, state);
        }
    }
}
