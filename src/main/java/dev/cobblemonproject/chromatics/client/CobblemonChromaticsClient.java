package dev.cobblemonproject.chromatics.client;

import dev.cobblemonproject.chromatics.CobblemonChromatics;
import net.fabricmc.api.ClientModInitializer;

public final class CobblemonChromaticsClient implements ClientModInitializer {
    @Override public void onInitializeClient() { ChromaticClientNetworking.register(); CobblemonChromatics.LOGGER.debug("Cobblemon Chromatics client services initialized"); }
}
