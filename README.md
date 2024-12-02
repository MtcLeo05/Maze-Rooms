# Maze Rooms
### A liminal minecraft mod

This mod allows you to explore procedurally generated maze-like dimensions

Although the mod's theme is liminal spaces, you can customize the dimensions as much as you want, as you're able to change the default structures, or add new dimensions entirely

To add / change dimensions, you only need a datapack and some structure files!

To do so, first thing is to have these paths:
- `data/mazerooms/dimension`
- `data/mazerooms/dimension_type`
- `data/mazerooms/structure`
- `data/mazerooms/worldgen/biome`

`dimension_type` is optional, as the built-in one most of the time is plenty

The name of the dimension file will also be used to search structures!
```json
{
    "type": "mazerooms:maze",
    "generator": {
        "type": "mazerooms:maze",
        "biome_source": {
            "type": "minecraft:fixed",
            "biome": "mazerooms:pool"
        }
    }
}
```

^ this is an example of the dimension file you should have. I suggest changing the biome only if you're interested in things like water color

Now for the structures, head over to `data/mazerooms/structure/name` where name is the name of the dimension file, and your structures. The structures should follow this guidelines:

- They must follow a naming convention based on how many open walls they have:
  * `room_0` for dead ends (so 1 entrance and 3 walls)
  * `room_1` for hallways ends (so 2 entrances and 2 walls)
  * `room_1_c` for clockwise corners (so 2 entrances and 2 walls)
  * `room_1_cc` for counter-clockwise corners (so 2 entrances and 2 walls)
  * `room_2` for t rooms (so 3 entrances and 1 wall)
  * `room_3` for intersections (for no walls)
  * `room_hub` for the hub (the hub is the first room you spawn in. It should have 4 walls and at least 1 trigger block)

- They must be 15x15 blocks, starting from 0, 0 in the chunk
- They must be north-first and clockwise, so first entrance is north, second is either east or south and so on
- You must have at least 1 room of each type, with `_0` after the name, e.g. `room_0_0`
- You can have multiple of one room, and each subsequent room should follow the previous rule, e.g. `room_0_0, room_0_1`
  * If you have multiple of one room, you MUST change the config file for that specific room type
  * The config file is for ALL dimensions, so if 1 dimension has 3 corner rooms, also have to (you can just copy-paste already existing ones and change the name)

For any doubt, just load a structure in a creative world with a structure block and use it as a template