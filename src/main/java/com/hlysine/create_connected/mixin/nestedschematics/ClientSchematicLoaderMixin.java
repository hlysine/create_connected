package com.hlysine.create_connected.mixin.nestedschematics;

import com.hlysine.create_connected.config.CServer;
import com.simibubi.create.content.schematics.client.ClientSchematicLoader;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

@Mixin(value = ClientSchematicLoader.class, remap = false)
public class ClientSchematicLoaderMixin {
    @Shadow
    private List<Component> availableSchematics;

    @Inject(
            at = @At(value = "INVOKE", target = "Ljava/util/List;sort(Ljava/util/Comparator;)V"),
            method = "refresh()V"
    )
    private void cc$refresh(CallbackInfo ci) {
        cc$searchInSubfolder("schematics/", 0);
    }

    @Unique
    private void cc$searchInSubfolder(String folder, int depth) {
        try {
            boolean canRecurse = depth < CServer.SchematicsNestingDepth.get();
            Path base = Path.of("schematics/");
            Files.list(Path.of(folder))
                    .forEach(path -> {
                        if (Files.isDirectory(path)) {
                            if (canRecurse && (depth != 0 || !path.getFileName().toString().equals("uploaded")))
                                cc$searchInSubfolder(path.toString(), depth + 1);
                        } else if (depth != 0 && path.getFileName().toString().endsWith(".nbt")) {
                            availableSchematics.add(Components.literal(base.relativize(path).toString().replace('\\', '/')));
                        }
                    });
        } catch (NoSuchFileException e) {
            // No Schematics created yet
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
