package com.leo.mazerooms.event;

import com.leo.mazerooms.MazeRooms;
import com.leo.mazerooms.config.ConfigReloadListener;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MazeRooms.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeBusEvents {
    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(new ConfigReloadListener());
    }
}
