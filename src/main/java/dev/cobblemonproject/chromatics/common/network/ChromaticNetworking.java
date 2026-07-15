package dev.cobblemonproject.chromatics.common.network;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import dev.cobblemonproject.chromatics.common.chromatic.ChromaticProfileStorage;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class ChromaticNetworking {
    private ChromaticNetworking() {}
    public static void register() {
        PayloadTypeRegistry.playS2C().register(ChromaticProfileSyncPayload.ID, ChromaticProfileSyncPayload.CODEC);
        EntityTrackingEvents.START_TRACKING.register((entity, player) -> { if(entity instanceof PokemonEntity pokemonEntity) ChromaticProfileStorage.get(pokemonEntity.getPokemon()).ifPresent(profile -> ServerPlayNetworking.send(player,new ChromaticProfileSyncPayload(pokemonEntity.getPokemon().getUuid(),profile))); });
    }

    public static void sync(PokemonEntity entity) {
        ChromaticProfileStorage.get(entity.getPokemon()).ifPresent(profile -> {
            ChromaticProfileSyncPayload payload = new ChromaticProfileSyncPayload(entity.getPokemon().getUuid(), profile);
            PlayerLookup.tracking(entity).forEach(player -> ServerPlayNetworking.send(player, payload));
        });
    }
}
