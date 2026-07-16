package dev.cobblemonproject.chromatics.common.effect;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.net.messages.client.effect.SpawnSnowstormEntityParticlePacket;
import dev.cobblemonproject.chromatics.CobblemonChromatics;
import dev.cobblemonproject.chromatics.common.chromatic.ChromaticProfileStorage;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.util.Identifier;

import java.util.List;

public final class ChromaticEffectEvents {
    private static final Identifier CHROMATIC_DOUBLE_RAINBOW_RING = Identifier.of(
        CobblemonChromatics.MOD_ID,
        "chromatic_double_rainbow_ring"
    );

    private ChromaticEffectEvents() {}

    public static void register() {
        CobblemonEvents.POKEMON_SENT_POST.subscribe(event -> {
            var pokemon = event.getPokemon();
            boolean chromatic = ChromaticProfileStorage.get(pokemon)
                .map(profile -> profile.chromatic())
                .orElse(false);
            if (!chromatic || pokemon.getShiny() || pokemon.isWild()) {
                return;
            }

            var entity = event.getPokemonEntity();
            var packet = new SpawnSnowstormEntityParticlePacket(
                CHROMATIC_DOUBLE_RAINBOW_RING,
                entity.getId(),
                List.of("shiny_particles", "middle"),
                null,
                null
            );
            packet.sendToPlayers(PlayerLookup.around(event.getLevel(), entity.getPos(), 64.0));
        });
    }
}
