package com.leo.mazerooms.init;

import com.leo.mazerooms.MazeRooms;
import com.leo.mazerooms.data.MazeData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachmentTypes {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MazeRooms.MODID);

    public static final Supplier<AttachmentType<MazeData>> MAZE_DATA_ATTACHMENT = ATTACHMENT_TYPES.register(
        "maze_data",
        () -> AttachmentType.builder(() -> MazeData.NEW_DATA)
            .serialize(MazeData.CODEC)
            .build()
    );

}
