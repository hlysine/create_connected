package com.hlysine.create_connected.content.dashboard;

import com.hlysine.create_connected.ConnectedLang;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.annotations.ClientOnly;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DashboardBlockEntity extends SmartBlockEntity {

    SignText text = new SignText().setColor(DyeColor.WHITE);
    int cycleTimer = 0;
    boolean wasDisplaying;
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

    public void setLine(int line, Component text) {
        this.setText(this.getText().setMessage(line, text));
    }

    public void clearText() {
        SignText text = this.getText();
        for (int i = 0; i < SignText.LINES; i++) {
            text = text.setMessage(i, Component.empty());
        }
        this.setText(text);
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
        for (int i = 0; i < SignText.LINES; i++) {
            Component line = this.text.getMessage(i, false);
            if (line.getString().isEmpty()) continue;
            if (needSpacer)
                status.append("   ");
            status.append(line).withStyle(s -> s.withColor(this.text.getColor().getTextColor()));
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
            if (state.getBlock() instanceof DashboardBlock && state.getValue(DashboardBlock.FACING) == direction.getOpposite() && state.getValue(DashboardBlock.OPEN)) {
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

    @ClientOnly
    private boolean displayStatus() {
        BlockPos seatPos = getSeatPos();
        if (seatPos == null)
            return false;

        Player player = ClientPlayerAccess.getPlayer();
        if (player == null)
            return false;
        if (!player.isPassenger())
            return false;

        Vec3 center = Vec3.atCenterOf(seatPos);
        if (player.distanceToSqr(center) > 1.2)
            return false;
        List<Component> list = getAllDisplays(seatPos);
        if (list == null || list.isEmpty()) return false;

        Component status = list.get((cycleTimer / CYCLE_INTERVAL) % list.size());
        player.displayClientMessage(status, true);
        cycleTimer += LAZY_TICK_RATE;
        return true;
    }

    static void displayOpenStatus(Player player, boolean open) {
        ConnectedLang
                .translate(open ? "dashboard.activate_hud" : "dashboard.deactivate_hud")
                .sendStatus(player);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();

        if (getLevel().isClientSide()) {
            boolean success = displayStatus();
            if (!success && wasDisplaying) {
                Player player = ClientPlayerAccess.getPlayer();
                if (player != null) {
                    if (!getBlockState().getValue(DashboardBlock.OPEN))
                        displayOpenStatus(player, false); // avoid flickering on wrench by displaying the open status instead of empty
                    else
                        player.displayClientMessage(Component.empty(), true);
                }
            }
            wasDisplaying = success;
        }
    }

    @Override
    public void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        SignText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, this.text).result().ifPresent((result) -> {
            tag.put("text", result);
        });
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (tag.contains("text")) {
            SignText.DIRECT_CODEC.parse(NbtOps.INSTANCE, tag.getCompound("text")).result().ifPresent((result) -> {
                this.text = result;
            });
        }
    }
}
