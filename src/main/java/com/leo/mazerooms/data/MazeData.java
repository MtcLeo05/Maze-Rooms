package com.leo.mazerooms.data;

import com.leo.mazerooms.Mazerooms;
import com.leo.mazerooms.util.ListUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class MazeData implements IMazeData {
    public static ResourceLocation KEY = new ResourceLocation(Mazerooms.MODID, "maze_data");
    public static CapabilityToken<MazeData> TOKEN = new CapabilityToken<>() {};
    public static Capability<MazeData> MAZE_DATA = CapabilityManager.get(TOKEN);

    public static final MazeData NEW_DATA = new MazeData(false, ListUtil.of());
    private boolean generated;
    private List<WallDirection> walls;

    public MazeData(boolean generated, List<WallDirection> walls) {
        this.generated = generated;
        this.walls = walls;
    }

    public static MazeData getOrCreateData(LevelChunk chunk) {
        LazyOptional<MazeData> capability = chunk.getCapability(MAZE_DATA);

        return capability.orElse(NEW_DATA);
    }

    public static void execute(LevelChunk chunk, Consumer<MazeData> consumer) {
        Optional<MazeData> optional = chunk.getCapability(MAZE_DATA).resolve();
        optional.ifPresent(consumer);
    }

    public void replace(MazeData data) {
        generated = data.generated;
        walls = data.walls;
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

        boolean northA, southA, eastA, westA;
        LevelChunk northD, southD, eastD, westD;

        northA = level.hasChunk(north.x, north.z);
        southA = level.hasChunk(south.x, south.z);
        eastA = level.hasChunk(east.x, east.z);
        westA = level.hasChunk(west.x, west.z);

        northD = northA ? level.getChunk(north.x, north.z) : null;
        southD = southA ? level.getChunk(south.x, south.z) : null;
        eastD = eastA ? level.getChunk(east.x, east.z) : null;
        westD = westA ? level.getChunk(west.x, west.z) : null;

        return new LevelChunk[]{
            northD,
            eastD,
            southD,
            westD
        };
    }

    public static MazeData[] getNearbyChunkData(ChunkAccess chunk, ServerLevel level) {
        if (level == null) return new MazeData[]{
            NEW_DATA,
            NEW_DATA,
            NEW_DATA,
            NEW_DATA
        };

        MazeData[] toRet = new MazeData[]{NEW_DATA, NEW_DATA, NEW_DATA, NEW_DATA};

        LevelChunk[] nearbyChunks = getNearbyChunks(chunk, level);

        for (int i = 0; i < nearbyChunks.length; i++) {
            LevelChunk c = nearbyChunks[i];
            if (c == null) continue;

            toRet[i] = getOrCreateData(c);
        }

        return toRet;
    }

    @Override
    public String toString() {
        return "MazeData[" +
            "generated=" + generated + ", " +
            "walls=" + walls + ']';
    }

    @Override
    public boolean generated() {
        return generated;
    }

    @Override
    public List<WallDirection> walls() {
        return walls;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (MazeData) obj;
        return this.generated == that.generated &&
            Objects.equals(this.walls, that.walls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(generated, walls);
    }

    public CompoundTag saveNBTData(CompoundTag tag) {
        tag.putBoolean("generated", generated);

        List<Integer> walls = this.walls.stream().map(Enum::ordinal).toList();
        tag.putIntArray("walls", walls);

        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        generated = tag.getBoolean("generated");
        Stream<WallDirection> walls = Arrays.stream(tag.getIntArray("walls")).mapToObj(WallDirection::fromIndex);
        this.walls = walls.toList();
    }
}
