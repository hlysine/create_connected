package com.hlysine.create_connected.mixin.brasschute;

import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.chute.ChuteItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChuteBlockEntity.class)
public interface ChuteBlockEntityAccessor {
    @Accessor
    ChuteItemHandler getItemHandler();
}
