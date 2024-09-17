package com.hlysine.create_connected.mixin.itemsilo;

import com.google.common.collect.Multimap;
import com.hlysine.create_connected.content.itemsilo.ItemSiloBlock;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Map;

/**
 * Data fixer for contraptions with corrupted item silo NBT
 */
@Mixin(value = Contraption.class, remap = false)
public class ContraptionMixin {
    @Shadow
    protected Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks;

    @Shadow
    protected Multimap<BlockPos, StructureTemplate.StructureBlockInfo> capturedMultiblocks;

    @Inject(
            at = @At("RETURN"),
            method = "readNBT"
    )
    private void fixNBT(Level world, CompoundTag nbt, boolean spawnData, CallbackInfo ci) {
        for (Map.Entry<BlockPos, StructureTemplate.StructureBlockInfo> entry : blocks.entrySet()) {
            if (!(entry.getValue().state().getBlock() instanceof ItemSiloBlock))
                return;
            if (entry.getValue().nbt().contains("Length") && !NbtUtils.readBlockPos(entry.getValue().nbt().getCompound("Controller")).equals(entry.getKey())) {
                entry.getValue().nbt().put("Controller", NbtUtils.writeBlockPos(entry.getKey()));
            }
        }
        for (Iterator<BlockPos> iterator = capturedMultiblocks.keys().iterator(); iterator.hasNext(); ) {
            BlockPos pos = iterator.next();
            if (capturedMultiblocks.get(pos).stream().noneMatch(info -> info.state().getBlock() instanceof ItemSiloBlock))
                continue;
            if (!blocks.containsKey(pos)) {
                iterator.remove();
                capturedMultiblocks.put(pos, blocks.get(pos));
                continue;
            }
            if (!blocks.get(pos).nbt().contains("Length")) {
                iterator.remove();
                capturedMultiblocks.put(pos, blocks.get(pos));
            }
        }
    }
}
