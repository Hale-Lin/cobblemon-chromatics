package dev.cobblemonproject.chromatics.common.chromatic;

import dev.cobblemonproject.chromatics.config.ChromaticsConfig;
import java.util.UUID;

public final class ChromaticProfileFactory {
    private ChromaticProfileFactory() {}

    public static ChromaticProfile create(UUID id, ChromaticsConfig config) {
        return create(id, config, 0, true);
    }

    public static ChromaticProfile create(UUID id, ChromaticsConfig config, int chromaticParentCount) {
        return create(id, config, chromaticParentCount, true);
    }

    public static ChromaticProfile create(UUID id, ChromaticsConfig config, int chromaticParentCount, boolean eligible) {
        long seed = mix(id.getMostSignificantBits() ^ Long.rotateLeft(id.getLeastSignificantBits(), 17));
        HsvTransform baseHsv = transform(seed, 0x2A119D71E8B64E3FL);
        HsvTransform layerHsv = contrastingLayerTransform(seed, baseHsv);
        boolean chromatic = false;
        if (eligible) {
            int rolls = 1 + chromaticParentCount * config.chromaticParentBonusRolls();
            for (int roll = 0; roll < rolls && !chromatic; roll++) {
                chromatic = Long.remainderUnsigned(
                    mix(seed ^ 0x31F4B6A9C217D85EL ^ roll),
                    config.chromaticChanceDenominator()
                ) == 0L;
            }
        }
        return new ChromaticProfile(
            ChromaticProfile.CURRENT_SCHEMA_VERSION,
            chromatic,
            seed,
            baseHsv,
            layerHsv,
            "chromatics:type_a"
        );
    }

    public static ChromaticProfile createForced(UUID id, ChromaticsConfig config) {
        ChromaticProfile generated = create(id, config, 0, false);
        return new ChromaticProfile(
            generated.schemaVersion(),
            true,
            generated.seed(),
            generated.baseHsv(),
            generated.layerHsv(),
            generated.variant()
        );
    }

    private static HsvTransform transform(long seed, long domain) {
        long value = mix(seed ^ domain);
        return new HsvTransform(
            ((value & 65535L) / 65535F) * 360F - 180F,
            0.82F + (((value >>> 16) & 65535L) / 65535F) * 0.46F,
            0.88F + (((value >>> 32) & 65535L) / 65535F) * 0.24F
        );
    }

    private static HsvTransform contrastingLayerTransform(long seed, HsvTransform base) {
        long value = mix(seed ^ 0x6D5C9E2F8314A7B1L);
        float hueSeparation = 70F + fraction(value, 0) * 80F;
        float hueDirection = ((value >>> 63) & 1L) == 0L ? -1F : 1F;
        float saturation = base.saturationScale() >= 1F
            ? 0.72F + fraction(value, 16) * 0.16F
            : 1.12F + fraction(value, 16) * 0.18F;
        float brightness = base.valueScale() >= 1F
            ? 0.84F + fraction(value, 32) * 0.10F
            : 1.06F + fraction(value, 32) * 0.10F;
        return new HsvTransform(
            wrapHue(base.hueShiftDegrees() + hueDirection * hueSeparation),
            saturation,
            brightness
        );
    }

    private static float fraction(long value, int shift) {
        return ((value >>> shift) & 65535L) / 65535F;
    }

    private static float wrapHue(float degrees) {
        float wrapped = degrees % 360F;
        if (wrapped >= 180F) {
            wrapped -= 360F;
        } else if (wrapped < -180F) {
            wrapped += 360F;
        }
        return wrapped;
    }

    private static long mix(long input) {
        long value = input + 0x9E3779B97F4A7C15L;
        value = (value ^ (value >>> 30)) * 0xBF58476D1CE4E5B9L;
        value = (value ^ (value >>> 27)) * 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }
}
