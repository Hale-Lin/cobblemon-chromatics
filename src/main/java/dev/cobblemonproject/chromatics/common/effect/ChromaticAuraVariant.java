package dev.cobblemonproject.chromatics.common.effect;

public enum ChromaticAuraVariant {
    NONE,
    SHINY,
    CHROMATIC,
    SHINY_CHROMATIC;

    public static ChromaticAuraVariant resolve(boolean shiny, boolean chromatic) {
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

    public boolean usesShinyEffects() {
        return this != NONE;
    }
}
