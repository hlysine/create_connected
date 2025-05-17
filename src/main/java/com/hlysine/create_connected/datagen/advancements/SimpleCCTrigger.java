package com.hlysine.create_connected.datagen.advancements;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SimpleCCTrigger extends CriterionTriggerBase<SimpleCCTrigger.Instance> {

    public SimpleCCTrigger(String id) {
        super(id);
    }

    public void trigger(ServerPlayer player) {
        super.trigger(player, null);
    }

    public SimpleCCTrigger.Instance instance() {
        return new SimpleCCTrigger.Instance();
    }

    @Override
    public Codec<SimpleCCTrigger.Instance> codec() {
        return SimpleCCTrigger.Instance.CODEC;
    }

    public static class Instance extends CriterionTriggerBase.Instance {
        private static final Codec<SimpleCCTrigger.Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(SimpleCCTrigger.Instance::player)
        ).apply(instance, SimpleCCTrigger.Instance::new));

        private final Optional<ContextAwarePredicate> player;

        public Instance() {
            player = Optional.empty();
        }

        public Instance(Optional<ContextAwarePredicate> player) {
            this.player = player;
        }

        @Override
        protected boolean test(@Nullable List<Supplier<Object>> suppliers) {
            return true;
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return player;
        }
    }
}

