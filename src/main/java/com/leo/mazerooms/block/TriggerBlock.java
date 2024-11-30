package com.leo.mazerooms.block;

import com.leo.mazerooms.world.RoomHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class TriggerBlock extends Block {
    public TriggerBlock(Properties properties) {
        super(
            properties
                .replaceable()
                .noCollission()
                .noLootTable()
                .noOcclusion()
                .isValidSpawn(Blocks::never)
                .noTerrainParticles()
        );
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1f;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if(!(entity instanceof ServerPlayer sPlayer)) return;
        if(!(sPlayer.level() instanceof ServerLevel sLevel)) return;
        ResourceLocation dimensionName = sLevel.dimension().location();
        if(!dimensionName.getNamespace().equalsIgnoreCase("mazerooms")) return;

        ChunkAccess chunk = sLevel.getChunk(sPlayer.blockPosition());
        RoomHandler.handleChunk(chunk, sLevel, dimensionName.getPath());
        RoomHandler.handleFutureChunks(chunk, sLevel, dimensionName.getPath());

        level.removeBlock(pos, false);

        super.entityInside(state, level, pos, entity);
    }
}
