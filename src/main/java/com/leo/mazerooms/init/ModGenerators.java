package com.leo.mazerooms.init;

import com.leo.mazerooms.MazeRooms;
import com.leo.mazerooms.world.MazeChunkGenerator;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModGenerators {

    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR, MazeRooms.MODID);

    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<? extends ChunkGenerator>> MAZE_GENERATOR = CHUNK_GENERATORS.register(
        "maze",
        () -> MazeChunkGenerator.CODEC
    );
}
