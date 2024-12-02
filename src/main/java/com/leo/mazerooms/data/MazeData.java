package com.leo.mazerooms.data;

import com.leo.mazerooms.MazeRooms;
import com.leo.mazerooms.util.ListUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.List;

import static com.leo.mazerooms.init.ModAttachmentTypes.MAZE_DATA_ATTACHMENT;

public record MazeData(boolean generated, List<WallDirection> walls) implements CustomPacketPayload {
    public static final Type<MazeData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MazeRooms.MODID, "maze_data"));

    public static final MazeData NEW_DATA = new MazeData(
        false,
        ListUtil.of()
    );

    public static final Codec<MazeData> CODEC = RecordCodecBuilder.create(
        inst -> inst.group(
            Codec.BOOL.fieldOf("generated").forGetter(d -> d.generated),
            WallDirection.CODEC.listOf().fieldOf("walls").forGetter(d -> d.walls)
        ).apply(inst, MazeData::new)
    );

    public static MazeData getOrCreateData(ChunkAccess chunk) {
        if(!chunk.hasData(MAZE_DATA_ATTACHMENT)){
            chunk.setData(MAZE_DATA_ATTACHMENT, NEW_DATA);
        }

        return chunk.getData(MAZE_DATA_ATTACHMENT);
    }

    public int getExitCount() {
        return walls().size();
    }

    public boolean isCorner() {
        if(getExitCount() != 2) return false;

        for (WallDirection dir : walls) {
            if(hasOpposite(dir)) return false;
            if(hasClockwise(dir) || hasCounterClockwise(dir)) return true;
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
        if(!isCorner()) return false;
        for (WallDirection dir : walls) {
            if(hasClockwise(dir)) return false;
            if(hasCounterClockwise(dir)) return true;
        }
        return false;
    }

    public static ChunkAccess[] getNearbyChunks(ChunkAccess chunk, Level level) {
        if(level == null) return null;

        ChunkPos def = chunk.getPos();
        ChunkPos north, south, east, west;

        north = new ChunkPos(def.x, def.z - 1);
        east = new ChunkPos(def.x + 1, def.z);
        south = new ChunkPos(def.x, def.z + 1);
        west = new ChunkPos(def.x - 1, def.z);

        boolean northA, southA, eastA, westA;
        ChunkAccess northD, southD, eastD, westD;

        northA = level.hasChunk(north.x, north.z);
        southA = level.hasChunk(south.x, south.z);
        eastA = level.hasChunk(east.x, east.z);
        westA = level.hasChunk(west.x, west.z);

        northD = northA? level.getChunk(north.x, north.z): null;
        southD = southA? level.getChunk(south.x, south.z): null;
        eastD = eastA? level.getChunk(east.x, east.z): null;
        westD = westA? level.getChunk(west.x, west.z): null;

        return new ChunkAccess[] {
            northD,
            eastD,
            southD,
            westD
        };
    }


    public static MazeData[] getNearbyChunkData(ChunkAccess chunk, Level level) {
        if(level == null) return new MazeData[] {
            NEW_DATA,
            NEW_DATA,
            NEW_DATA,
            NEW_DATA
        };

        MazeData[] toRet = new MazeData[] {NEW_DATA, NEW_DATA, NEW_DATA, NEW_DATA};

        ChunkAccess[] nearbyChunks = getNearbyChunks(chunk, level);

        for (int i = 0; i < nearbyChunks.length; i++) {
            ChunkAccess c = nearbyChunks[i];
            if (c == null) continue;

            toRet[i] = getOrCreateData(c);
        }

        return toRet;
    }

    public static ChunkAccess getChunkFromDirection(ChunkAccess current, WallDirection direction, ServerLevel level) {
        ChunkPos currentPos = current.getPos();
        ChunkPos neighborPos = switch (direction) {
            case NORTH -> new ChunkPos(currentPos.x, currentPos.z - 1);
            case SOUTH -> new ChunkPos(currentPos.x, currentPos.z + 1);
            case WEST -> new ChunkPos(currentPos.x - 1, currentPos.z);
            case EAST -> new ChunkPos(currentPos.x + 1, currentPos.z);
        };
        return level.getChunkSource().getChunk(neighborPos.x, neighborPos.z, false);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
