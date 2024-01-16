package com.hlysine.create_connected.mixin.nestedschematics;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.schematics.SchematicExport;
import com.simibubi.create.foundation.utility.FilesHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(value = SchematicExport.class, remap = false)
@Debug(export = true)
public class SchematicExportMixin {

    @Inject(
            at = @At(value = "INVOKE", target = "Ljava/nio/file/Files;createDirectories(Ljava/nio/file/Path;[Ljava/nio/file/attribute/FileAttribute;)Ljava/nio/file/Path;"),
            method = "saveSchematic(Ljava/nio/file/Path;Ljava/lang/String;ZLnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Lcom/simibubi/create/content/schematics/SchematicExport$SchematicExportResult;"
    )
    private static void createNestedFolders(Path dir,
                                            String fileName,
                                            boolean overwrite,
                                            Level level,
                                            BlockPos first,
                                            BlockPos second,
                                            CallbackInfoReturnable<SchematicExport.SchematicExportResult> cir,
                                            @Local(ordinal = 1) Path file) {
        FilesHelper.createFolderIfMissing(file.getParent().toString());
    }
}
