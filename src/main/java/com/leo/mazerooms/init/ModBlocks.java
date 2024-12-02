package com.leo.mazerooms.init;

import com.leo.mazerooms.MazeRooms;
import com.leo.mazerooms.block.TriggerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MazeRooms.MODID);

    public static final RegistryObject<Block> MAZE_TILE = BLOCKS.register("maze_tile",
        () -> new Block(
            BlockBehaviour.Properties.copy(Blocks.BEDROCK)
        )
    );

    public static final RegistryObject<Block> LIGHT_BLOCK = BLOCKS.register("light_block",
        () -> new Block(
            BlockBehaviour.Properties.of()
                .replaceable()
                .strength(-1.0F, 3600000.8F)
                .mapColor(MapColor.NONE)
                .noLootTable()
                .noOcclusion()
                .lightLevel((s) -> 15)
        )
    );

    public static final RegistryObject<Block> TRIGGER_BLOCK = BLOCKS.register("trigger_block",
        () -> new TriggerBlock(
            BlockBehaviour.Properties.copy(Blocks.BEDROCK)
        )
    );

}
