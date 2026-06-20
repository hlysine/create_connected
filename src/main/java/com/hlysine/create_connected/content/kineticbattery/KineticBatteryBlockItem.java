package com.hlysine.create_connected.content.kineticbattery;

import com.hlysine.create_connected.ConnectedLang;
import com.hlysine.create_connected.registries.CCDataComponents;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

import static com.hlysine.create_connected.content.kineticbattery.KineticBatteryBlockEntity.*;

public class KineticBatteryBlockItem extends BlockItem {
    public static final int BAR_COLOR = 0x5555FF;

    public KineticBatteryBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getBatteryLevel(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return (int) Math.round(13.0F * Mth.clamp(getBatteryLevel(stack) / getMaxBatteryLevel(), 0, 1));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    public static double getBatteryLevel(ItemStack stack) {
        return stack.getOrDefault(CCDataComponents.KINETIC_BATTERY_CHARGE, 0.0);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        double batteryLevel = getBatteryLevel(stack);
        ConnectedLang.builder().add(ConnectedLang.translateDirect("battery.charge")
                        .withStyle(ChatFormatting.GRAY)
                        .append(" ")
                        .append(barComponent(0, getCrudeBatteryLevel(batteryLevel, 20), 20)))
                .addTo(tooltipComponents);
        ConnectedLang.builder().space()
                .add(ConnectedLang.number(batteryLevel / 3600 / 20).style(ChatFormatting.BLUE))
                .add(ConnectedLang.text(" / ").style(ChatFormatting.GRAY))
                .add(ConnectedLang.number(getMaxBatteryLevel() / 3600 / 20)
                        .add(Component.literal(" "))
                        .add(ConnectedLang.translate("generic.unit.su_hours"))
                        .style(ChatFormatting.DARK_GRAY))
                .addTo(tooltipComponents);
    }

    public void registerModelOverrides() {
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> KineticBatteryOverrides.registerModelOverridesClient(this));
    }
}