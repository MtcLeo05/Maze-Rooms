package com.leo.mazerooms.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue SPAWN_IN_POOLROOMS;

    public static final ForgeConfigSpec.IntValue DEAD_END_NUMBER;
    public static final ForgeConfigSpec.IntValue HALLWAY_NUMBER;
    public static final ForgeConfigSpec.IntValue CORNER_NUMBER;
    public static final ForgeConfigSpec.IntValue T_ROOM_NUMBER;
    public static final ForgeConfigSpec.IntValue INTERSECTION_NUMBER;

    static {
        BUILDER.push("Maze Configs");

        BUILDER.push("General");

        SPAWN_IN_POOLROOMS = BUILDER
            .comment("Whether players should be teleported in the Maze when joining the world in the overworld")
            .define("spawnInMaze", true);

        BUILDER.pop();
        BUILDER.push("General");

        DEAD_END_NUMBER = BUILDER
            .comment("How many dead end room files exists, used when generating a random room")
            .defineInRange("deadEnds", 1, 1, Integer.MAX_VALUE);

        HALLWAY_NUMBER = BUILDER
            .comment("How many hallway room files exists, used when generating a random room")
            .defineInRange("hallways", 1, 1, Integer.MAX_VALUE);

        CORNER_NUMBER = BUILDER
            .comment("How many corner room files exists, used when generating a random room")
            .defineInRange("corners", 1, 1, Integer.MAX_VALUE);

        T_ROOM_NUMBER = BUILDER
            .comment("How many t-room room files exists, used when generating a random room")
            .defineInRange("tRooms", 1, 1, Integer.MAX_VALUE);

        INTERSECTION_NUMBER = BUILDER
            .comment("How many intersection room files exists, used when generating a random room")
            .defineInRange("intersections", 1, 1, Integer.MAX_VALUE);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static int getRoomNumberFromType(int type) {
        return switch (type) {
            default -> throw new IllegalStateException("Unexpected value: " + type);
            case 0 -> DEAD_END_NUMBER.get();
            case 1 -> HALLWAY_NUMBER.get();
            case 2 -> T_ROOM_NUMBER.get();
            case 3 -> INTERSECTION_NUMBER.get();
            case 4 -> CORNER_NUMBER.get();
        };
    }
}
