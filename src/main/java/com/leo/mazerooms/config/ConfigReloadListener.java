package com.leo.mazerooms.config;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

public class ConfigReloadListener implements ResourceManagerReloadListener {

    @SuppressWarnings("null")
    @Override
    public void onResourceManagerReload(@NotNull ResourceManager pResourceManager) {
        WeightConfigs.getInstance().load();
    }
}
