package com.leo.mazerooms.event;

import com.leo.mazerooms.MazeRooms;
import com.leo.mazerooms.config.ConfigReloadListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(modid = MazeRooms.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModBusEvents {
    @SubscribeEvent
    public static void addReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ConfigReloadListener());
    }
}
