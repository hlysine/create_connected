package com.hlysine.create_connected.datagen.advancements;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.common.util.FakePlayer;

import java.util.*;

public class AdvancementBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<AdvancementBehaviour> TYPE = new BehaviourType<>();

    private UUID playerId;
    private final Set<Awardable> advancements;

    public AdvancementBehaviour(SmartBlockEntity be, Awardable... advancements) {
        super(be);
        this.advancements = new HashSet<>();
        add(advancements);
    }

    public void add(Awardable... advancements) {
        this.advancements.addAll(Arrays.asList(advancements));
    }

    public boolean isOwnerPresent() {
        return playerId != null;
    }

    public void setOwner(UUID id) {
        Player player = getWorld().getPlayerByUUID(id);
        if (player == null)
            return;
        playerId = id;
        removeAwarded();
        blockEntity.setChanged();
    }

    @Override
    public void initialize() {
        super.initialize();
        removeAwarded();
    }

    private void removeAwarded() {
        Player player = getOwner();
        if (player == null)
            return;
        advancements.removeIf(c -> c.isAlreadyAwardedTo(player));
        if (advancements.isEmpty()) {
            playerId = null;
            blockEntity.setChanged();
        }
    }

    public void awardOwnerIfNear(Awardable advancement, int maxDistance) {
        Player player = getOwner();
        if (player == null)
            return;
        if (player.distanceToSqr(Vec3.atCenterOf(getPos())) > maxDistance * maxDistance)
            return;
        award(advancement, player);
    }

    public void awardOwner(Awardable advancement) {
        Player player = getOwner();
        if (player == null)
            return;
        award(advancement, player);
    }

    private void award(Awardable advancement, Player player) {
        if (advancements.contains(advancement))
            advancement.awardTo(player);
        removeAwarded();
    }

    private Player getOwner() {
        if (playerId == null)
            return null;
        return getWorld().getPlayerByUUID(playerId);
    }

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        if (playerId != null)
            nbt.putUUID("Owner", playerId);
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        if (nbt.contains("Owner"))
            playerId = nbt.getUUID("Owner");
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public static void tryAward(BlockGetter reader, BlockPos pos, Awardable advancement) {
        AdvancementBehaviour behaviour = BlockEntityBehaviour.get(reader, pos, AdvancementBehaviour.TYPE);
        if (behaviour != null)
            behaviour.awardOwner(advancement);
    }

    public static void tryAward(BlockEntity be, Awardable advancement) {
        AdvancementBehaviour behaviour = BlockEntityBehaviour.get(be, AdvancementBehaviour.TYPE);
        if (behaviour != null)
            behaviour.awardOwner(advancement);
    }

    public static void trackOwner(Level worldIn, BlockPos pos, LivingEntity placer) {
        AdvancementBehaviour behaviour = BlockEntityBehaviour.get(worldIn, pos, TYPE);
        if (behaviour == null)
            return;
        if (placer instanceof FakePlayer)
            return;
        if (placer instanceof ServerPlayer)
            behaviour.setOwner(placer.getUUID());
    }

    public static void registerAwardables(SmartBlockEntity be, List<BlockEntityBehaviour> behaviours, CCAdvancement... advancements) {
        for (BlockEntityBehaviour behaviour : behaviours) {
            if (behaviour instanceof AdvancementBehaviour ab) {
                ab.add(advancements);
                return;
            }
        }
        behaviours.add(new AdvancementBehaviour(be, advancements));
    }
}
