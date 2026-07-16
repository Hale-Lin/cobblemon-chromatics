package dev.cobblemonproject.chromatics.client.effect;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.cobblemonproject.chromatics.CobblemonChromatics;
import dev.cobblemonproject.chromatics.client.ChromaticClientProfiles;
import dev.cobblemonproject.chromatics.common.effect.ChromaticAuraVariant;
import net.minecraft.util.Identifier;

public final class ChromaticAuraEffects {
    private static final Identifier SHINY_RING = Identifier.of("cobblemon", "shiny_ring");
    private static final String WILD_SHINY_RING = "cobblemon:wild_shiny_ring";
    private static final String AMBIENT_SHINY_SPARKLE = "cobblemon:ambient_shiny_sparkle";
    private static final String WILD_AMBIENT_SHINY_SPARKLE = "cobblemon:shiny_sparkle_ambient_wild";
    private static final Identifier CHROMATIC_DOUBLE_RAINBOW_RING = id("chromatic_double_rainbow_ring");
    private static final Identifier SHINY_CHROMATIC_COMPACT_FIXED_RING = id("shiny_chromatic_compact_fixed_ring");
    private static final String WILD_CHROMATIC_DOUBLE_RAINBOW_RING = id("wild_chromatic_double_rainbow_ring").toString();
    private static final String WILD_SHINY_CHROMATIC_COMPACT_FIXED_RING = id("wild_shiny_chromatic_compact_fixed_ring").toString();
    private static final String AMBIENT_CHROMATIC_DOUBLE_RAINBOW_RING = id("ambient_chromatic_double_rainbow_ring").toString();
    private static final String AMBIENT_SHINY_CHROMATIC_COMPACT_FIXED_RING = id("ambient_shiny_chromatic_compact_fixed_ring").toString();
    private static final String WILD_AMBIENT_CHROMATIC_DOUBLE_RAINBOW_RING = id("wild_ambient_chromatic_double_rainbow_ring").toString();
    private static final String WILD_AMBIENT_SHINY_CHROMATIC_COMPACT_FIXED_RING = id("wild_ambient_shiny_chromatic_compact_fixed_ring").toString();

    private ChromaticAuraEffects() {}

    public static boolean usesShinyEffects(Pokemon pokemon) {
        return variant(pokemon).usesShinyEffects();
    }

    public static Identifier routePacketEffect(Identifier effectId, PokemonEntity entity) {
        ChromaticAuraVariant variant = variant(entity.getPokemon());
        if (!SHINY_RING.equals(effectId) || !isChromaticVariant(variant)) {
            return effectId;
        }
        return variant == ChromaticAuraVariant.SHINY_CHROMATIC
            ? SHINY_CHROMATIC_COMPACT_FIXED_RING
            : CHROMATIC_DOUBLE_RAINBOW_RING;
    }

    public static String routeWildEffect(String effectId, PokemonEntity entity) {
        return routeStringEffect(
            effectId,
            entity,
            WILD_SHINY_RING,
            WILD_CHROMATIC_DOUBLE_RAINBOW_RING,
            WILD_SHINY_CHROMATIC_COMPACT_FIXED_RING
        );
    }

    public static String routeAmbientEffect(String effectId, PokemonEntity entity) {
        return routeStringEffect(
            effectId,
            entity,
            AMBIENT_SHINY_SPARKLE,
            AMBIENT_CHROMATIC_DOUBLE_RAINBOW_RING,
            AMBIENT_SHINY_CHROMATIC_COMPACT_FIXED_RING
        );
    }

    public static String routeWildAmbientEffect(String effectId, PokemonEntity entity) {
        return routeStringEffect(
            effectId,
            entity,
            WILD_AMBIENT_SHINY_SPARKLE,
            WILD_AMBIENT_CHROMATIC_DOUBLE_RAINBOW_RING,
            WILD_AMBIENT_SHINY_CHROMATIC_COMPACT_FIXED_RING
        );
    }

    private static ChromaticAuraVariant variant(Pokemon pokemon) {
        return ChromaticAuraVariant.resolve(pokemon.getShiny(), isChromatic(pokemon));
    }

    private static boolean isChromaticVariant(ChromaticAuraVariant variant) {
        return variant == ChromaticAuraVariant.CHROMATIC
            || variant == ChromaticAuraVariant.SHINY_CHROMATIC;
    }

    private static String routeStringEffect(
        String effectId,
        PokemonEntity entity,
        String nativeEffect,
        String chromaticEffect,
        String shinyChromaticEffect
    ) {
        ChromaticAuraVariant variant = variant(entity.getPokemon());
        if (!nativeEffect.equals(effectId) || !isChromaticVariant(variant)) {
            return effectId;
        }
        return variant == ChromaticAuraVariant.SHINY_CHROMATIC
            ? shinyChromaticEffect
            : chromaticEffect;
    }

    private static boolean isChromatic(Pokemon pokemon) {
        return ChromaticClientProfiles.get(pokemon.getUuid())
            .map(profile -> profile.chromatic())
            .orElse(false);
    }

    private static Identifier id(String path) {
        return Identifier.of(CobblemonChromatics.MOD_ID, path);
    }
}
