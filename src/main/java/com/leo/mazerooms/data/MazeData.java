package com.leo.mazerooms.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.leo.mazerooms.data.MazeDataProvider.MAZE_DATA;

public final class MazeData {
    private boolean generated;
    private List<WallDirection> walls;

    public MazeData(boolean generated, List<WallDirection> walls) {
        this.generated = generated;
        this.walls = walls;
    }

    public MazeData() {
        this(false, new ArrayList<>());
    }

    public static MazeData getOrCreateData(LevelChunk chunk) {
        LazyOptional<MazeData> capability = chunk.getCapability(MAZE_DATA);
        return capability.orElse(new MazeData());
    }

    public static void execute(LevelChunk chunk, Consumer<MazeData> consumer) {
        consumer.accept(MazeData.getOrCreateData(chunk));
    }

    public void setWalls(List<WallDirection> walls) {
        this.walls = walls;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public int getExitCount() {
        return walls().size();
    }

    public boolean isCorner() {
        if (getExitCount() != 2) return false;

        for (WallDirection dir : walls) {
            if (hasOpposite(dir)) return false;
            if (hasClockwise(dir) || hasCounterClockwise(dir)) return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "MazeData{" +
            "generated=" + generated +
            ", walls=" + walls +
            '}';
    }

    public boolean hasDirection(WallDirection dir) {
        return walls.contains(dir);
    }

    public boolean hasClockwise(WallDirection dir) {
        return hasDirection(dir.clockwise());
    }

    public boolean hasOpposite(WallDirection dir) {
        return hasDirection(dir.opposite());
    }

    public boolean hasCounterClockwise(WallDirection dir) {
        return hasDirection(dir.counterClockwise());
    }

    public boolean isLeft() {
        if (!isCorner()) return false;
        for (WallDirection dir : walls) {
            if (hasClockwise(dir)) return false;
            if (hasCounterClockwise(dir)) return true;
        }
        return false;
    }

    public static LevelChunk[] getNearbyChunks(ChunkAccess chunk, Level level) {
        if (level == null) return null;

        ChunkPos def = chunk.getPos();
        ChunkPos north, south, east, west;

        north = new ChunkPos(def.x, def.z - 1);
        east = new ChunkPos(def.x + 1, def.z);
        south = new ChunkPos(def.x, def.z + 1);
        west = new ChunkPos(def.x - 1, def.z);

        LevelChunk northD, southD, eastD, westD;

        northD = level.getChunk(north.x, north.z);
        southD = level.getChunk(south.x, south.z);
        eastD = level.getChunk(east.x, east.z);
        westD = level.getChunk(west.x, west.z);

        return new LevelChunk[]{
            northD,
            eastD,
            southD,
            westD
        };
    }

    public static MazeData[] getNearbyChunkData(ChunkAccess chunk, ServerLevel level) {
        if (level == null) return new MazeData[]{
            new MazeData(),
            new MazeData(),
            new MazeData(),
            new MazeData()
        };

        MazeData[] toRet = new MazeData[]{new MazeData(), new MazeData(), new MazeData(), new MazeData()};

        LevelChunk[] nearbyChunks = getNearbyChunks(chunk, level);

        for (int i = 0; i < nearbyChunks.length; i++) {
            LevelChunk c = nearbyChunks[i];
            if (c == null) continue;

            toRet[i] = getOrCreateData(c);
        }

        return toRet;
    }

    public boolean generated() {
        return generated;
    }

    public List<WallDirection> walls() {
        return walls;
    }

    public static LevelChunk getChunkFromDirection(LevelChunk current, WallDirection direction, ServerLevel level) {
        ChunkPos currentPos = current.getPos();
        ChunkPos neighborPos = switch (direction) {
            case NORTH -> new ChunkPos(currentPos.x, currentPos.z - 1);
            case SOUTH -> new ChunkPos(currentPos.x, currentPos.z + 1);
            case WEST -> new ChunkPos(currentPos.x - 1, currentPos.z);
            case EAST -> new ChunkPos(currentPos.x + 1, currentPos.z);
        };
        return level.getChunkSource().getChunk(neighborPos.x, neighborPos.z, false);
    }

    public void saveNBTData(CompoundTag tag) {
        tag.putBoolean("generated", generated);

        List<Integer> walls = this.walls.stream().map(Enum::ordinal).toList();
        tag.putIntArray("walls", walls);

    }

    public void loadNBTData(CompoundTag tag) {
        generated = tag.getBoolean("generated");
        Stream<WallDirection> walls = Arrays.stream(tag.getIntArray("walls")).mapToObj(WallDirection::fromIndex);
        this.walls = walls.toList();
    }
}
