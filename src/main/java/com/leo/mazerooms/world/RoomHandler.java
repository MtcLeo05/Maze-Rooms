package com.leo.mazerooms.world;

import com.leo.mazerooms.MazeRooms;
import com.leo.mazerooms.config.ServerConfig;
import com.leo.mazerooms.data.MazeData;
import com.leo.mazerooms.data.WallDirection;
import com.leo.mazerooms.util.ListUtil;
import com.leo.mazerooms.util.CommonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.Random;

public class RoomHandler {
    public static void handleFutureChunks(LevelChunk current, ServerLevel sLevel, ServerPlayer player) {
        int posX = current.getPos().x, posZ = current.getPos().z, pX = player.chunkPosition().x, pZ = player.chunkPosition().z;
        if(Math.abs((posX - pX)) + Math.abs((posZ - pZ)) >= ServerConfig.MAX_CHUNK_DISTANCE.get()) return;

        LevelChunk[] nearbyChunks = MazeData.getNearbyChunks(current, sLevel);
        List<LevelChunk> chunks = ListUtil.of();

        for (int i = 0; i < nearbyChunks.length; i++) {
            LevelChunk chunk = nearbyChunks[i];
            if (chunk == null) continue;
            MazeData data = MazeData.getOrCreateData(current);
            if(data.generated()) {
                chunks.add(chunk);
                continue;
            }
            WallDirection dir = WallDirection.fromIndex(i);
            if(!data.hasOpposite(dir)) continue;
            chunks.add(chunk);
        }

        while(!chunks.isEmpty()) {
            int random = sLevel.random.nextInt(chunks.size());
            LevelChunk levelChunk = chunks.get(random);
            chunks.remove(levelChunk);
            handleChunk(levelChunk, sLevel, player);
        }
    }

    public static void handleHub(LevelChunk chunk, ServerLevel level) {
        MazeData data = MazeData.getOrCreateData(chunk);
        if (data.generated()) return;
        String dimensionName = level.dimension().location().getPath();
        placeChunkRoom(chunk, level, CommonUtils.create(dimensionName + "/room_hub"));
        data = new MazeData(false, ListUtil.of(WallDirection.values()));
        CommonUtils.saveData(chunk, data);
    }

    public static void handleChunk(LevelChunk chunk, ServerLevel level, ServerPlayer player) {
        MazeData data = MazeData.getOrCreateData(chunk);
        if (data.generated()) {
            handleFutureChunks(chunk, level, player);
            return;
        }

        //Check and replace hub room
        if(data.walls().size() >= 4) {
            String dimensionName = level.dimension().location().getPath();
            data = new MazeData(true, ListUtil.of(WallDirection.values()));
            CommonUtils.saveData(chunk, data);
            placeChunkRoom(chunk, level, CommonUtils.create(dimensionName + "/room_3_0"));
            handleFutureChunks(chunk, level, player);
            return;
        }

        List<WallDirection> walls = ListUtil.of();

        MazeData[] nearbyData = MazeData.getNearbyChunkData(chunk, level);

        int remove = -1;

        //Check for already opened rooms nearby
        for (int i = 0; i < nearbyData.length; i++) {
            MazeData sideData = nearbyData[i];
            if(!sideData.generated()) continue;
            WallDirection dir = WallDirection.fromIndex(i);
            if(sideData.hasOpposite(dir)) {
                walls.add(dir);
                remove++;
            }
        }

        int numberOfPaths = getWeightedRandom(new int[]{0, 1, 2, 3}, new double[]{0, 0.0, 0.75, 0.25}, level.random);

        List<WallDirection> possibleWalls = ListUtil.of(WallDirection.values());
        //Randomize new entrances based on how many were already opened
        for (int currentWallIndex = 0; currentWallIndex < numberOfPaths - remove; currentWallIndex++) {
            //If all entrances are open break loop
            if (possibleWalls.isEmpty()) break;

            //Randomize direction
            int wallToOpen = level.random.nextInt(possibleWalls.size());
            WallDirection wallDir = possibleWalls.get(wallToOpen);

            boolean isSelectedWallOpen = walls.contains(wallDir);

            //If the selected wall is already open, remove it from the available ones
            if (isSelectedWallOpen) {
                possibleWalls.remove(wallDir);
                currentWallIndex--;
                continue;
            }

            WallDirection opposite = wallDir.opposite();
            boolean chunkGen = nearbyData[currentWallIndex].generated();
            boolean isOppositeWallOpen = nearbyData[currentWallIndex].hasDirection(opposite);

            //If the nearby chunk is already generated, and the opposite wall to the selected one is closed,
            // remove it from the available ones to avoid ugly dead ends
            if (chunkGen && !isOppositeWallOpen) {
                possibleWalls.remove(wallDir);
                currentWallIndex--;
                continue;
            }

            //Open the wall and remove it from the available ones
            walls.add(wallDir);
            possibleWalls.remove(wallDir);
        }

        data = new MazeData(true, walls);

        CommonUtils.saveData(chunk, data);

        MazeRooms.LOGGER.info("Chunk: {}, Maze Data: {}", chunk.getPos(), MazeData.getOrCreateData(chunk));
        handleChunkRoom(chunk, level);
        handleFutureChunks(chunk, level, player);
    }

