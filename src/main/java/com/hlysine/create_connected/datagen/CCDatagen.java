package com.hlysine.create_connected.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hlysine.create_connected.CCPonderPlugin;
import com.hlysine.create_connected.CCSoundEvents;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.datagen.advancements.CCAdvancements;
import com.hlysine.create_connected.datagen.recipes.CCStandardRecipes;
import com.hlysine.create_connected.datagen.recipes.ProcessingRecipeGen;
import com.hlysine.create_connected.datagen.recipes.SequencedAssemblyGen;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.simibubi.create.infrastructure.data.GeneratedEntriesProvider;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateDataProvider;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class CCDatagen {

    public static void gatherData(GatherDataEvent event) {
        if (!event.getMods().contains(CreateConnected.MODID)) return;
        addExtraRegistrateData();

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(true, CCSoundEvents.provider(generator));

        generator.addProvider(event.includeServer(), new CCAdvancements(output, lookupProvider));
        generator.addProvider(event.includeServer(), new CCStandardRecipes(output, lookupProvider));
        generator.addProvider(event.includeServer(), new SequencedAssemblyGen(output, lookupProvider));

        if (event.includeServer()) {
            ProcessingRecipeGen.registerAll(generator, output, lookupProvider);
        }

        event.getGenerator().addProvider(true, CreateConnected.getRegistrate().setDataProvider(new RegistrateDataProvider(CreateConnected.getRegistrate(), CreateConnected.MODID, event)));
    }

    private static void addExtraRegistrateData() {
        CCTagGen.addGenerators();

        CreateConnected.getRegistrate().addDataGenerator(ProviderType.LANG, provider -> {
            BiConsumer<String, String> langConsumer = provider::add;

            provideDefaultLang("interface", langConsumer);
            provideDefaultLang("tooltips", langConsumer);
            CCAdvancements.provideLang(langConsumer);
            CCSoundEvents.provideLang(langConsumer);
            providePonderLang(langConsumer);
        });
    }

    private static void provideDefaultLang(String fileName, BiConsumer<String, String> consumer) {
        String path = "assets/create_connected/lang/default/" + fileName + ".json";
        JsonElement jsonElement = FilesHelper.loadJsonResource(path);
        if (jsonElement == null) {
            throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();
            consumer.accept(key, value);
        }
    }

    private static void providePonderLang(BiConsumer<String, String> consumer) {
        // Register this since FMLClientSetupEvent does not run during datagen
        PonderIndex.addPlugin(new CCPonderPlugin());

        PonderIndex.getLangAccess().provideLang(CreateConnected.MODID, consumer);
    }
}

