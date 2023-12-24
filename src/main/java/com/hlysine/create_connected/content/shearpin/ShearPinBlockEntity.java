package com.hlysine.create_connected.content.shearpin;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.datagen.advancements.AdvancementBehaviour;
import com.hlysine.create_connected.datagen.advancements.CCAdvancements;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import java.util.List;

public class ShearPinBlockEntity extends BracketedKineticBlockEntity {

    static final int RANDOM_DELAY = 5;

    public ShearPinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void initialize() {
        onKineticUpdate();
        super.initialize();
    }

    private void onKineticUpdate() {
        if (IRotate.StressImpact.isEnabled()) {
            if (isOverStressed()) {
                if (level != null) {
                    level.scheduleTick(getBlockPos(), CCBlocks.SHEAR_PIN.get(), level.random.nextInt(RANDOM_DELAY), TickPriority.EXTREMELY_HIGH);
                }
            }
        }
    }

    @Override
    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        super.updateFromNetwork(maxStress, currentStress, networkSize);
        onKineticUpdate();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        AdvancementBehaviour.registerAwardables(this, behaviours, CCAdvancements.SHEAR_PIN);
    }
}

