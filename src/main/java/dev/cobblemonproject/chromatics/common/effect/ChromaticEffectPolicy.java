package dev.cobblemonproject.chromatics.common.effect;

import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.cobblemonproject.chromatics.common.chromatic.ChromaticProfileStorage;

public final class ChromaticEffectPolicy {
    private ChromaticEffectPolicy() {}

    public static boolean usesShinyEffects(Pokemon pokemon) {
        return pokemon.getShiny() || ChromaticProfileStorage.get(pokemon)
            .map(profile -> profile.chromatic())
            .orElse(false);
    }
}
