package com.leo.mazerooms.init;

import com.leo.mazerooms.Mazerooms;
import com.leo.mazerooms.world.MazeChunkGenerator;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModGenerators {

    public static final DeferredRegister<Codec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR, Mazerooms.MODID);

    public static final RegistryObject<Codec<? extends ChunkGenerator>> MAZE_GENERATOR = CHUNK_GENERATORS.register(
        "maze",
        () -> MazeChunkGenerator.CODEC
    );
}
