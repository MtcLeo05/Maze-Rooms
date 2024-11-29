package com.leo.mazerooms.init;

import com.leo.mazerooms.MazeRooms;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(MazeRooms.MODID);

    public static final DeferredHolder<Item, Item> MAZE_TILE = ITEMS.register(
        "maze_tile",
        () -> new BlockItem(
            ModBlocks.MAZE_TILE.get(),
            new Item.Properties()
        )
    );

    public static final DeferredHolder<Item, Item> TRIGGER_BLOCK = ITEMS.register(
        "trigger_block",
        () -> new BlockItem(
            ModBlocks.TRIGGER_BLOCK.get(),
            new Item.Properties()
        )
    );

    public static final DeferredHolder<Item, Item> LIGHT_BLOCK = ITEMS.register(
        "light_block",
        () -> new BlockItem(
            ModBlocks.LIGHT_BLOCK.get(),
            new Item.Properties()
        )
    );

}
