package com.leo.mazerooms.util;

import com.leo.mazerooms.MazeRooms;
import com.leo.mazerooms.data.MazeData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;

public class CommonUtils {

     public static void saveData(LevelChunk chunk, MazeData data) {
            MazeData.execute(chunk, d -> {
                d.setGenerated(data.generated());
                d.setWalls(data.walls());
            });
     }

     public static ResourceLocation create(String path) {
         return new ResourceLocation(MazeRooms.MODID, path);
     }
}
