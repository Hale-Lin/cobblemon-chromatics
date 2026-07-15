package dev.cobblemonproject.chromatics;

import dev.cobblemonproject.chromatics.common.CommonBootstrap;
import dev.cobblemonproject.chromatics.config.ChromaticsConfig;
import dev.cobblemonproject.chromatics.config.ChromaticsConfigManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CobblemonChromatics implements ModInitializer {
    public static final String MOD_ID = "cobblemon_chromatics";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ChromaticsConfig config;

    @Override
    public void onInitialize() {
        config = ChromaticsConfigManager.load();
        CommonBootstrap.initialize(config);
        LOGGER.info("Cobblemon Chromatics initialized for Cobblemon 1.7.3 on Minecraft 1.21.1");
    }

    public static ChromaticsConfig config() {
        if (config == null) {
            throw new IllegalStateException("Cobblemon Chromatics configuration is not initialized");
        }
        return config;
    }
}
