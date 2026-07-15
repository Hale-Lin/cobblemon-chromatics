package dev.cobblemonproject.chromatics.common.chromatic;

public record ChromaticProfile(int schemaVersion, boolean chromatic, long seed, HsvTransform baseHsv, HsvTransform layerHsv, String variant) {
    public static final int CURRENT_SCHEMA_VERSION = 2;
    public ChromaticProfile {
        if (schemaVersion != CURRENT_SCHEMA_VERSION || baseHsv == null || layerHsv == null || variant == null || variant.isBlank()) throw new IllegalArgumentException("Invalid chromatic profile");
    }
    public int stableHash() { return java.util.Objects.hash(chromatic, seed, baseHsv, layerHsv, variant); }
}
