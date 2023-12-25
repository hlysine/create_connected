package com.hlysine.create_connected.content.itemsilo;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ItemSiloCTBehaviour extends ConnectedTextureBehaviour.Base {

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        Axis vaultBlockAxis = ItemSiloBlock.getVaultBlockAxis(state);
        boolean small = !ItemSiloBlock.isLarge(state);
        if (vaultBlockAxis == null)
            return null;

        if (direction == Direction.DOWN)
            return AllSpriteShifts.VAULT_BOTTOM.get(small);
        if (direction.getAxis() == vaultBlockAxis)
            return AllSpriteShifts.VAULT_FRONT.get(small);

        return AllSpriteShifts.VAULT_TOP.get(small);
    }

    public boolean buildContextForOccludedDirections() {
        return super.buildContextForOccludedDirections();
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos,
                              BlockPos otherPos, Direction face) {
        return state == other && ConnectivityHandler.isConnected(reader, pos, otherPos); //ItemVaultConnectivityHandler.isConnected(reader, pos, otherPos);
    }

}

