package com.hlysine.create_connected.compat;

import com.firemerald.additionalplacements.generation.IBlockBlacklister;
import com.firemerald.additionalplacements.generation.Registration;
import com.firemerald.additionalplacements.generation.RegistrationInitializer;
import com.hlysine.create_connected.content.copycat.ICopycatWithWrappedBlock;
import com.hlysine.create_connected.content.copycat.IWrappedBlock;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class AdditionalPlacementsCompat {
    public static void register() {
        Registration.addRegistration(new RegistrationInitializer() {
            @Override
            public void addGlobalBlacklisters(Consumer<IBlockBlacklister<Block>> register) {
                register.accept((block, resourceLocation) -> block instanceof CopycatBlock || block instanceof ICopycatWithWrappedBlock || block instanceof IWrappedBlock);
            }
        });
    }
}
