package com.hlysine.create_connected.content.kineticbattery;

import com.hlysine.create_connected.CreateConnected;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class KineticBatteryGenerator extends SpecialBlockStateGen {

    @Override
    protected int getXRotation(BlockState state) {
        Direction dir = state.getValue(KineticBatteryBlock.FACING);
        return dir == Direction.DOWN ? 180
                : dir.getAxis()
                .isHorizontal() ? 90 : 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        Direction dir = state.getValue(KineticBatteryBlock.FACING);
        return dir.getAxis()
                .isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360;
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                BlockState state) {
        int level = state.getValue(KineticBatteryBlock.LEVEL);
        boolean powered = state.getValue(KineticBatteryBlock.POWERED);
        String path = "block/kinetic_battery/block";
        String suffix = level + "_" + (powered ? "discharge" : "charge");
        return prov.models()
                .withExistingParent(path + "_" + suffix, CreateConnected.asResource(path))
                .texture("level", CreateConnected.asResource("block/kinetic_battery/level_" + suffix));
    }

}

