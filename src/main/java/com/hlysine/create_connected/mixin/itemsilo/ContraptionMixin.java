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

import java.util.*;

/**
 * Data fixer for contraptions with corrupted item silo NBT
 */
@Mixin(value = Contraption.class, remap = false)
public abstract class ContraptionMixin {
    @Shadow
    protected Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks;

    @Shadow
    protected Multimap<BlockPos, StructureTemplate.StructureBlockInfo> capturedMultiblocks;

    @Shadow
    protected abstract BlockPos toLocalPos(BlockPos globalPos);

    @Inject(
            at = @At("RETURN"),
            method = "readNBT"
    )
    private void fixNBT(Level world, CompoundTag nbt, boolean spawnData, CallbackInfo ci) {
        List<Map.Entry<BlockPos, StructureTemplate.StructureBlockInfo>> toBeReplaced = new ArrayList<>();
        for (Iterator<Map.Entry<BlockPos, StructureTemplate.StructureBlockInfo>> iterator = blocks.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<BlockPos, StructureTemplate.StructureBlockInfo> entry = iterator.next();
            if (!(entry.getValue().state().getBlock() instanceof ItemSiloBlock))
                continue;
            if (!entry.getValue().nbt().contains("Length") && (
                    blocks.get(NbtUtils.readBlockPos(entry.getValue().nbt().getCompound("Controller"))) == null ||
                            !(blocks.get(NbtUtils.readBlockPos(entry.getValue().nbt().getCompound("Controller"))).state().getBlock() instanceof ItemSiloBlock))) {
                entry.getValue().nbt().put("Controller", NbtUtils.writeBlockPos(entry.getKey()));
                entry.getValue().nbt().putInt("Length", 1);
                entry.getValue().nbt().putInt("Size", 1);
                iterator.remove();
                toBeReplaced.add(new AbstractMap.SimpleEntry<>(entry.getKey(), new StructureTemplate.StructureBlockInfo(entry.getKey(), entry.getValue().state().setValue(ItemSiloBlock.LARGE, false), entry.getValue().nbt())));
            }
        }
        for (Map.Entry<BlockPos, StructureTemplate.StructureBlockInfo> entry : toBeReplaced) {
            blocks.put(entry.getKey(), entry.getValue());
        }
        toBeReplaced.clear();
        for (Iterator<Map.Entry<BlockPos, StructureTemplate.StructureBlockInfo>> iterator = capturedMultiblocks.entries().iterator(); iterator.hasNext(); ) {
            Map.Entry<BlockPos, StructureTemplate.StructureBlockInfo> entry = iterator.next();
            if (!(entry.getValue().state().getBlock() instanceof ItemSiloBlock))
                continue;
            if (!blocks.containsKey(entry.getKey()) || !(blocks.get(entry.getKey()).state().getBlock() instanceof ItemSiloBlock)) {
                if (entry.getValue().nbt().contains("Controller")) {
                    iterator.remove();
                    toBeReplaced.add(new AbstractMap.SimpleEntry<>(NbtUtils.readBlockPos(entry.getValue().nbt().getCompound("Controller")), entry.getValue()));
                }
            }
        }
        for (Map.Entry<BlockPos, StructureTemplate.StructureBlockInfo> entry : toBeReplaced) {
            capturedMultiblocks.put(entry.getKey(), entry.getValue());
        }
        toBeReplaced.clear();
        for (BlockPos blockPos : capturedMultiblocks.keySet()) {
            if (!(blocks.get(blockPos).state().getBlock() instanceof ItemSiloBlock))
                continue;
            Collection<StructureTemplate.StructureBlockInfo> parts = capturedMultiblocks.get(blockPos);

            if (parts.size() == 1) {
                StructureTemplate.StructureBlockInfo part = parts.iterator().next();
                if (part.nbt().contains("Length") && part.nbt().getInt("Length") > 1) {
                    part.nbt().putInt("Length", 1);
                    part.nbt().putInt("Size", 1);
                    toBeReplaced.add(new AbstractMap.SimpleEntry<>(blockPos, new StructureTemplate.StructureBlockInfo(part.pos(), part.state().setValue(ItemSiloBlock.LARGE, false), part.nbt())));
                }
            }
        }
        for (Map.Entry<BlockPos, StructureTemplate.StructureBlockInfo> entry : toBeReplaced) {
            capturedMultiblocks.removeAll(entry.getKey());
            capturedMultiblocks.put(entry.getKey(), entry.getValue());
            blocks.put(entry.getValue().pos(), entry.getValue());
        }
    }
}
