package dev.cobblemonproject.chromatics.server;

import dev.cobblemonproject.chromatics.CobblemonChromatics;
import net.fabricmc.api.DedicatedServerModInitializer;

public final class CobblemonChromaticsServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        CobblemonChromatics.LOGGER.debug("Cobblemon Chromatics dedicated-server services initialized");
    }
}
