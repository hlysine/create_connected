package com.hlysine.create_connected.content.kineticbattery;


import com.hlysine.create_connected.ConnectedLang;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.PercentOrProgressBarDisplaySource;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class KineticBatteryDisplaySource extends PercentOrProgressBarDisplaySource {

    @Override
    protected String getTranslationKey() {
        return "kinetic_battery";
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    protected Float getProgress(DisplayLinkContext context) {
        BlockEntity entity = context.getSourceBlockEntity();
        if (!(entity instanceof KineticBatteryBlockEntity kbe)) return null;
        return (float) (kbe.getBatteryLevel() / KineticBatteryBlockEntity.getMaxBatteryLevel());
    }

    @Override
    protected MutableComponent formatNumeric(DisplayLinkContext context, Float currentLevel) {
        if (context.sourceConfig().getInt("Mode") == 1)
            return super.formatNumeric(context, currentLevel);
        LangBuilder builder = ConnectedLang.number(Math.round(currentLevel * KineticBatteryBlockEntity.getMaxBatteryLevel() / 3600 / 20));
        if (context.getTargetBlockEntity() instanceof FlapDisplayBlockEntity)
            builder.space();
        return builder.translate("generic.unit.su_hours")
                .component();
    }

    @Override
    protected boolean progressBarActive(DisplayLinkContext context) {
        return context.sourceConfig()
                .getInt("Mode") == 2;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine)
            return;
        builder.addSelectionScrollInput(0, 120,
                (si, l) -> si.forOptions(ConnectedLang.translatedOptions("display_source.kinetic_battery", "number", "percentage", "progress_bar"))
                        .titled(ConnectedLang.translateDirect("display_source.kinetic_battery.display")),
                "Mode");
    }

}

