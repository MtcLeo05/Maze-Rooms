package com.leo.mazerooms;

import com.leo.mazerooms.config.ServerConfig;
import com.leo.mazerooms.init.ModBlocks;
import com.leo.mazerooms.init.ModGenerators;
import com.leo.mazerooms.init.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Mazerooms.MODID)
public class Mazerooms {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "mazerooms";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Mazerooms() {
        FMLJavaModLoadingContext ctx = FMLJavaModLoadingContext.get();
        IEventBus modEventBus = ctx.getModEventBus();

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModGenerators.CHUNK_GENERATORS.register(modEventBus);

        ctx.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC, "mazerooms/server-configs.toml");
    }
}
