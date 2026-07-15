package dev.cobblemonproject.chromatics.chromatic;

import dev.cobblemonproject.chromatics.client.ChromaticStatusIcon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChromaticStatusIconTest {
    @Test void resolvesAllFourStatusCombinations() {
        assertEquals(ChromaticStatusIcon.NONE, ChromaticStatusIcon.resolve(false, false));
        assertEquals(ChromaticStatusIcon.SHINY, ChromaticStatusIcon.resolve(true, false));
        assertEquals(ChromaticStatusIcon.CHROMATIC, ChromaticStatusIcon.resolve(false, true));
        assertEquals(ChromaticStatusIcon.SHINY_CHROMATIC, ChromaticStatusIcon.resolve(true, true));
    }
}
