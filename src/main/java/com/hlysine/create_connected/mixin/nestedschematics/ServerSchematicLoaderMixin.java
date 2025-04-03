package com.hlysine.create_connected.mixin.nestedschematics;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.config.CServer;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.schematics.ServerSchematicLoader;
import com.simibubi.create.foundation.utility.FilesHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(value = ServerSchematicLoader.class, remap = false)
public abstract class ServerSchematicLoaderMixin {

    @Shadow
    public abstract String getSchematicPath();

    @Unique
    private void deleteEmptyFolders(Path rootPath, Path pathToDelete) throws IOException {
        pathToDelete = pathToDelete.getParent();
        while (!pathToDelete.equals(rootPath) && pathToDelete.toString().contains("schematics")) { // a failsafe to avoid deleting folders outside the schematics folder
            try (Stream<Path> paths = Files.list(pathToDelete)) {
                if (paths.findAny().isEmpty())
                    Files.delete(pathToDelete);
                else break;
            }
            pathToDelete = pathToDelete.getParent();
        }
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/schematics/ServerSchematicLoader;validateSchematicSizeOnServer(Lnet/minecraft/server/level/ServerPlayer;J)Z"),
            method = "handleNewUpload(Lnet/minecraft/server/level/ServerPlayer;Ljava/lang/String;JLnet/minecraft/core/BlockPos;)V",
            cancellable = true
    )
    private void validateNesting(ServerPlayer player,
                                 String schematic,
                                 long size,
                                 BlockPos pos,
                                 CallbackInfo ci,
                                 @Local(ordinal = 2) String playerSchematicId) {
        Path schematicPath = Path.of(schematic);
        if (schematicPath.getNameCount() - 1 > CServer.SchematicsNestingDepth.get()) {
            CreateConnected.LOGGER.warn("Attempted Schematic Upload with too many nested folders: " + playerSchematicId);
            ci.cancel();
        }
    }

    @ModifyExpressionValue(
            at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;count()J"),
            method = "handleNewUpload(Lnet/minecraft/server/level/ServerPlayer;Ljava/lang/String;JLnet/minecraft/core/BlockPos;)V"
    )
    private long countNestedFiles(long original,
                                  @Local(ordinal = 1) String playerPath) throws IOException {
        try (Stream<Path> list = Files.walk(Paths.get(playerPath))) {
            return list.filter(x -> !Files.isDirectory(x)).count();
        }
    }

    @ModifyExpressionValue(
            at = @At(value = "INVOKE", target = "Ljava/nio/file/Files;list(Ljava/nio/file/Path;)Ljava/util/stream/Stream;"),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lcom/simibubi/create/infrastructure/config/CSchematics;maxSchematics:Lnet/createmod/catnip/config/ConfigBase$ConfigInt;"),
                    to = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;")
            ),
            method = "handleNewUpload(Lnet/minecraft/server/level/ServerPlayer;Ljava/lang/String;JLnet/minecraft/core/BlockPos;)V"
    )
    private Stream<Path> listNestedFiles(Stream<Path> original,
                                         @Local(ordinal = 1) String playerPath) throws IOException {
        original.close();
        return Files.walk(Path.of(playerPath));
    }

    @WrapOperation(
            at = @At(value = "INVOKE", target = "Ljava/nio/file/Files;deleteIfExists(Ljava/nio/file/Path;)Z"),
            method = "handleNewUpload(Lnet/minecraft/server/level/ServerPlayer;Ljava/lang/String;JLnet/minecraft/core/BlockPos;)V"
    )
    private boolean deleteEmptyFolders(Path path, Operation<Boolean> original,
                                       @Local(ordinal = 1) String playerPath) throws IOException {
        boolean result = original.call(path);
        deleteEmptyFolders(Path.of(playerPath), path);
        return result;
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Ljava/nio/file/Files;newOutputStream(Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/OutputStream;"),
            method = "handleNewUpload(Lnet/minecraft/server/level/ServerPlayer;Ljava/lang/String;JLnet/minecraft/core/BlockPos;)V"
    )
    private void createNestedFolders(ServerPlayer player,
                                     String schematic,
                                     long size,
                                     BlockPos pos,
                                     CallbackInfo ci,
                                     @Local(ordinal = 1) Path uploadPath) {
        FilesHelper.createFolderIfMissing(uploadPath.getParent().toString());
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Ljava/nio/file/Files;deleteIfExists(Ljava/nio/file/Path;)Z", shift = At.Shift.AFTER),
            method = "cancelUpload(Ljava/lang/String;)V"
    )
    private void deleteEmptyFolders(String playerSchematicId,
                                    CallbackInfo ci) throws IOException {
        deleteEmptyFolders(Path.of(getSchematicPath()), Path.of(getSchematicPath(), playerSchematicId));
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Ljava/lang/String;endsWith(Ljava/lang/String;)Z"),
            method = "handleInstantSchematic(Lnet/minecraft/server/level/ServerPlayer;Ljava/lang/String;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)V",
            cancellable = true
    )
    private void validateNesting(ServerPlayer player,
                                 String schematic,
                                 Level world,
                                 BlockPos pos,
                                 BlockPos bounds,
                                 CallbackInfo ci,
                                 @Local(ordinal = 3) String playerSchematicId) {
        Path schematicPath = Path.of(schematic);
        if (schematicPath.getNameCount() - 1 > CServer.SchematicsNestingDepth.get()) {
            CreateConnected.LOGGER.warn("Attempted Instant Schematic with too many nested folders: " + playerSchematicId);
            ci.cancel();
        }
    }

    @ModifyExpressionValue(
            at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;toList()Ljava/util/List;"),
            method = "tryDeleteOldestSchematic(Ljava/nio/file/Path;)Z"
    )
    private List<Path> countNestedFiles(List<Path> original,
                                        @Local(ordinal = 0) Path dir) throws IOException {
        try (Stream<Path> list = Files.walk(dir)) {
            return list.filter(x -> !Files.isDirectory(x)).collect(Collectors.toList());
        }
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Ljava/nio/file/Files;delete(Ljava/nio/file/Path;)V", shift = At.Shift.AFTER),
            method = "tryDeleteOldestSchematic(Ljava/nio/file/Path;)Z"
    )
    private void deleteEmptyFolders(Path dir,
                                    CallbackInfoReturnable<Boolean> cir,
                                    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
                                    @Local(ordinal = 0) Optional<Path> oldest) throws IOException {
        if (oldest.isPresent()) {
            deleteEmptyFolders(Path.of(getSchematicPath()), oldest.get());
        }
    }
}
