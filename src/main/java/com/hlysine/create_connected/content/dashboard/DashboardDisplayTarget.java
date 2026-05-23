package com.hlysine.create_connected.content.dashboard;


import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignText;

import java.util.List;

public class DashboardDisplayTarget extends DisplayTarget {

    @Override
    public void acceptText(int line, List<MutableComponent> text, DisplayLinkContext context) {
        BlockEntity be = context.getTargetBlockEntity();
        if (!(be instanceof DashboardBlockEntity dashboard))
            return;

        boolean changed = false;
        SignText signText = dashboard.getText();
        for (int i = 0; i < text.size() && i + line < 4; i++) {
            if (i == 0)
                reserve(i + line, dashboard, context);
            if (i > 0 && isReserved(i + line, dashboard, context))
                break;

            String content = text.get(i).getString(dashboard.getMaxTextLineWidth());
            signText = signText.setMessage(i + line, Component.literal(content));
            changed = true;
        }

        if (changed) {
            dashboard.setText(signText);
        }
    }

    @Override
    public DisplayTargetStats provideStats(DisplayLinkContext context) {
        return new DisplayTargetStats(4, 15, this);
    }

    @Override
    public boolean requiresComponentSanitization() {
        return true;
    }

}

