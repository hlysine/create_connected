package com.hlysine.create_connected.config;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum FeatureCategory implements StringRepresentable {
    KINETIC("All kinetic components, such as gearboxes and crank wheels"),
    REDSTONE("All redstone components, such as linked transmitter and sequenced pulse generator"),
    LOGISTICS("All components related to item and fluid transport"),
    COPYCATS("All copycats (Install Create: Copycats+ to upgrade)"),
    PALETTE("All building palette blocks"),
    ;

    private final String description;

    FeatureCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public static FeatureCategory byName(String name) {
        for (FeatureCategory category : values()) {
            if (category.getSerializedName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return null;
    }
}

