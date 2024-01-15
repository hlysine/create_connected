package com.hlysine.create_connected.mixin;

import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.config.CCConfigs;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.schematics.ServerSchematicLoader;
import com.simibubi.create.foundation.utility.FilesHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

@Mixin(value = ServerSchematicLoader.class, remap = false)
@Debug(export = true)
public class ServerSchematicLoaderMixin {
    // Todo: Also patch instant schematics
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
        if (schematicPath.getNameCount() - 1 > CCConfigs.common().getSchematicsNestingDepth()) {
            CreateConnected.LOGGER.warn("Attempted Schematic Upload with too many nested folders: " + playerSchematicId);
            ci.cancel();
        }
    }

    @Inject(
            at = @At(value = "FIELD", target = "Lcom/simibubi/create/infrastructure/config/CSchematics;maxSchematics:Lcom/simibubi/create/foundation/config/ConfigBase$ConfigInt;"),
            method = "handleNewUpload(Lnet/minecraft/server/level/ServerPlayer;Ljava/lang/String;JLnet/minecraft/core/BlockPos;)V"
    )
    private void countNestedFiles(ServerPlayer player,
                                  String schematic,
                                  long size,
                                  BlockPos pos,
                                  CallbackInfo ci,
                                  @Local(ordinal = 1) String playerPath,
                                  @Local(ordinal = 1) LocalLongRef count) throws IOException {
        try (Stream<Path> list = Files.walk(Paths.get(playerPath))) {
            count.set(list.filter(x -> !Files.isDirectory(x)).count());
        }
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;"),
            method = "handleNewUpload(Lnet/minecraft/server/level/ServerPlayer;Ljava/lang/String;JLnet/minecraft/core/BlockPos;)V"
    )
    private void deleteNestedFiles(ServerPlayer player,
                                   String schematic,
                                   long size,
                                   BlockPos pos,
                                   CallbackInfo ci,
                                   @Local(ordinal = 1) String playerPath,
                                   @Local(ordinal = 0) LocalRef<Stream<Path>> list2) throws IOException {
        list2.get().close();
        list2.set(Files.walk(Paths.get(playerPath)));
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Ljava/nio/file/Files;deleteIfExists(Ljava/nio/file/Path;)Z", shift = At.Shift.AFTER),
            method = "handleNewUpload(Lnet/minecraft/server/level/ServerPlayer;Ljava/lang/String;JLnet/minecraft/core/BlockPos;)V"
    )
    private void deleteEmptyFolders(ServerPlayer player,
                                    String schematic,
                                    long size,
                                    BlockPos pos,
                                    CallbackInfo ci,
                                    @Local(ordinal = 1) String playerPath,
                                    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
                                    @Local(ordinal = 0) Optional<Path> lastFilePath) throws IOException {
        Path root = Path.of(playerPath);
        if (lastFilePath.isPresent()) {
            while (!lastFilePath.get().equals(root)) {
                try (Stream<Path> paths = Files.list(lastFilePath.get())) {
                    if (paths.findAny().isEmpty())
                        Files.delete(lastFilePath.get());
                    else break;
                    lastFilePath = Optional.of(lastFilePath.get().getParent());
                }
            }
        }
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Ljava/nio/file/Files;newOutputStream(Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/OutputStream;"),
            method = "handleNewUpload(Lnet/minecraft/server/level/ServerPlayer;Ljava/lang/String;JLnet/minecraft/core/BlockPos;)V"
    )
    private void uploadNestedFile(ServerPlayer player,
                                  String schematic,
                                  long size,
                                  BlockPos pos,
                                  CallbackInfo ci,
                                  @Local(ordinal = 1) Path uploadPath) {
        FilesHelper.createFolderIfMissing(uploadPath.getParent().toString());
    }
}
