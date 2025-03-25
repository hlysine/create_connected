package com.hlysine.create_connected;

import com.hlysine.create_connected.compat.CopycatsManager;
import com.hlysine.create_connected.compat.Mods;
import com.hlysine.create_connected.config.CCConfigs;
import com.hlysine.create_connected.datagen.CCDatagen;
import com.hlysine.create_connected.datagen.advancements.CCAdvancements;
import com.hlysine.create_connected.datagen.advancements.CCTriggers;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    public CreateConnected() {
        modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        REGISTRATE.registerEventListeners(modEventBus);

        // Register the commonSetup method for mod loading
        modEventBus.addListener(this::commonSetup);
        CCCraftingConditions.register();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        REGISTRATE.setCreativeTab(CCCreativeTabs.MAIN);
        CCSoundEvents.prepare();
        CCBlocks.register();
        CCItems.register();
        CCBlockEntityTypes.register();
        CCCreativeTabs.register(modEventBus);
        CCPackets.registerPackets();

        CCConfigs.register(ModLoadingContext.get());
        CCConfigs.common().register();

        CCInteractionBehaviours.register();
        CCMovementBehaviours.register();
        CCMountedStorageTypes.register();

        if (Mods.COPYCATS.isLoaded())
            forgeEventBus.addListener(CopycatsManager::onLevelTick);

        modEventBus.addListener(EventPriority.LOWEST, CCDatagen::gatherData);
        modEventBus.addListener(CCSoundEvents::register);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CreateConnectedClient.onCtorClient(modEventBus, forgeEventBus));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CCAdvancements.register();
            CCTriggers.register();
        });
    }

    public static CreateRegistrate getRegistrate() {
        return REGISTRATE;
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }
}