    public static void placeChunkRoom(LevelChunk chunk, ServerLevel level, ResourceLocation room) {
        ChunkPos cPos = chunk.getPos();
        MazeData data = MazeData.getOrCreateData(chunk);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        pos.set(cPos.getMinBlockX(), 0, cPos.getMinBlockZ());

        StructureTemplateManager manager = level.getStructureManager();
        StructureTemplate template = manager.getOrCreate(room);

        BlockPos center = new BlockPos(7, pos.getY(), 7);
        Rotation rot = getRoomRotation(data);

        BlockPos toUse = pos.immutable();
        BlockPos offset = new BlockPos(0, 0, 0);

        if (!chunk.getPos().equals(new ChunkPos(0, 0))) {
            offset = offset.north(chunk.getPos().z).west(chunk.getPos().x);
            toUse = toUse.offset(offset.getX(), offset.getY(), offset.getZ());
        }

        template.placeInWorld(level, toUse, toUse, new StructurePlaceSettings().setRotationPivot(center).setRotation(rot), level.random, 3);
    }

    public static void handleChunkRoom(LevelChunk chunk, ServerLevel level) {
        placeChunkRoom(chunk, level, getRoomToPlace(chunk, level));
    }

    public static Rotation getRoomRotation(MazeData data) {
        if(!data.generated()) return Rotation.NONE;

        WallDirection start = data.walls().get(0).opposite();
        int exitCount = data.getExitCount();

        //Dead ends
        if (exitCount == 1) {
            return determineRotation(start);
        }

        if(exitCount == 3) {
            int count = data.walls().stream().map(Enum::ordinal).mapToInt(Integer::intValue).sum();
            return determineRotation(WallDirection.fromIndex(count - 5));
        }

        // Hallways / Corners
        if (exitCount == 2) {
            if (data.isCorner()) {
                return data.isLeft() ?
                    determineRotation(start.counterClockwise()) :
                    determineRotation(start.clockwise());
            }

            return determineRotation(start);
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

    public static ResourceLocation getRoomToPlace(LevelChunk chunk, ServerLevel level) {
        MazeData data = MazeData.getOrCreateData(chunk);
        int roomNumber = Math.toIntExact(data.getExitCount());

        String dimensionName = level.dimension().location().getPath();

        int availableRoomsPerType = ServerConfig.getRoomNumberFromType(data.isCorner() ? 4 : roomNumber);
        int roomType = new Random().nextInt(0, availableRoomsPerType);

        return CommonUtils.create(dimensionName + "/room_" + (roomNumber - 1) + (data.isCorner() ? (data.isLeft()? "_cc_": "_c_") : "_") + roomType);
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
