package com.hlysine.create_connected;

import com.hlysine.create_connected.compat.AdditionalPlacementsCompat;
import com.hlysine.create_connected.compat.CopycatsManager;
import com.hlysine.create_connected.compat.Mods;
import com.hlysine.create_connected.config.CCConfigs;
import com.hlysine.create_connected.datagen.CCDatagen;
import com.hlysine.create_connected.datagen.advancements.CCAdvancements;
import com.hlysine.create_connected.datagen.advancements.CCTriggers;
import com.mojang.logging.LogUtils;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateConnected.MODID)
public class CreateConnected {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_connected";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static IEventBus modEventBus;
    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    static {
        REGISTRATE
                .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
                .setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                        .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    public CreateConnected(IEventBus eventBus, ModContainer modContainer) {
        modEventBus = eventBus;
        REGISTRATE.registerEventListeners(modEventBus);

        // Register the commonSetup method for mod loading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onRegister);

        REGISTRATE.setCreativeTab(CCCreativeTabs.MAIN);
        CCSoundEvents.prepare();
        CCBlocks.register();
        CCItems.register();
        CCBlockEntityTypes.register();
        CCCreativeTabs.register(modEventBus);
        CCPackets.register();
        CCCraftingConditions.register(modEventBus);
        CCArmInteractionPointTypes.register(modEventBus);

        CCConfigs.register(modContainer);

        if (Mods.COPYCATS.isLoaded())
            NeoForge.EVENT_BUS.addListener(CopycatsManager::onLevelTick);

        modEventBus.addListener(EventPriority.HIGHEST, CCDatagen::gatherDataHighPriority);
        modEventBus.addListener(EventPriority.LOWEST, CCDatagen::gatherData);
        modEventBus.addListener(CCSoundEvents::register);

        Mods.ADDITIONAL_PLACEMENTS.executeIfInstalled(() -> AdditionalPlacementsCompat::register);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CCInteractionBehaviours.register();
            CCMovementBehaviours.register();
            CCMountedStorageTypes.register();
            CCDisplaySources.register();
        });
    }

    public void onRegister(final RegisterEvent event) {
        if (event.getRegistry() == CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE) {
            CCItemAttributes.register();
        } else if (event.getRegistry() == BuiltInRegistries.TRIGGER_TYPES) {
            CCAdvancements.register();
            CCTriggers.register();
        }
    }

    public static CreateRegistrate getRegistrate() {
        return REGISTRATE;
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
