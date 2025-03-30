package com.leo.mazerooms.event;

import com.leo.mazerooms.MazeRooms;
import com.leo.mazerooms.config.ConfigReloadListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(modid = MazeRooms.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GameBusEvents {
    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(new ConfigReloadListener());
    }
}
