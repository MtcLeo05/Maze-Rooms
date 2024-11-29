package com.leo.mazerooms;

import com.leo.mazerooms.config.ServerConfig;
import com.leo.mazerooms.init.ModAttachmentTypes;
import com.leo.mazerooms.init.ModBlocks;
import com.leo.mazerooms.init.ModGenerators;
import com.leo.mazerooms.init.ModItems;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MazeRooms.MODID)
public class MazeRooms {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "mazerooms";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public MazeRooms(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);

        ModAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        ModGenerators.CHUNK_GENERATORS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC, "mazerooms/server-configs.toml");
    }
}
