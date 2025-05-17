package com.hlysine.create_connected.content.inventoryaccessport;


import com.hlysine.create_connected.ConnectedLang;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class InventoryAccessPortGenerator extends SpecialBlockStateGen {

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return horizontalAngle(state.getValue(InventoryAccessPortBlock.FACING)) + 180;
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                BlockState state) {
        boolean attached = state.getValue(InventoryAccessPortBlock.ATTACHED);
        ResourceLocation path = prov.modLoc("block/inventory_access_port/block_" + ConnectedLang.asId(state.getValue(InventoryAccessPortBlock.TARGET).name()));
        return prov.models().withExistingParent(path + (attached ? "_on" : "_off"), path)
                .texture("level", prov.modLoc("block/inventory_access_port_" + (attached ? "on" : "off")));
    }

}

