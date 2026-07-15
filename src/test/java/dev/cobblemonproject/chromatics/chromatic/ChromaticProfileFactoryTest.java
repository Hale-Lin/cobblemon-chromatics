package dev.cobblemonproject.chromatics.chromatic;

import dev.cobblemonproject.chromatics.common.chromatic.ChromaticProfile;
import dev.cobblemonproject.chromatics.common.chromatic.ChromaticProfileFactory;
import dev.cobblemonproject.chromatics.common.chromatic.ChromaticRenderPolicy;
import dev.cobblemonproject.chromatics.common.chromatic.HsvTransform;
import dev.cobblemonproject.chromatics.config.ChromaticsConfig;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChromaticProfileFactoryTest {
    @Test void uuidDerivationIsStable() {
        UUID id = UUID.fromString("1d31e670-05a7-4f91-91c5-82e6d8f33b71");
        ChromaticProfile first = ChromaticProfileFactory.create(id, new ChromaticsConfig());
        ChromaticProfile second = ChromaticProfileFactory.create(id, new ChromaticsConfig());
        assertEquals(first, second);
    }
    @Test void chromaticRenderingTransformsBaseAndLayers() {
        HsvTransform hsv = new HsvTransform(10, 1, 1);
        ChromaticProfile profile = new ChromaticProfile(2, true, 4L, hsv, hsv, "chromatics:type_a");
        assertTrue(ChromaticRenderPolicy.transformsBase(profile));
        assertTrue(ChromaticRenderPolicy.transformsLayer(profile));
    }
    @Test void generationDoesNotRequireAConfiguredLayer() {
        ChromaticsConfig config = new ChromaticsConfig();
        setChanceDenominator(config, 1);
        ChromaticProfile profile = ChromaticProfileFactory.create(new UUID(0, 0), config, 0, true);
        assertTrue(profile.chromatic());
        assertEquals(ChromaticProfile.CURRENT_SCHEMA_VERSION, profile.schemaVersion());
    }
    @Test void shinyChromaticTransformsBaseAndEveryLayer() {
        HsvTransform hsv = new HsvTransform(10, 1, 1);
        ChromaticProfile profile = new ChromaticProfile(2, true, 4L, hsv, hsv, "chromatics:type_a");
        assertTrue(ChromaticRenderPolicy.transformsBase(profile));
        assertTrue(ChromaticRenderPolicy.transformsLayer(profile));
    }
    @Test void nonChromaticProfileDoesNotTransformAnyPass() {
        HsvTransform hsv = new HsvTransform(10, 1, 1);
        ChromaticProfile profile = new ChromaticProfile(2, false, 4L, hsv, hsv, "chromatics:type_a");
        assertFalse(ChromaticRenderPolicy.transformsBase(profile));
        assertFalse(ChromaticRenderPolicy.transformsLayer(profile));
    }
    @Test void forcedProfilesAreChromaticAndRespectShinyScope() {
        ChromaticsConfig config = new ChromaticsConfig();
        ChromaticProfile profile = ChromaticProfileFactory.createForced(new UUID(1, 2), config);
        assertTrue(profile.chromatic());
        assertEquals(ChromaticProfile.CURRENT_SCHEMA_VERSION, profile.schemaVersion());
    }
    @Test void layerTransformAlwaysExistsAndContrastsWithBase() {
        ChromaticsConfig config = new ChromaticsConfig();
        for (int sample = 0; sample < 1024; sample++) {
            ChromaticProfile profile = ChromaticProfileFactory.create(
                new UUID(0x19D3A7B5L, sample),
                config,
                0,
                false
            );
            HsvTransform base = profile.baseHsv();
            HsvTransform layer = profile.layerHsv();
            assertNotEquals(base, layer);
            assertTrue(angularDistance(base.hueShiftDegrees(), layer.hueShiftDegrees()) >= 69.99F);
            assertTrue(Math.abs(base.saturationScale() - layer.saturationScale()) >= 0.119F);
            assertTrue(Math.abs(base.valueScale() - layer.valueScale()) >= 0.059F);
        }
    }
    private static float angularDistance(float first, float second) {
        float distance = Math.abs(first - second) % 360F;
        return Math.min(distance, 360F - distance);
    }
    private static void setChanceDenominator(ChromaticsConfig config, int value) {
        try {
            var field = ChromaticsConfig.class.getDeclaredField("chromaticChanceDenominator");
            field.setAccessible(true);
            field.setInt(config, value);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }
}
