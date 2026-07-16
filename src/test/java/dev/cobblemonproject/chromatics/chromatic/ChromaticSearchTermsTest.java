package dev.cobblemonproject.chromatics.chromatic;

import dev.cobblemonproject.chromatics.client.ChromaticSearchTerms;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChromaticSearchTermsTest {
    @Test void removesChromaticTermAndPreservesCobblemonTerms() {
        ChromaticSearchTerms terms = ChromaticSearchTerms.parse("charizard chromatic shiny lvl=50");
        assertEquals("charizard shiny lvl=50", terms.cobblemonQuery());
        assertEquals(ChromaticSearchTerms.Requirement.CHROMATIC, terms.requirement());
        assertTrue(terms.matches(true));
        assertFalse(terms.matches(false));
    }

    @Test void supportsPluralAndNegatedTermsCaseInsensitively() {
        ChromaticSearchTerms terms = ChromaticSearchTerms.parse("!ChRoMaTiCs held_item");
        assertEquals("held_item", terms.cobblemonQuery());
        assertEquals(ChromaticSearchTerms.Requirement.NOT_CHROMATIC, terms.requirement());
        assertFalse(terms.matches(true));
        assertTrue(terms.matches(false));
    }

    @Test void leavesQueriesWithoutChromaticTermsUntouched() {
        ChromaticSearchTerms terms = ChromaticSearchTerms.parse("pikachu shiny");
        assertEquals("pikachu shiny", terms.cobblemonQuery());
        assertEquals(ChromaticSearchTerms.Requirement.ANY, terms.requirement());
    }
}
