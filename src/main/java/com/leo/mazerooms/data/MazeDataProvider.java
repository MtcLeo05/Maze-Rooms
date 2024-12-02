package com.leo.mazerooms.data;

import com.leo.mazerooms.util.CommonUtils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MazeDataProvider implements ICapabilitySerializable<CompoundTag> {

    public static ResourceLocation KEY = CommonUtils.create("maze_data");
    public static Capability<MazeData> MAZE_DATA = CapabilityManager.get(new CapabilityToken<>(){});

    private MazeData data = null;
    private final LazyOptional<MazeData> optional = LazyOptional.of(this::createMazeData);

    private MazeData createMazeData() {
        if (data == null) {
            data = new MazeData();
        }

        return data;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if(capability == MAZE_DATA) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createMazeData().saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        createMazeData().loadNBTData(tag);
    }
}
