package com.hlysine.create_connected.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hlysine.create_connected.CCPonders;
import com.hlysine.create_connected.CCSoundEvents;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.datagen.advancements.CCAdvancements;
import com.hlysine.create_connected.datagen.recipes.CCStandardRecipes;
import com.hlysine.create_connected.datagen.recipes.ProcessingRecipeGen;
import com.hlysine.create_connected.datagen.recipes.SequencedAssemblyGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class CCDatagen {

    private static final CreateRegistrate REGISTRATE = CreateConnected.getRegistrate();

    public static void gatherData(GatherDataEvent event) {
        addExtraRegistrateData();

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        if (event.includeClient()) {
            generator.addProvider(true, CCSoundEvents.provider(generator));
        }

        if (event.includeServer()) {
            generator.addProvider(true, new CCAdvancements(output));
            generator.addProvider(true, new CCStandardRecipes(output));
            generator.addProvider(true, new SequencedAssemblyGen(output));
            ProcessingRecipeGen.registerAll(generator, output);
        }
    }

    private static void addExtraRegistrateData() {
        REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
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
        // Register these since FMLClientSetupEvent does not run during datagen
        CCPonders.register();

        PonderLocalization.generateSceneLang();

        PonderLocalization.provideLang(CreateConnected.MODID, consumer);
    }
}

