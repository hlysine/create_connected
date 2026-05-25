package com.hlysine.create_connected.content.brasschute;

import com.hlysine.create_connected.registries.CCBlockEntityTypes;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.mixin.brasschute.ChuteBlockEntityAccessor;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.chute.ChuteItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = CreateConnected.MODID)
public class BrassChuteBlockEntity extends ChuteBlockEntity {
    public BrassChuteBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected int getExtractionAmount() {
        return 64;
    }

    public ChuteItemHandler itemHandler() {
        return ((ChuteBlockEntityAccessor) this).getItemHandler();
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CCBlockEntityTypes.BRASS_CHUTE.get(),
                (be, context) -> be.itemHandler()
        );
    }
}
