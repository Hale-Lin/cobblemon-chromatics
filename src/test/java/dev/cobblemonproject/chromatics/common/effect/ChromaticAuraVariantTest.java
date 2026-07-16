package dev.cobblemonproject.chromatics.common.effect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChromaticAuraVariantTest {
    @Test
    void resolvesAllFourStates() {
        assertEquals(ChromaticAuraVariant.NONE, ChromaticAuraVariant.resolve(false, false));
        assertEquals(ChromaticAuraVariant.SHINY, ChromaticAuraVariant.resolve(true, false));
        assertEquals(ChromaticAuraVariant.CHROMATIC, ChromaticAuraVariant.resolve(false, true));
        assertEquals(ChromaticAuraVariant.SHINY_CHROMATIC, ChromaticAuraVariant.resolve(true, true));
    }

    @Test
    void onlyPlainPokemonSkipShinyEffects() {
        assertFalse(ChromaticAuraVariant.NONE.usesShinyEffects());
        assertTrue(ChromaticAuraVariant.SHINY.usesShinyEffects());
        assertTrue(ChromaticAuraVariant.CHROMATIC.usesShinyEffects());
        assertTrue(ChromaticAuraVariant.SHINY_CHROMATIC.usesShinyEffects());
    }
}
