package com.hlysine.create_connected.datagen.advancements;

import com.google.common.collect.Sets;
import com.hlysine.create_connected.CCBlocks;
import com.simibubi.create.AllItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static com.hlysine.create_connected.datagen.advancements.CCAdvancement.TaskType.*;

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

    OVERPOWERED_BRAKE = create("overpowered_brake", b -> b.icon(CCBlocks.BRAKE)
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

    private final PackOutput output;

    public CCAdvancements(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        PathProvider pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
        List<CompletableFuture<?>> futures = new ArrayList<>();

        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<Advancement> consumer = (advancement) -> {
            ResourceLocation id = advancement.getId();
            if (!set.add(id))
                throw new IllegalStateException("Duplicate advancement " + id);
            Path path = pathProvider.json(id);
            futures.add(DataProvider.saveStable(cache, advancement.deconstruct()
                    .serializeToJson(), path));
        };

        for (CCAdvancement advancement : ENTRIES)
            advancement.save(consumer);

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Advancements for Create: Connected";
    }

    public static void provideLang(BiConsumer<String, String> consumer) {
        for (CCAdvancement advancement : ENTRIES)
            advancement.provideLang(consumer);
    }

    public static void register() {
    }

}
