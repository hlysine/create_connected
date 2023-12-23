package com.hlysine.create_connected.datagen.advancements;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hlysine.create_connected.CCBlocks;
import com.mojang.logging.LogUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static com.hlysine.create_connected.datagen.advancements.CCAdvancement.TaskType.SECRET;
import static com.hlysine.create_connected.datagen.advancements.CCAdvancement.TaskType.SILENT;

@SuppressWarnings("unused")
public class CCAdvancements implements DataProvider {

    public static final List<CCAdvancement> ENTRIES = new ArrayList<>();
    public static final CCAdvancement START = null,

    /*
     * Some ids have trailing 0's to modify their vertical position on the tree
     * (Advancement ordering seems to be deterministic but hash based)
     */

    ROOT = create("root", b -> b.icon(CCBlocks.PARALLEL_GEARBOX)
            .title("Welcome to Create: Connected")
            .description("Gadgets for all situations")
            .awardedForFree()
            .special(SILENT)),

    SHEAR_PIN = create("shear_pin", b -> b.icon(CCBlocks.SHEAR_PIN)
            .title("Snap!")
            .description("Blow a Shear Pin")
            .after(ROOT)),

    OVERSTRESS_CLUTCH = create("overstress_clutch", b -> b.icon(CCBlocks.OVERSTRESS_CLUTCH)
            .title("Circuit Breaker")
            .description("Trigger an Overstress Clutch")
            .after(SHEAR_PIN)),

    BRASS_GEARBOX = create("brass_gearbox", b -> b.icon(CCBlocks.BRASS_GEARBOX)
            .title("Serious Organization")
            .description("Place down a Brass Gearbox")
            .whenBlockPlaced(CCBlocks.BRASS_GEARBOX.get())
            .after(ROOT)),

    OVERPOWERED_BRAKE = create("overpowered_brake_0", b -> b.icon(CCBlocks.BRAKE)
            .title("Overpowered")
            .description("Keep a network running at speed with a powered brake attached")
            .after(ROOT)
            .special(SECRET)),

    //
    END = null;

    private static CCAdvancement create(String id, UnaryOperator<CCAdvancement.Builder> b) {
        return new CCAdvancement(id, b);
    }

    // Datagen

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
            .create();
    private final DataGenerator generator;

    public CCAdvancements(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    @Override
    public void run(@NotNull HashCache cache) throws IOException {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<Advancement> consumer = (p_204017_3_) -> {
            if (!set.add(p_204017_3_.getId()))
                throw new IllegalStateException("Duplicate advancement " + p_204017_3_.getId());

            Path path1 = getPath(path, p_204017_3_);

            try {
                DataProvider.save(GSON, cache, p_204017_3_.deconstruct()
                        .serializeToJson(), path1);
            } catch (IOException ioexception) {
                LOGGER.error("Couldn't save advancement {}", path1, ioexception);
            }
        };

        for (CCAdvancement advancement : ENTRIES)
            advancement.save(consumer);
    }

    private static Path getPath(Path pathIn, Advancement advancementIn) {
        return pathIn.resolve("data/" + advancementIn.getId()
                .getNamespace() + "/advancements/"
                + advancementIn.getId()
                .getPath()
                + ".json");
    }

    @Override
    public @NotNull String getName() {
        return "Advancements for Create: Connected";
    }

    public static void provideLang(BiConsumer<String, String> consumer) {
        for (CCAdvancement advancement : ENTRIES)
            advancement.provideLang(consumer);
    }

    public static void register() {
    }

}
