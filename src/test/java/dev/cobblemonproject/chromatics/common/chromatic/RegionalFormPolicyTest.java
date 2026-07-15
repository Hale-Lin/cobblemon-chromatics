package dev.cobblemonproject.chromatics.common.chromatic;

import dev.cobblemonproject.chromatics.config.ChromaticsConfig;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegionalFormPolicyTest {
    @Test
    void excludesKnownRegionalFormLabels() {
        assertTrue(RegionalFormPolicy.isRegionalLabels(Set.of("gen7", "alolan_form")));
        assertTrue(RegionalFormPolicy.isRegionalLabels(Set.of("gen8", "galarian_form")));
        assertTrue(RegionalFormPolicy.isRegionalLabels(Set.of("gen8a", "hisuian_form")));
        assertTrue(RegionalFormPolicy.isRegionalLabels(Set.of("gen9", "paldean_form")));
    }

    @Test
    void doesNotExcludeOrdinaryOriginLabels() {
        assertFalse(RegionalFormPolicy.isRegionalLabels(Set.of("gen1", "kantonian_form")));
        assertFalse(RegionalFormPolicy.isRegionalLabels(Set.of("gen2", "johtonian_form")));
        assertFalse(RegionalFormPolicy.isRegionalLabels(Set.of("gen5", "unovan_form")));
        assertFalse(RegionalFormPolicy.isRegionalLabels(Set.of("gen5")));
    }

    @Test
    void ineligiblePokemonCanNeverBeChromatic() {
        ChromaticProfile profile = ChromaticProfileFactory.create(
            UUID.fromString("0d7ff040-d8b1-4b65-8436-7db416dc7116"),
            new ChromaticsConfig(),
            0,
            false
        );
        assertFalse(profile.chromatic());
    }
}
