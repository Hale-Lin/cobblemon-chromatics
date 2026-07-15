package dev.cobblemonproject.chromatics.common.chromatic;

import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.cobblemonproject.chromatics.CobblemonChromatics;
import dev.cobblemonproject.chromatics.config.ChromaticsConfig;

public final class ChromaticLifecycle {
    private static ChromaticsConfig config;

    private ChromaticLifecycle() {}

    public static void register(ChromaticsConfig value) {
        config = value;
    }

    public static void ensureProfile(Pokemon pokemon) {
        if (pokemon.isClient$common()) {
            return;
        }

        ChromaticProfileStorage.ReadResult result = ChromaticProfileStorage.read(pokemon);
        if (result.status() == ChromaticProfileStorage.ReadStatus.VALID) {
            return;
        }
        if (result.status() == ChromaticProfileStorage.ReadStatus.INVALID) {
            CobblemonChromatics.LOGGER.error(
                "Chromatic profile for Pokemon {} is invalid; preserving stored data without regenerating",
                pokemon.getUuid(),
                result.error()
            );
            return;
        }

        ChromaticProfileStorage.put(
            pokemon,
            ChromaticProfileFactory.create(
                pokemon.getUuid(),
                config,
                0,
                !RegionalFormPolicy.isRegional(pokemon.getForm())
            )
        );
    }

    public static ForceResult force(Pokemon pokemon, boolean chromatic) {
        if (pokemon.isClient$common()) {
            return ForceResult.CLIENT_SIDE;
        }
        if (chromatic && RegionalFormPolicy.isRegional(pokemon.getForm())) {
            return ForceResult.REGIONAL_FORM;
        }
        ChromaticProfileStorage.ReadResult stored = ChromaticProfileStorage.read(pokemon);
        if (stored.status() == ChromaticProfileStorage.ReadStatus.INVALID) {
            return ForceResult.INVALID_PROFILE;
        }
        ChromaticProfile base = stored.profile().orElseGet(
            () -> ChromaticProfileFactory.createForced(pokemon.getUuid(), config)
        );
        ChromaticProfileStorage.put(pokemon, new ChromaticProfile(
            base.schemaVersion(),
            chromatic,
            base.seed(),
            base.baseHsv(),
            base.layerHsv(),
            base.variant()
        ));
        return ForceResult.SUCCESS;
    }

    public enum ForceResult {
        SUCCESS,
        REGIONAL_FORM,
        INVALID_PROFILE,
        CLIENT_SIDE
    }
}
