package com.leo.mazerooms.world;

import com.leo.mazerooms.Mazerooms;
import com.leo.mazerooms.config.ServerConfig;
import com.leo.mazerooms.data.MazeData;
import com.leo.mazerooms.data.WallDirection;
import com.leo.mazerooms.util.ListUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RoomHandler {

    public static void handleFutureChunks(LevelChunk current, ServerLevel sLevel, String dimensionName) {
        for (LevelChunk chunk : MazeData.getNearbyChunks(current, sLevel)) {
            if (chunk == null) continue;
            MazeData data = MazeData.getOrCreateData(chunk);
            if (data.generated() && data.isCorner()) continue;
            MazeData[] nearData = MazeData.getNearbyChunkData(chunk, sLevel);

            if(Arrays.stream(nearData).noneMatch(MazeData::generated)) continue;
            handleChunk(chunk, sLevel, dimensionName);
        }
    }

    public static void handleHub(LevelChunk chunk, ServerLevel level, String dimensionName) {
        MazeData data = MazeData.getOrCreateData(chunk);
        if (data.generated()) return;
        placeChunkRoom(chunk, level, new ResourceLocation(Mazerooms.MODID, dimensionName + "/room_hub"));
        MazeData.execute(chunk, (d) -> d.replace(new MazeData(false, ListUtil.of(WallDirection.values()))));
    }

    public static void handleChunk(LevelChunk chunk, ServerLevel level, String dimensionName) {
        MazeData data = MazeData.getOrCreateData(chunk);
        if (data.generated()) return;

        //Check and replace hub room
        if(data.walls().size() >= 4) {
            placeChunkRoom(chunk, level, new ResourceLocation(Mazerooms.MODID, dimensionName + "/room_3_0"));
            MazeData.execute(chunk, (d) -> d.replace(new MazeData(true, ListUtil.of(WallDirection.values()))));
            handleFutureChunks(chunk, level, dimensionName);
            return;
        }

        List<WallDirection> walls = ListUtil.of();

        MazeData[] nearbyData = MazeData.getNearbyChunkData(chunk, level);

        int remove = 0;
        //Check for already opened rooms nearby
        for (int i = 0; i < nearbyData.length; i++) {
            MazeData sideData = nearbyData[i];
            if(!sideData.generated()) continue;
            WallDirection dir = WallDirection.fromIndex(i).opposite();
            if(sideData.hasDirection(dir)) {
                walls.add(WallDirection.fromIndex(i));
                remove++;
            }
        }

        int numberOfPaths = getWeightedRandom(new int[]{0, 1, 2, 3}, new double[]{0, 0.0, 0.75, 0.25}, level.random);
        List<Integer> possibleWallMap = ListUtil.of(0, 1, 2, 3);

        //Randomize new entrances based on how many were already opened
        for (int currentWallIndex = 0; currentWallIndex < numberOfPaths - remove; currentWallIndex++) {
            //If all entrances are open break loop
            if (possibleWallMap.isEmpty()) break;

            //Randomize direction
            int wallToOpen = level.random.nextInt(possibleWallMap.size());
            int wallDir = possibleWallMap.get(wallToOpen);

            WallDirection sideDir = WallDirection.fromIndex(wallDir).opposite();
            boolean isSelectedWallOpen = walls.contains(WallDirection.fromIndex(wallDir));

            //If the selected wall is already open, remove it from the available ones
            if (isSelectedWallOpen) {
                possibleWallMap.remove((Integer) wallDir);
                currentWallIndex--;
                continue;
            }

            boolean chunkGen = nearbyData[currentWallIndex].generated();
            boolean isOppositeWallOpen = nearbyData[currentWallIndex].hasDirection(sideDir);

            //If the nearby chunk is already generated, and the opposite wall to the selected one is closed,
            // remove it from the available ones to avoid ugly dead ends
            if (chunkGen && !isOppositeWallOpen) {
                possibleWallMap.remove((Integer) wallDir);
                currentWallIndex--;
                continue;
            }

            //Open the wall and remove it from the available ones
            walls.add(WallDirection.fromIndex(wallDir));
            possibleWallMap.remove((Integer) wallDir);
        }

        MazeData.execute(chunk, (d) -> d.replace(new MazeData(true, walls)));
        handleChunkRoom(chunk, level, dimensionName);
        handleFutureChunks(chunk, level, dimensionName);
    }

    public static void placeChunkRoom(LevelChunk chunk, ServerLevel level, ResourceLocation room) {
        ChunkPos cPos = chunk.getPos();
        MazeData data = MazeData.getOrCreateData(chunk);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        pos.set(cPos.getMinBlockX(), 0, cPos.getMinBlockZ());

        StructureTemplateManager manager = level.getStructureManager();
        StructureTemplate template = manager.getOrCreate(room);

        BlockPos center = new BlockPos(7, pos.getY(), 7);
        Rotation rot = gerRoomRotation(data);

        BlockPos toUse = pos.immutable();
        BlockPos offset = new BlockPos(0, 0, 0);

        if (!chunk.getPos().equals(new ChunkPos(0, 0))) {
            offset = offset.north(chunk.getPos().z).west(chunk.getPos().x);
            toUse = toUse.offset(offset.getX(), offset.getY(), offset.getZ());
        }

        template.placeInWorld(level, toUse, toUse, new StructurePlaceSettings().setRotationPivot(center).setRotation(rot), level.random, 3);
    }

    public static void handleChunkRoom(LevelChunk chunk, ServerLevel level, String dimensionName) {
        placeChunkRoom(chunk, level, getRoomToPlace(chunk, dimensionName));
    }

    public static Rotation gerRoomRotation(MazeData data) {
        int n = data.walls().size();

        WallDirection start = null;
        for (int i = 0; i < n; i++) {
            if (data.hasDirection(WallDirection.fromIndex(i))) {
                start = WallDirection.fromIndex(i);
                break;
            }
        }

        long exitCount = data.getExitCount();

        //Dead ends
        if (start != null && exitCount == 1) {
            return determineRotation(start);
        }

        // Hallways / Corners
        if (exitCount == 2) {
            if (data.isCorner()) {
                WallDirection dir = data.walls().stream().findFirst().get();

                return data.isLeft() ?
                    determineRotation(dir.counterClockwise()) :
                    determineRotation(dir.clockwise());
            } else {
                if (data.hasDirection(WallDirection.fromIndex(0)) &&
                    data.hasDirection(WallDirection.fromIndex(2))) {
                    return Rotation.NONE;
                }

                if (data.hasDirection(WallDirection.fromIndex(1)) &&
                    data.hasDirection(WallDirection.fromIndex(3))) {
                    return Rotation.CLOCKWISE_90;
                }
            }
        }

        // T - Rooms
        if (exitCount == 3) {
            WallDirection wallDirection = null;
            for (int i = 0; i < n; i++) {
                if (data.hasDirection(WallDirection.fromIndex(i))) {
                    wallDirection = WallDirection.fromIndex(i);
                    break;
                }
            }
            return (wallDirection != null ? determineRotation(wallDirection.clockwise()): Rotation.NONE );
        }

        //Intersections
        return Rotation.NONE;
    }

    private static Rotation determineRotation(WallDirection dir) {
        return switch (dir) {
            case NORTH -> Rotation.NONE; // North
            case EAST -> Rotation.CLOCKWISE_90; // East
            case SOUTH -> Rotation.CLOCKWISE_180; // South
            case WEST -> Rotation.COUNTERCLOCKWISE_90; // West
        };
    }

    public static ResourceLocation getRoomToPlace(LevelChunk chunk, String dimensionName) {
        MazeData data = MazeData.getOrCreateData(chunk);
        int roomNumber = Math.toIntExact(data.getExitCount());

        int availableRoomsPerType = ServerConfig.getRoomNumberFromType(data.isCorner() ? 4 : roomNumber);
        int roomType = new Random().nextInt(0, availableRoomsPerType);

        return new ResourceLocation(Mazerooms.MODID, dimensionName + "/room_" + (roomNumber - 1) + (data.isCorner() ? "_c_" : "_") + roomType);
    }

    public static int getWeightedRandom(int[] values, double[] weights, RandomSource random) {
        if (values.length != weights.length) {
            throw new IllegalArgumentException("Values and weights must have the same length");
        }

        double totalWeight = 0;
        for (double weight : weights) {
            totalWeight += weight;
        }

        double[] cumulativeWeights = new double[weights.length];
        double cumulativeSum = 0;
        for (int i = 0; i < weights.length; i++) {
            cumulativeSum += weights[i] / totalWeight;
            cumulativeWeights[i] = cumulativeSum;
        }

        double randomValue = random.nextDouble();
        for (int i = 0; i < cumulativeWeights.length; i++) {
            if (randomValue < cumulativeWeights[i]) {
                return values[i];
            }
        }

        return values[0];
    }

}
