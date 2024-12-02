package com.leo.mazerooms;

import com.leo.mazerooms.config.ServerConfig;
import com.leo.mazerooms.data.MazeDataProvider;
import com.leo.mazerooms.init.ModBlocks;
import com.leo.mazerooms.init.ModGenerators;
import com.leo.mazerooms.init.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import static com.leo.mazerooms.data.MazeDataProvider.MAZE_DATA;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MazeRooms.MODID)
public class MazeRooms {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "mazerooms";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public MazeRooms() {
        FMLJavaModLoadingContext ctx = FMLJavaModLoadingContext.get();
        IEventBus modEventBus = ctx.getModEventBus();

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModGenerators.CHUNK_GENERATORS.register(modEventBus);


        MinecraftForge.EVENT_BUS.addGenericListener(LevelChunk.class, MazeRooms::onAttachCapabilities);

        ctx.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC, "mazerooms/server-configs.toml");
    }

    public static void onAttachCapabilities(AttachCapabilitiesEvent<LevelChunk> event) {
        if(event.getObject() instanceof LevelChunk &&
            !event.getObject().getCapability(MAZE_DATA).isPresent())
            event.addCapability(MazeDataProvider.KEY, new MazeDataProvider());
    }
}
