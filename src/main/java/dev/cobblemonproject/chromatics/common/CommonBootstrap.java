package dev.cobblemonproject.chromatics.common;

import dev.cobblemonproject.chromatics.CobblemonChromatics;
import dev.cobblemonproject.chromatics.common.chromatic.ChromaticLifecycle;
import dev.cobblemonproject.chromatics.common.command.ChromaticsCommands;
import dev.cobblemonproject.chromatics.common.effect.ChromaticEffectEvents;
import dev.cobblemonproject.chromatics.common.network.ChromaticNetworking;
import dev.cobblemonproject.chromatics.config.ChromaticsConfig;

public final class CommonBootstrap {
    private CommonBootstrap() {}
    public static void initialize(ChromaticsConfig config) {
        ChromaticLifecycle.register(config);
        ChromaticEffectEvents.register();
        ChromaticNetworking.register();
        ChromaticsCommands.register();
        if (config.developmentLogging()) CobblemonChromatics.LOGGER.info("Development logging is enabled");
    }
}
