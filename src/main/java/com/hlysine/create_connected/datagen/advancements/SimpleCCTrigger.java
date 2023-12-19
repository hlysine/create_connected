package com.hlysine.create_connected.datagen.advancements;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonObject;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SimpleCCTrigger extends CriterionTriggerBase<SimpleCCTrigger.Instance> {

    public SimpleCCTrigger(String id) {
        super(id);
    }

    @Override
    public SimpleCCTrigger.Instance createInstance(JsonObject json, DeserializationContext context) {
        return new SimpleCCTrigger.Instance(getId());
    }

    public void trigger(ServerPlayer player) {
        super.trigger(player, null);
    }

    public SimpleCCTrigger.Instance instance() {
        return new SimpleCCTrigger.Instance(getId());
    }

    public static class Instance extends CriterionTriggerBase.Instance {

        public Instance(ResourceLocation idIn) {
            super(idIn, ContextAwarePredicate.ANY);
        }

        @Override
        protected boolean test(@Nullable List<Supplier<Object>> suppliers) {
            return true;
        }
    }
}

