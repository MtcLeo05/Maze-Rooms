package com.leo.mazerooms.util;

import com.leo.mazerooms.MazeRooms;
import com.leo.mazerooms.data.MazeData;
import com.leo.mazerooms.init.ModAttachmentTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkAccess;

public class CommonUtils {

     public static void saveData(ChunkAccess chunk, MazeData data) {
            chunk.setData(ModAttachmentTypes.MAZE_DATA_ATTACHMENT, data);
     }

     public static ResourceLocation create(String path) {
         return ResourceLocation.fromNamespaceAndPath(MazeRooms.MODID, path);
     }
}
