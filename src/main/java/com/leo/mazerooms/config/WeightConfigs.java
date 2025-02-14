package com.leo.mazerooms.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class WeightConfigs {

    public static final String CONFIG_FILE = "mazerooms.json5";
    private static WeightConfigs INSTANCE = new WeightConfigs();

    private WeightConfigs() {}

    public static WeightConfigs getInstance() {
        return INSTANCE != null ? INSTANCE : new WeightConfigs();
    }

    @Expose
    @SerializedName("dimension_config")
    public Map<String, DimensionConfig> DIMENSION_CONFIG = Map.of(
        "mazerooms:pool",
        new DimensionConfig( 0.05, 0.75, 0.15, 0.05)
    );

    public DimensionConfig fromDimension(ResourceKey<Level> dimension) {
        String dim = dimension.location().toString();

        return DIMENSION_CONFIG.getOrDefault(dim, new DimensionConfig( 0.05, 0.75, 0.15, 0.05));
    }

    public void load() {
        Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

        Path configPath = FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE);
        File file = configPath.toFile();

        try {
            if (!file.exists()) {
                System.out.println("Configuration file does not exist. Creating a new one.");
                saveDefaultConfig(file, gson);
            } else {
                try (JsonReader jsonReader = new JsonReader(new FileReader(file))) {
                    INSTANCE = gson.fromJson(jsonReader, WeightConfigs.class);
                    if (INSTANCE == null) {
                        throw new JsonSyntaxException("Parsed configuration is null.");
                    }
                }
            }
        } catch (JsonSyntaxException | IOException e) {
            System.err.println("Invalid configuration file. Regenerating default config.");
            saveDefaultConfig(file, gson);
        }
    }

    private void saveDefaultConfig(File file, Gson gson) {
        try (FileWriter writer = new FileWriter(file)) {
            if(INSTANCE == null) INSTANCE = new WeightConfigs();

            gson.toJson(INSTANCE, WeightConfigs.class, writer);
            System.out.println("Default configuration file created successfully.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create default configuration file.", e);
        }
    }

    public record DimensionConfig(
        @Expose @SerializedName("dead_end_chance") double one,
        @Expose @SerializedName("hallway_chance") double two,
        @Expose @SerializedName("t_room_chance") double three,
        @Expose @SerializedName("intersection_chance") double four
    ){

    }
}
