package com.hlysine.create_connected.mixin;

import com.simibubi.create.content.decoration.copycat.CopycatModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(CopycatModel.class)
public interface CopycatModelInvoker {
    @Invoker(value = "getCroppedQuads", remap = false)
    List<BakedQuad> invokeGetCroppedQuads(BlockState state, Direction side, RandomSource rand,
                                          BlockState material, ModelData wrappedData, RenderType renderType);
}
