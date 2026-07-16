package dev.cobblemonproject.chromatics.common.effect;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChromaticAmbientAuraResourceTest {
    private static final Path PARTICLE_DIR = Path.of(
        "src/main/resources/assets/cobblemon_chromatics/bedrock/particles/chromatic_aura"
    );

    @Test
    void ambientAuraRootsPreserveTheirSoundContractsAndRingLayouts() throws IOException {
        assertAura(
            "ambient_chromatic_double_rainbow_ring.particle.json",
            "cobblemon_chromatics:ambient_chromatic_double_rainbow_ring",
            "q.sound",
            21,
            "native_inner_point_02",
            "cobblemon:shiny_ring2"
        );
        assertAura(
            "ambient_shiny_chromatic_compact_fixed_ring.particle.json",
            "cobblemon_chromatics:ambient_shiny_chromatic_compact_fixed_ring",
            "q.sound",
            13,
            "compact_point_02",
            "cobblemon_chromatics:compact_fixed_ring_point_02"
        );
        assertAura(
            "wild_ambient_chromatic_double_rainbow_ring.particle.json",
            "cobblemon_chromatics:wild_ambient_chromatic_double_rainbow_ring",
            "particle.wild_shiny_ambient_chime",
            21,
            "native_inner_point_02",
            "cobblemon:shiny_ring2"
        );
        assertAura(
            "wild_ambient_shiny_chromatic_compact_fixed_ring.particle.json",
            "cobblemon_chromatics:wild_ambient_shiny_chromatic_compact_fixed_ring",
            "particle.wild_shiny_ambient_chime",
            13,
            "compact_point_02",
            "cobblemon_chromatics:compact_fixed_ring_point_02"
        );
    }

    @Test
    void pureChromaticUsesDoubleRainbowWhileCombinedUsesCompactFixedPalette() throws IOException {
        String rainbowTexture = "textures/particles/generic/sparkle/shinesparkle_rainbow";
        Set<String> fixedPalette = Set.of(
            "cobblemon_chromatics:textures/particles/chromatic_shinesparkle_cyan",
            "cobblemon_chromatics:textures/particles/chromatic_shinesparkle_magenta",
            "cobblemon_chromatics:textures/particles/chromatic_shinesparkle_gold",
            "cobblemon_chromatics:textures/particles/chromatic_shinesparkle_violet"
        );

        for (int index = 1; index <= 12; index++) {
            assertEquals(
                rainbowTexture,
                particle("double_rainbow_outer_point_" + String.format("%02d", index) + ".particle.json")
                    .getAsJsonObject("description")
                    .getAsJsonObject("basic_render_parameters")
                    .get("texture")
                    .getAsString()
            );
        }
        for (String file : Set.of(
            "chromatic_double_rainbow_ring.particle.json",
            "wild_chromatic_double_rainbow_ring.particle.json",
            "ambient_chromatic_double_rainbow_ring.particle.json",
            "wild_ambient_chromatic_double_rainbow_ring.particle.json"
        )) {
            assertEquals(
                rainbowTexture,
                particle(file)
                    .getAsJsonObject("description")
                    .getAsJsonObject("basic_render_parameters")
                    .get("texture")
                    .getAsString()
            );
        }
        for (int index = 2; index <= 12; index++) {
            assertTrue(
                fixedPalette.contains(
                    particle("compact_fixed_ring_point_" + String.format("%02d", index) + ".particle.json")
                        .getAsJsonObject("description")
                        .getAsJsonObject("basic_render_parameters")
                        .get("texture")
                        .getAsString()
                )
            );
        }
        assertTrue(
            fixedPalette.contains(
                particle("shiny_chromatic_compact_fixed_ring.particle.json")
                    .getAsJsonObject("description")
                    .getAsJsonObject("basic_render_parameters")
                    .get("texture")
                    .getAsString()
            )
        );
    }

    @Test
    void combinedCompactRadiusRemainsBelowPureChromaticOuterRing() throws IOException {
        Set<String> compactRoots = Set.of(
            "shiny_chromatic_compact_fixed_ring.particle.json",
            "wild_shiny_chromatic_compact_fixed_ring.particle.json",
            "ambient_shiny_chromatic_compact_fixed_ring.particle.json",
            "wild_ambient_shiny_chromatic_compact_fixed_ring.particle.json"
        );

        for (String file : compactRoots) {
            assertRadiusMultiplier(file, "1.1");
        }
        for (int index = 2; index <= 12; index++) {
            assertRadiusMultiplier("compact_fixed_ring_point_" + String.format("%02d", index) + ".particle.json", "1.1");
        }
        for (int index = 1; index <= 12; index++) {
            assertRadiusMultiplier("double_rainbow_outer_point_" + String.format("%02d", index) + ".particle.json", "1.6");
        }

        for (String file : Set.of(
            "chromatic_double_rainbow_ring.particle.json",
            "wild_chromatic_double_rainbow_ring.particle.json",
            "ambient_chromatic_double_rainbow_ring.particle.json",
            "wild_ambient_chromatic_double_rainbow_ring.particle.json"
        )) {
            for (var position : relativePositions(file)) {
                assertFalse(position.getAsString().contains("* 1.1 *"));
                assertFalse(position.getAsString().contains("* 1.6 *"));
            }
        }
    }

    private static void assertAura(
        String file,
        String identifier,
        String soundEvent,
        int creationEventCount,
        String childEvent,
        String childEffect
    ) throws IOException {
        JsonObject effect = particle(file);

        assertEquals(identifier, effect.getAsJsonObject("description").get("identifier").getAsString());
        assertEquals(
            soundEvent,
            effect.getAsJsonObject("events")
                .getAsJsonObject("event_sound")
                .getAsJsonObject("sound_effect")
                .get("event_name")
                .getAsString()
        );
        assertEquals(
            creationEventCount,
            effect.getAsJsonObject("components")
                .getAsJsonObject("minecraft:emitter_lifetime_events")
                .getAsJsonArray("creation_event")
                .size()
        );
        assertEquals(
            childEffect,
            effect.getAsJsonObject("events")
                .getAsJsonObject(childEvent)
                .getAsJsonObject("particle_effect")
                .get("effect")
                .getAsString()
        );
    }

    private static JsonObject particle(String file) throws IOException {
        return JsonParser.parseString(Files.readString(PARTICLE_DIR.resolve(file)))
            .getAsJsonObject()
            .getAsJsonObject("particle_effect");
    }

    private static void assertRadiusMultiplier(String file, String multiplier) throws IOException {
        for (var position : relativePositions(file)) {
            assertTrue(
                position.getAsString().contains("* " + multiplier + " * math.clamp(q.entity_size*3,2,10)"),
                file + " must use a " + multiplier + "x radius"
            );
        }
    }

    private static Iterable<com.google.gson.JsonElement> relativePositions(String file) throws IOException {
        return particle(file)
            .getAsJsonObject("components")
            .getAsJsonObject("minecraft:particle_motion_parametric")
            .getAsJsonArray("relative_position");
    }
}
