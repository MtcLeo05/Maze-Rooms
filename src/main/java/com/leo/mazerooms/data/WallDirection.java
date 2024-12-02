package com.leo.mazerooms.data;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum WallDirection implements StringRepresentable {
    NORTH("north"),
    EAST("south"),
    SOUTH("east"),
    WEST("west");

    public static final Codec<WallDirection> CODEC = StringRepresentable.fromEnum(WallDirection::values);

    public final String name;

    WallDirection(String name) {
        this.name = name;
    }

    public static WallDirection fromName(String name) {
        for (WallDirection dir : values()) {
            if(dir.name.equalsIgnoreCase(name)) return dir;
        }

        return null;
    }

    public static WallDirection fromIndex(int i) {
        i = Math.abs(i % 4);
        return values()[i];
    }

    public WallDirection clockwise() {
        return values()[(this.ordinal() + 1) % 4];
    }

    public WallDirection opposite() {
        return values()[(this.ordinal() + 2) % 4];
    }

    public WallDirection counterClockwise() {
        return values()[(this.ordinal() + 3) % 4];
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
