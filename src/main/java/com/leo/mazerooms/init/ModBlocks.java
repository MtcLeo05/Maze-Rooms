package com.leo.mazerooms.init;

import com.leo.mazerooms.MazeRooms;
import com.leo.mazerooms.block.TriggerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(MazeRooms.MODID);

    public static final DeferredHolder<Block, Block> MAZE_TILE = BLOCKS.register("maze_tile",
        () -> new Block(
            BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK)
        )
    );

    public static final DeferredHolder<Block, Block> LIGHT_BLOCK = BLOCKS.register("light_block",
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

    public static final DeferredHolder<Block, Block> TRIGGER_BLOCK = BLOCKS.register("trigger_block",
        () -> new TriggerBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK)
        )
    );

}
