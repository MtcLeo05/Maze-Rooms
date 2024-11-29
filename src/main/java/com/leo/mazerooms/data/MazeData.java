package com.leo.mazerooms.data;

import com.leo.mazerooms.MazeRooms;
import com.leo.mazerooms.util.ListUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.util.List;

import static com.leo.mazerooms.init.ModAttachmentTypes.MAZE_DATA_ATTACHMENT;

public record MazeData(boolean generated, List<Boolean> walls) implements CustomPacketPayload {
    public static final Type<MazeData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MazeRooms.MODID, "maze_data"));

    public static final MazeData NEW_DATA = new MazeData(
        false,
        ListUtil.of(false, false, false, false)
    );

    public static final Codec<MazeData> CODEC = RecordCodecBuilder.create(
        inst -> inst.group(
            Codec.BOOL.fieldOf("generated").forGetter(d -> d.generated),
            Codec.BOOL.listOf().fieldOf("walls").forGetter(d -> d.walls)
        ).apply(inst, MazeData::new)
    );

    public static MazeData getOrCreateData(ChunkAccess chunk) {
        if(!chunk.hasData(MAZE_DATA_ATTACHMENT)){
            chunk.setData(MAZE_DATA_ATTACHMENT, NEW_DATA);
        }

        return chunk.getData(MAZE_DATA_ATTACHMENT);
    }

    public long getExitCount() {
        return walls().stream().filter(Boolean::booleanValue).count();
    }

    public boolean isCorner() {
        if(getExitCount() != 2) return false;

        for (int i = 0; i < walls().size(); i++) {
            if(walls().get(i) && walls().get((i + 2) % 4)) return false;
            if(walls().get(i) && walls().get((i + 1) % 4)) return true;
            if(walls().get(i) && walls().get((i + 3) % 4)) return true;
        }

        return false;
    }

    public boolean isLeft() {
        if(!isCorner()) return false;

        for (int i = 0; i < walls().size(); i++) {
            if(walls().get(i) && walls().get((i + 1) % 4)) return false;
            if(walls().get(i) && walls().get((i + 3) % 4)) return true;
        }

        return false;
    }

    public static MazeData[] getNearbyChunkData(ChunkAccess chunk, Level level) {
        if(level == null) return new MazeData[] {
            NEW_DATA,
            NEW_DATA,
            NEW_DATA,
            NEW_DATA
        };

        ChunkPos def = chunk.getPos();
        ChunkPos north, south, east, west;

        north = new ChunkPos(def.x, def.z + 1);
        east = new ChunkPos(def.x - 1, def.z);
        south = new ChunkPos(def.x, def.z - 1);
        west = new ChunkPos(def.x + 1, def.z);

        boolean northA, southA, eastA, westA;
        MazeData northD, southD, eastD, westD;

        northA = level.hasChunk(north.x, north.z);
        southA = level.hasChunk(south.x, south.z);
        eastA = level.hasChunk(east.x, east.z);
        westA = level.hasChunk(west.x, west.z);

        northD = northA? MazeData.getOrCreateData(level.getChunk(north.x, north.z, ChunkStatus.SURFACE)): NEW_DATA;
        southD = southA? MazeData.getOrCreateData(level.getChunk(south.x, south.z, ChunkStatus.SURFACE)): NEW_DATA;
        eastD = eastA? MazeData.getOrCreateData(level.getChunk(east.x, east.z, ChunkStatus.SURFACE)): NEW_DATA;
        westD = westA? MazeData.getOrCreateData(level.getChunk(west.x, west.z, ChunkStatus.SURFACE)): NEW_DATA;

        return new MazeData[] {
            northD,
            eastD,
            southD,
            westD
        };
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
