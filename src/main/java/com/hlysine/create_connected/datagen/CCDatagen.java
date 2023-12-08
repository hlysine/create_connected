package com.hlysine.create_connected.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hlysine.create_connected.CCTags;
import com.hlysine.create_connected.CreateConnected;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.TagGen;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagEntry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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

        if (event.includeServer()) {
            generator.addProvider(true, new CCStandardRecipes(output));
            ProcessingRecipeGen.registerAll(generator, output);
        }
    }

    private static void addExtraRegistrateData() {
        REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, CCDatagen::genItemTags);

        REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            BiConsumer<String, String> langConsumer = provider::add;

            provideDefaultLang("interface", langConsumer);
            provideDefaultLang("tooltips", langConsumer);
        });
    }

    private static void genItemTags(RegistrateTagsProvider<Item> provIn) {
        CCTagGen.CCTagsProvider<Item> prov = new CCTagGen.CCTagsProvider<>(provIn, Item::builtInRegistryHolder);
        prov.tag(CCTags.AllItemTags.FAN_CATALYST_BLASTING.tag).add(Items.LAVA_BUCKET);
        prov.tag(CCTags.AllItemTags.FAN_CATALYST_SMOKING.tag).add(Items.FLINT_AND_STEEL);
        prov.tag(CCTags.AllItemTags.FAN_CATALYST_SPLASHING.tag).add(Items.WATER_BUCKET);
        prov.tag(CCTags.AllItemTags.FAN_CATALYST_HAUNTING.tag).add(Items.SOUL_SAND, Items.SOUL_SOIL);
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
}

