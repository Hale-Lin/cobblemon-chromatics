package dev.cobblemonproject.chromatics.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ChromaticsConfigTest {
    @Test
    void defaultsAreValid() {
        ChromaticsConfig config = new ChromaticsConfig();

        assertDoesNotThrow(config::validate);
        assertEquals(1, config.schemaVersion());
        assertFalse(config.developmentLogging());
    }
}
