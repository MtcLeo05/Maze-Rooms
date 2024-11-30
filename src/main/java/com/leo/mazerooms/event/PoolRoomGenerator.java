package com.leo.mazerooms.event;

import com.leo.mazerooms.MazeRooms;
import com.leo.mazerooms.config.ServerConfig;
import com.leo.mazerooms.world.RoomHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = MazeRooms.MODID, bus = EventBusSubscriber.Bus.GAME)
public class PoolRoomGenerator {
    public static final ResourceKey<Level> POOL = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(MazeRooms.MODID, "pool"));

    @SubscribeEvent
    public static void onPlayerChangeChunk(EntityEvent.EnteringSection event) {
        if(!(event.getEntity().level() instanceof ServerLevel sLevel)) return;
        if(!(event.getEntity() instanceof ServerPlayer sPlayer)) return;
        ResourceLocation dimensionName = sLevel.dimension().location();
        if(!dimensionName.getNamespace().equalsIgnoreCase("mazerooms")) return;

        ChunkAccess chunk = sLevel.getChunk(sPlayer.blockPosition());
        RoomHandler.handleFutureChunks(chunk, sLevel, dimensionName.getPath());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if(!ServerConfig.SPAWN_IN_POOLROOMS.get()) return;
        if(!(event.getEntity().level() instanceof ServerLevel sLevel)) return;
        if(!(event.getEntity() instanceof ServerPlayer sPlayer)) return;
        if(!sLevel.dimension().equals(Level.OVERWORLD)) return;

        sPlayer.teleportTo(
            sLevel.getServer().getLevel(POOL),
            7.5,
            3,
            7.5,
            0,
            0
        );
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(EntityJoinLevelEvent event) {
        if(!(event.getEntity().level() instanceof ServerLevel sLevel)) return;
        if(!(event.getEntity() instanceof ServerPlayer sPlayer)) return;
        ResourceLocation dimensionName = sLevel.dimension().location();
        if(!dimensionName.getNamespace().equalsIgnoreCase("mazerooms")) return;

        RoomHandler.handleHub(
            sLevel.getChunk(sPlayer.chunkPosition().x,
                sPlayer.chunkPosition().z), sLevel, dimensionName.getPath());
    }
}
