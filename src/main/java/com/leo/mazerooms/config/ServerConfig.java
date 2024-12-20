package com.leo.mazerooms.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue SPAWN_IN_POOLROOMS;
    public static final ModConfigSpec.IntValue MAX_CHUNK_DISTANCE;

    public static final ModConfigSpec.IntValue DEAD_END_NUMBER;
    public static final ModConfigSpec.IntValue HALLWAY_NUMBER;
    public static final ModConfigSpec.IntValue CORNER_NUMBER;
    public static final ModConfigSpec.IntValue T_ROOM_NUMBER;
    public static final ModConfigSpec.IntValue INTERSECTION_NUMBER;

    static {
        BUILDER.push("Maze Configs");

        BUILDER.push("General");

        SPAWN_IN_POOLROOMS = BUILDER
            .comment("Whether players should be teleported in the Maze when joining the world in the overworld")
            .define("spawnInMaze", true);

        MAX_CHUNK_DISTANCE = BUILDER
            .comment("The max distance between the chunk and the player before it's not considered valid to generate")
            .comment("Do not change this unless you know what you're doing, the config is here mainly to ease testing")
            .defineInRange("maxChunkDistance", 10, 1, Integer.MAX_VALUE);

        BUILDER.pop();
        BUILDER.push("Room Config");

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
