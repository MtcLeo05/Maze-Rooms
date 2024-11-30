package com.leo.mazerooms.event;

import com.leo.mazerooms.Mazerooms;
import com.leo.mazerooms.data.MazeData;
import com.leo.mazerooms.data.MazeDataProvider;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = Mazerooms.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeBusEvents {
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<LevelChunk> event) {
        event.addCapability(MazeData.KEY, new MazeDataProvider());
    }
}
