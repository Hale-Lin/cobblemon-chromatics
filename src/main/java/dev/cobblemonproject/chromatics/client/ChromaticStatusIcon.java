package dev.cobblemonproject.chromatics.client;

import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.cobblemonproject.chromatics.common.chromatic.ChromaticProfileStorage;
import net.minecraft.util.Identifier;

public enum ChromaticStatusIcon {
    NONE,
    SHINY,
    CHROMATIC,
    SHINY_CHROMATIC;

    public static ChromaticStatusIcon resolve(boolean shiny, boolean chromatic) {
        if (shiny && chromatic) {
            return SHINY_CHROMATIC;
        }
        if (chromatic) {
            return CHROMATIC;
        }
        if (shiny) {
            return SHINY;
        }
        return NONE;
    }

    public static ChromaticStatusIcon resolve(Pokemon pokemon) {
        boolean chromatic = ChromaticProfileStorage.get(pokemon)
            .map(profile -> profile.chromatic())
            .orElse(false);
        return resolve(pokemon.getShiny(), chromatic);
    }

    public boolean visible() {
        return this != NONE;
    }

    public Identifier texture() {
        return switch (this) {
            case CHROMATIC -> Identifier.of(
                "cobblemon_chromatics",
                "textures/gui/summary/icon_chromatic.png"
            );
            case SHINY_CHROMATIC -> Identifier.of(
                "cobblemon_chromatics",
                "textures/gui/summary/icon_shiny_chromatic.png"
            );
            case SHINY, NONE -> Identifier.of(
                "cobblemon",
                "textures/gui/summary/icon_shiny.png"
            );
        };
    }
}
