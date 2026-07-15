package dev.cobblemonproject.chromatics.common.chromatic;

public final class ChromaticRenderPolicy {
    private ChromaticRenderPolicy() {}

    public static boolean transformsBase(ChromaticProfile profile) {
        return profile.chromatic();
    }

    public static boolean transformsLayer(ChromaticProfile profile) {
        return profile.chromatic();
    }
}
