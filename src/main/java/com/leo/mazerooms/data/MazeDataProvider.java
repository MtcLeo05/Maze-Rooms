package com.leo.mazerooms.data;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MazeDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public MazeData data = null;
    private final LazyOptional<MazeData> optional = LazyOptional.of(this::createMazeData);

    private MazeData createMazeData() {
        if (data == null) {
            data = MazeData.NEW_DATA;
        }

        return data;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if(capability == MazeData.MAZE_DATA) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag = createMazeData().saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        createMazeData().deserializeNBT(tag);
    }
}
