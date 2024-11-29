package com.leo.mazerooms.block;

import com.leo.mazerooms.mixin.ChunkMapAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

import static com.leo.mazerooms.event.PoolRoomGenerator.MAZE;
import static com.leo.mazerooms.event.PoolRoomGenerator.handleFutureChunks;

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
        if(!sLevel.dimension().equals(MAZE)) return;

        ChunkMapAccessor chunkMap = (ChunkMapAccessor) sLevel.getChunkSource().chunkMap;

        if(chunkMap.getVisibleChunkMap().isEmpty()) return;
        if(chunkMap.getVisibleChunkMap().values().isEmpty()) return;
        if(chunkMap.getVisibleChunkMap().values().stream().noneMatch(Objects::nonNull)) return;
        if(chunkMap.getVisibleChunkMap().values().stream().noneMatch(o -> o.getTickingChunk() != null)) return;

        handleFutureChunks(chunkMap, sLevel);

        level.removeBlock(pos, false);

        super.entityInside(state, level, pos, entity);
    }
}
