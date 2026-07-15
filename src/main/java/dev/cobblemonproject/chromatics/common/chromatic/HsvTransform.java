package dev.cobblemonproject.chromatics.common.chromatic;

public record HsvTransform(float hueShiftDegrees, float saturationScale, float valueScale) {
    public HsvTransform {
        if (!Float.isFinite(hueShiftDegrees) || !Float.isFinite(saturationScale) || !Float.isFinite(valueScale)) throw new IllegalArgumentException("HSV values must be finite");
    }
}
