package com.leo.mazerooms.data;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.List;

@AutoRegisterCapability
public interface IMazeData {
    boolean generated();
    List<WallDirection> walls();
}
