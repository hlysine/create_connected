package com.hlysine.create_connected.content.dashboard;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DashboardBlockEntity extends SmartBlockEntity {

    SignText text = new SignText().setColor(DyeColor.WHITE);
    int cycleTimer = 0;
    private static final int LAZY_TICK_RATE = 4;
    private static final int CYCLE_INTERVAL = 40;

    public DashboardBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(LAZY_TICK_RATE);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public SignText getText() {
        return text;
    }

    public void setText(SignText text) {
        this.text = text;
        notifyUpdate();
    }

    public int getMaxTextLineWidth() {
        return 90;
    }

    public int getTextLineHeight() {
        return 10;
    }

    public @Nullable BlockPos getSeatPos() {
        if (!getBlockState().getValue(DashboardBlock.OPEN))
            return null;
        return getBlockPos().relative(getBlockState().getValue(DashboardBlock.FACING));
    }

    private @Nullable Component getStatusLine() {
        MutableComponent status = Component.empty();
        boolean needSpacer = false;
        for (int i = 0; i < 4; i++) {
            Component line = this.text.getMessage(i, false);
            if (line.getString().isEmpty()) continue;
            if (needSpacer)
                status.append("   ");
            status.append(line).withColor(this.text.getColor().getTextColor());
            needSpacer = true;
        }
        if (!needSpacer)
            return null;
        return status;
    }

    private @Nullable List<Component> getAllDisplays(BlockPos seatPos) {
        List<Component> list = new ArrayList<>(4);
        for (Direction direction : Iterate.horizontalDirections) {
            BlockPos dashboardPos = seatPos.relative(direction);
            if (dashboardPos.equals(getBlockPos())) {
                if (!list.isEmpty()) return null; // one dashboard takes care of displaying status text for all
                Component status = getStatusLine();
                if (status == null) return null;
                list.add(status);
                continue;
            }
            BlockState state = getLevel().getBlockState(dashboardPos);
            if (state.getBlock() instanceof DashboardBlock && state.getValue(DashboardBlock.FACING) == direction.getOpposite()) {
                BlockEntity blockEntity = getLevel().getBlockEntity(dashboardPos);
                if (blockEntity instanceof DashboardBlockEntity dashboard) {
                    Component status = dashboard.getStatusLine();
                    if (status != null)
                        list.add(status);
                }
            }
        }
        return list;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (!getLevel().isClientSide())
            return;
        BlockPos seatPos = getSeatPos();
        if (seatPos == null)
            return;
        List<SeatEntity> entities = getLevel().getEntitiesOfClass(
                SeatEntity.class,
                getRenderBoundingBox().move(seatPos.subtract(getBlockPos())),
                Entity::hasExactlyOnePlayerPassenger
        );
        if (entities.isEmpty())
            return;
        List<Component> list = getAllDisplays(seatPos);
        if (list == null || list.isEmpty()) return;

        Component status = list.get((cycleTimer / CYCLE_INTERVAL) % list.size());
        for (SeatEntity seat : entities) {
            for (Entity passenger : seat.getPassengers()) {
                if (passenger instanceof Player player) {
                    player.displayClientMessage(status, true);
                }
            }
        }
        cycleTimer += LAZY_TICK_RATE;
    }

    @Override
    public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        DynamicOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);
        DataResult<Tag> result = SignText.DIRECT_CODEC.encodeStart(ops, this.text);
        result.result().ifPresent((tagResult) -> tag.put("text", tagResult));
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        DynamicOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);
        if (tag.contains("text")) {
            DataResult<SignText> result = SignText.DIRECT_CODEC.parse(ops, tag.getCompound("text"));
            result.result().ifPresent((signText) -> this.text = signText);
        }
    }
}
