package com.leo.mazerooms.event;

import com.leo.mazerooms.MazeRooms;
import com.leo.mazerooms.config.ServerConfig;
import com.leo.mazerooms.data.MazeData;
import com.leo.mazerooms.data.WallDirection;
import com.leo.mazerooms.mixin.ChunkMapAccessor;
import com.leo.mazerooms.util.ListUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.List;
import java.util.Random;

import static com.leo.mazerooms.init.ModAttachmentTypes.MAZE_DATA_ATTACHMENT;

@EventBusSubscriber(modid = MazeRooms.MODID, bus = EventBusSubscriber.Bus.GAME)
public class PoolRoomGenerator {

    public static final ResourceKey<Level> MAZE = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(MazeRooms.MODID, "maze"));


    @SubscribeEvent
    public static void onPlayerChangeChunk(EntityEvent.EnteringSection event) {
        if(!(event.getEntity().level() instanceof ServerLevel sLevel)) return;
        if(!(event.getEntity() instanceof ServerPlayer)) return;
        if(!sLevel.dimension().equals(MAZE)) return;

        ChunkMapAccessor chunkMap = (ChunkMapAccessor) sLevel.getChunkSource().chunkMap;
        handleFutureChunks(chunkMap, sLevel);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if(!ServerConfig.SPAWN_IN_POOLROOMS.get()) return;
        if(!(event.getEntity().level() instanceof ServerLevel sLevel)) return;
        if(!(event.getEntity() instanceof ServerPlayer sPlayer)) return;
        if(!sLevel.dimension().equals(Level.OVERWORLD)) return;

        sPlayer.teleportTo(
            sLevel.getServer().getLevel(MAZE),
            7.5,
            3,
            7.5,
            0,
            0
        );

        handleChunk(sLevel.getChunk(sPlayer.chunkPosition().x, sPlayer.chunkPosition().z, ChunkStatus.SURFACE), sLevel);
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if(!(event.getEntity().level() instanceof ServerLevel sLevel)) return;
        if(!(event.getEntity() instanceof ServerPlayer sPlayer)) return;
        if(!event.getTo().equals(MAZE)) return;

        handleChunk(sLevel.getChunk(sPlayer.chunkPosition().x, sPlayer.chunkPosition().z, ChunkStatus.SURFACE), sLevel);
    }

    public static void handleFutureChunks(ChunkMapAccessor chunkMap, ServerLevel sLevel) {
        for (ChunkHolder chunkH : chunkMap.getVisibleChunkMap().values()) {
            LevelChunk currentChunk = chunkH.getTickingChunk();
            if(currentChunk == null) continue;
            handleChunk(currentChunk, sLevel);
        }
    }

    public static void handleChunk(ChunkAccess chunk, ServerLevel level) {
        MazeData cData = MazeData.getOrCreateData(chunk);

        if (chunk.getPos().equals(new ChunkPos(0, 0))) {
            placeChunkRoom(chunk, level, cData.generated()? ResourceLocation.fromNamespaceAndPath(MazeRooms.MODID, "room_3_0"): ResourceLocation.fromNamespaceAndPath(MazeRooms.MODID, "room_hub"));
            cData = new MazeData(true, ListUtil.of(WallDirection.values()));

            chunk.setData(MAZE_DATA_ATTACHMENT, cData);
            return;
        }

        if (cData.generated()) return;

        boolean gen = true;
        List<WallDirection> walls = ListUtil.of();

        MazeData[] nearbyData = MazeData.getNearbyChunkData(chunk, level);

        int length = nearbyData.length;
        boolean cont = false;



        for (int i = 0; i < length; i++) {
            if(nearbyData[i].generated()) cont = true;
            if (!nearbyData[i].hasDirection(WallDirection.fromIndex(i))) continue;
            walls.add(WallDirection.fromIndex(i));
            break;
        }

        if(!cont) return;

        int numberOfPaths = getWeightedRandom(new int[]{0, 1, 2, 3}, new double[]{0, 0.5, 0.3, 0.3}, level.random);

        List<Integer> possibleWallMap = ListUtil.of(0, 1, 2, 3);
        for (int currentWallIndex = 0; currentWallIndex < numberOfPaths; currentWallIndex++) {
            if (possibleWallMap.isEmpty()) break;

            int wallToOpen = level.random.nextInt(possibleWallMap.size());
            int oppositeWall = (wallToOpen + 2) % 4;

            boolean chunkGen = nearbyData[oppositeWall].generated();
            boolean isOppositeWallOpen = nearbyData[oppositeWall].hasDirection(WallDirection.fromIndex(oppositeWall));

            if(chunkGen && !isOppositeWallOpen) {
                possibleWallMap.remove(wallToOpen);
                currentWallIndex--;
                continue;
            }

            boolean isSelectedWallOpen = walls.contains(WallDirection.fromIndex(wallToOpen));

            if (isSelectedWallOpen) {
                possibleWallMap.remove(wallToOpen);
                currentWallIndex--;
                continue;
            }

            walls.add(WallDirection.fromIndex(wallToOpen));
            possibleWallMap.remove(wallToOpen);
        }

        MazeData data = new MazeData(gen, walls);
        chunk.setData(MAZE_DATA_ATTACHMENT, data);
        handleChunkRoom(chunk, level);
    }

    public static void placeChunkRoom(ChunkAccess chunk, ServerLevel level, ResourceLocation room) {
        ChunkPos cPos = chunk.getPos();
        MazeData data = MazeData.getOrCreateData(chunk);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        pos.set(cPos.getMinBlockX(), 0, cPos.getMinBlockZ());

        StructureTemplateManager manager = level.getStructureManager();
        StructureTemplate template = manager.getOrCreate(room);

        BlockPos center = new BlockPos(7, pos.getY(), 7);
        Rotation rot = getRotation(data);

        BlockPos toUse = pos.immutable();
        BlockPos offset = new BlockPos(0, 0, 0);

        if(!chunk.getPos().equals(new ChunkPos(0, 0))) {
            offset = offset.north(chunk.getPos().z).west(chunk.getPos().x);
            toUse = toUse.offset(offset.getX(), offset.getY(), offset.getZ());
        }

        template.placeInWorld(level, toUse, toUse, new StructurePlaceSettings().setRotationPivot(center).setRotation(rot), level.random, 3);
    }

    public static void handleChunkRoom(ChunkAccess chunk, ServerLevel level) {
        placeChunkRoom(chunk, level, getRoomToPlace(chunk));
    }

    public static Rotation getRotation(MazeData data) {
        int n = data.walls().size();

        int start = -1;
        for (int i = 0; i < n; i++) {
            if (data.hasDirection(WallDirection.fromIndex(i))) {
                start = i;
                break;
            }
        }

        long exitCount = data.getExitCount();

        //Dead ends
        if (start != -1 && exitCount == 1) {
            return determineRotation((start + 2) % 4);
        }

        // Hallways / Corners
        if (exitCount == 2) {
            if (data.isCorner()) {
                int cornerStart = data.walls().indexOf(true);

                return data.isLeft()?
                    determineRotation((cornerStart + 2) % 4):
                    determineRotation((cornerStart + 3) % 4);
            } else {
                if(data.hasDirection(WallDirection.fromIndex(0)) &&
                    data.hasDirection(WallDirection.fromIndex(2))) {
                    return Rotation.NONE;
                }

                if(data.hasDirection(WallDirection.fromIndex(1)) &&
                    data.hasDirection(WallDirection.fromIndex(3))) {
                    return Rotation.CLOCKWISE_90;
                }
            }
        }

        // T - Rooms
        if (exitCount == 3) {
            int wallDirection = -1;
            for (int i = 0; i < n; i++) {
                if (!data.hasDirection(WallDirection.fromIndex(i))) {
                    wallDirection = i;
                    break;
                }
            }
            return determineRotation((wallDirection + 1) % 4);
        }

        //Intersections
        return Rotation.NONE;
    }

    private static Rotation determineRotation(int direction) {
        return switch (direction) {
            case 0 -> Rotation.NONE; // North
            case 1 -> Rotation.CLOCKWISE_90; // East
            case 2 -> Rotation.CLOCKWISE_180; // South
            case 3 -> Rotation.COUNTERCLOCKWISE_90; // West
            default -> throw new IllegalArgumentException("Invalid direction");
        };
    }

    public static ResourceLocation getRoomToPlace(ChunkAccess chunk) {
        MazeData data = MazeData.getOrCreateData(chunk);
        int roomNumber = Math.toIntExact(data.getExitCount());

        int availableRoomsPerType = ServerConfig.getRoomNumberFromType(data.isCorner()? 4: roomNumber);
        int roomType = new Random().nextInt(0, availableRoomsPerType);

        return ResourceLocation.fromNamespaceAndPath(MazeRooms.MODID, "room_" + (roomNumber - 1) + (data.isCorner()? "_c_": "_") + roomType);
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
