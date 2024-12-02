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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class TriggerBlock extends Block {
    public TriggerBlock(Properties properties) {
        super(
            properties
                .replaceable()
                .noCollission()
                .noLootTable()
                .noOcclusion()
                .isValidSpawn((s, l, p, e) -> false)
                .noTerrainParticles()
        );
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1f;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if(!(entity instanceof ServerPlayer sPlayer)) return;
        if(!(sPlayer.level() instanceof ServerLevel sLevel)) return;
        ResourceLocation dimensionName = sLevel.dimension().location();
        if(!dimensionName.getNamespace().equalsIgnoreCase("mazerooms")) return;


        RoomHandler.handlePlayerChunkChange(sPlayer,  sLevel);

        level.removeBlock(pos, false);

        super.entityInside(state, level, pos, entity);
    }
}
