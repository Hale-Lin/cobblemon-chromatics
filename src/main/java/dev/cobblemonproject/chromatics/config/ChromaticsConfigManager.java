package dev.cobblemonproject.chromatics.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.cobblemonproject.chromatics.CobblemonChromatics;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ChromaticsConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = CobblemonChromatics.MOD_ID + ".json";

    private ChromaticsConfigManager() {
    }

    public static ChromaticsConfig load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        ChromaticsConfig config = read(path);
        config.validate();
        write(path, config);
        return config;
    }

    private static ChromaticsConfig read(Path path) {
        if (!Files.isRegularFile(path)) {
            return new ChromaticsConfig();
        }

        try (Reader reader = Files.newBufferedReader(path)) {
            ChromaticsConfig config = GSON.fromJson(reader, ChromaticsConfig.class);
            return config == null ? new ChromaticsConfig() : config;
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException("Unable to read configuration " + path, exception);
        }
    }

    private static void write(Path path, ChromaticsConfig config) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to write configuration " + path, exception);
        }
    }
}
