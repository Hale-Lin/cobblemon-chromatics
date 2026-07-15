package dev.cobblemonproject.chromatics.client;

import dev.cobblemonproject.chromatics.common.network.ChromaticProfileSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ChromaticClientNetworking {
    private ChromaticClientNetworking() {}
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ChromaticProfileSyncPayload.ID,(payload,context)->context.client().execute(()->ChromaticClientProfiles.put(payload.pokemonId(),payload.profile())));
        ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->ChromaticClientProfiles.clear());
    }
}
