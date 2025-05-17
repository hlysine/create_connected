package com.hlysine.create_connected.datagen.advancements;

import com.hlysine.create_connected.CreateConnected;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class CCAdvancement implements Awardable {

    static final ResourceLocation BACKGROUND = Create.asResource("textures/gui/advancements.png");
    static final String LANG = "advancement." + CreateConnected.MODID + ".";
    static final String SECRET_SUFFIX = "\n§7(Hidden Advancement)";

    private final Advancement.Builder mcBuilder = Advancement.Builder.advancement();
    private SimpleCCTrigger builtinTrigger;
    private CCAdvancement parent;
    private final Builder ccBuilder = new Builder();

    AdvancementHolder datagenResult;

    private final String id;
    private String title;
    private String description;

    public CCAdvancement(String id, UnaryOperator<Builder> b) {
        this.id = id;

        b.apply(ccBuilder);

        if (!ccBuilder.externalTrigger) {
            builtinTrigger = CCTriggers.addSimple(id + "_builtin");
            mcBuilder.addCriterion("0", builtinTrigger.createCriterion(builtinTrigger.instance()));
        }

        if (ccBuilder.type == CCAdvancement.TaskType.SECRET)
            description += SECRET_SUFFIX;

        CCAdvancements.ENTRIES.add(this);
    }

    private String titleKey() {
        return LANG + id;
    }

    private String descriptionKey() {
        return titleKey() + ".desc";
    }

    public boolean isAlreadyAwardedTo(Player player) {
        if (!(player instanceof ServerPlayer sp))
            return true;
        AdvancementHolder advancement = sp.getServer()
                .getAdvancements()
                .get(CreateConnected.asResource(id));
        if (advancement == null)
            return true;
        return sp.getAdvancements()
                .getOrStartProgress(advancement)
                .isDone();
    }

    public void awardTo(Player player) {
        if (!(player instanceof ServerPlayer sp))
            return;
        if (builtinTrigger == null)
            throw new UnsupportedOperationException(
                    "Advancement " + id + " uses external Triggers, it cannot be awarded directly");
        builtinTrigger.trigger(sp);
    }

    void save(Consumer<AdvancementHolder> t, HolderLookup.Provider registries) {
        if (parent != null)
            mcBuilder.parent(parent.datagenResult);

        if (ccBuilder.func != null)
            ccBuilder.icon(ccBuilder.func.apply(registries));

        mcBuilder.display(ccBuilder.icon, Component.translatable(titleKey()),
                Component.translatable(descriptionKey()).withStyle(s -> s.withColor(0xDBA213)),
                id.equals("root") ? BACKGROUND : null, ccBuilder.type.advancementType, ccBuilder.type.toast,
                ccBuilder.type.announce, ccBuilder.type.hide);

        datagenResult = mcBuilder.save(t, CreateConnected.asResource(id).toString());
    }

    void provideLang(BiConsumer<String, String> consumer) {
        consumer.accept(titleKey(), title);
        consumer.accept(descriptionKey(), description);
    }

    enum TaskType {

        SILENT(AdvancementType.TASK, false, false, false),
        NORMAL(AdvancementType.TASK, true, false, false),
        NOISY(AdvancementType.TASK, true, true, false),
        EXPERT(AdvancementType.GOAL, true, true, false),
        SECRET(AdvancementType.GOAL, true, true, true),

        ;

        private final AdvancementType advancementType;
        private final boolean toast;
        private final boolean announce;
        private final boolean hide;

        TaskType(AdvancementType advancementType, boolean toast, boolean announce, boolean hide) {
            this.advancementType = advancementType;
            this.toast = toast;
            this.announce = announce;
            this.hide = hide;
        }
    }

    public class Builder {

        private CCAdvancement.TaskType type = CCAdvancement.TaskType.NORMAL;
        private boolean externalTrigger;
        private int keyIndex;
        private ItemStack icon;
        private Function<HolderLookup.Provider, ItemStack> func;

        CCAdvancement.Builder special(CCAdvancement.TaskType type) {
            this.type = type;
            return this;
        }

        CCAdvancement.Builder after(CCAdvancement other) {
            CCAdvancement.this.parent = other;
            return this;
        }

        CCAdvancement.Builder icon(ItemProviderEntry<?, ?> item) {
            return icon(item.asStack());
        }

        CCAdvancement.Builder icon(ItemLike item) {
            return icon(new ItemStack(item));
        }

        CCAdvancement.Builder icon(ItemStack stack) {
            icon = stack;
            return this;
        }

        CCAdvancement.Builder icon(Function<HolderLookup.Provider, ItemStack> func) {
            this.func = func;
            return this;
        }

        CCAdvancement.Builder title(String title) {
            CCAdvancement.this.title = title;
            return this;
        }

        CCAdvancement.Builder description(String description) {
            CCAdvancement.this.description = description;
            return this;
        }

        CCAdvancement.Builder whenBlockPlaced(Block block) {
            return externalTrigger(ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(block));
        }

        CCAdvancement.Builder whenIconCollected() {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(icon.getItem()));
        }

        CCAdvancement.Builder whenItemCollected(ItemProviderEntry<?, ?> item) {
            return whenItemCollected(item.asStack()
                    .getItem());
        }

        CCAdvancement.Builder whenItemCollected(ItemLike itemProvider) {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(itemProvider));
        }

        CCAdvancement.Builder whenItemCollected(TagKey<Item> tag) {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance
                    .hasItems(ItemPredicate.Builder.item().of(tag).build()));
        }

        CCAdvancement.Builder awardedForFree() {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{}));
        }

        CCAdvancement.Builder externalTrigger(Criterion<?> trigger) {
            mcBuilder.addCriterion(String.valueOf(keyIndex), trigger);
            externalTrigger = true;
            keyIndex++;
            return this;
        }

    }

}
