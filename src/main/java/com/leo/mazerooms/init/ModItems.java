package com.leo.mazerooms.init;

import com.leo.mazerooms.Mazerooms;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Mazerooms.MODID);

    public static final RegistryObject<Item> MAZE_TILE = ITEMS.register(
        "maze_tile",
        () -> new BlockItem(
            ModBlocks.MAZE_TILE.get(),
            new Item.Properties()
        )
    );

    public static final RegistryObject<Item> TRIGGER_BLOCK = ITEMS.register(
        "trigger_block",
        () -> new BlockItem(
            ModBlocks.TRIGGER_BLOCK.get(),
            new Item.Properties()
        )
    );

    public static final RegistryObject<Item> LIGHT_BLOCK = ITEMS.register(
        "light_block",
        () -> new BlockItem(
            ModBlocks.LIGHT_BLOCK.get(),
            new Item.Properties()
        )
    );

}
