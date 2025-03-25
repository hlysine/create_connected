package com.hlysine.create_connected.datagen.advancements;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import com.hlysine.create_connected.CreateConnected;
import com.simibubi.create.Create;
import com.tterrag.registrate.util.entry.ItemProviderEntry;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class CCAdvancement implements Awardable {

    static final ResourceLocation BACKGROUND = Create.asResource("textures/gui/advancements.png");
    static final String LANG = "advancement." + CreateConnected.MODID + ".";
    static final String SECRET_SUFFIX = "\n§7(Hidden Advancement)";

    private final Advancement.Builder builder;
    private SimpleCCTrigger builtinTrigger;
    private CCAdvancement parent;

    Advancement datagenResult;

    private final String id;
    private String title;
    private String description;

    public CCAdvancement(String id, UnaryOperator<CCAdvancement.Builder> b) {
        this.builder = Advancement.Builder.advancement();
        this.id = id;

        CCAdvancement.Builder t = new CCAdvancement.Builder();
        b.apply(t);

        if (!t.externalTrigger) {
            builtinTrigger = CCTriggers.addSimple(id + "_builtin");
            builder.addCriterion("0", builtinTrigger.instance());
        }

        builder.display(t.icon, Component.translatable(titleKey()),
                Component.translatable(descriptionKey()).withStyle(s -> s.withColor(0xDBA213)),
                id.equals("root") ? BACKGROUND : null, t.type.frame, t.type.toast, t.type.announce, t.type.hide);

        if (t.type == CCAdvancement.TaskType.SECRET)
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
        Advancement advancement = sp.getServer()
                .getAdvancements()
                .getAdvancement(CreateConnected.asResource(id));
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

    void save(Consumer<Advancement> t) {
        if (parent != null)
            builder.parent(parent.datagenResult);
        datagenResult = builder.save(t, CreateConnected.asResource(id)
                .toString());
    }

    void provideLang(BiConsumer<String, String> consumer) {
        consumer.accept(titleKey(), title);
        consumer.accept(descriptionKey(), description);
    }

    enum TaskType {

        SILENT(FrameType.TASK, false, false, false),
        NORMAL(FrameType.TASK, true, false, false),
        NOISY(FrameType.TASK, true, true, false),
        EXPERT(FrameType.GOAL, true, true, false),
        SECRET(FrameType.GOAL, true, true, true),

        ;

        private final FrameType frame;
        private final boolean toast;
        private final boolean announce;
        private final boolean hide;

        TaskType(FrameType frame, boolean toast, boolean announce, boolean hide) {
            this.frame = frame;
            this.toast = toast;
            this.announce = announce;
            this.hide = hide;
        }
    }

    class Builder {

        private CCAdvancement.TaskType type = CCAdvancement.TaskType.NORMAL;
        private boolean externalTrigger;
        private int keyIndex;
        private ItemStack icon;

        CCAdvancement.Builder special(CCAdvancement.TaskType type) {
            this.type = type;
            return this;
        }

        CCAdvancement.Builder after(CCAdvancement other) {
            CCAdvancement.this.parent = other;
            return this;
        }

        CCAdvancement.Builder icon(ItemProviderEntry<?> item) {
            return icon(item.asStack());
        }

        CCAdvancement.Builder icon(ItemLike item) {
            return icon(new ItemStack(item));
        }

        CCAdvancement.Builder icon(ItemStack stack) {
            icon = stack;
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

        CCAdvancement.Builder whenItemCollected(ItemProviderEntry<?> item) {
            return whenItemCollected(item.asStack()
                    .getItem());
        }

        CCAdvancement.Builder whenItemCollected(ItemLike itemProvider) {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(itemProvider));
        }

        CCAdvancement.Builder whenItemCollected(TagKey<Item> tag) {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance
                    .hasItems(new ItemPredicate(tag, null, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY,
                            EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, null, NbtPredicate.ANY)));
        }

        CCAdvancement.Builder awardedForFree() {
            return externalTrigger(InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[] {}));
        }

        CCAdvancement.Builder externalTrigger(CriterionTriggerInstance trigger) {
            builder.addCriterion(String.valueOf(keyIndex), trigger);
            externalTrigger = true;
            keyIndex++;
            return this;
        }

    }

}
