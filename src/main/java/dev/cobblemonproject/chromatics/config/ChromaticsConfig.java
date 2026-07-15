package dev.cobblemonproject.chromatics.config;

public final class ChromaticsConfig {
    private int schemaVersion = 1;
    private boolean developmentLogging = false;
    private int chromaticChanceDenominator = 4096;
    // Zero preserves the current rule: chromatic parents do not affect offspring odds.
    private int chromaticParentBonusRolls = 0;

    public int schemaVersion() { return schemaVersion; }
    public boolean developmentLogging() { return developmentLogging; }
    public int chromaticChanceDenominator() { return chromaticChanceDenominator; }
    public int chromaticParentBonusRolls() { return chromaticParentBonusRolls; }

    public void validate() {
        if (schemaVersion != 1) throw new IllegalArgumentException("Unsupported configuration schema: " + schemaVersion);
        if (chromaticChanceDenominator < 1) throw new IllegalArgumentException("chromaticChanceDenominator must be positive");
        if (chromaticParentBonusRolls < 0) throw new IllegalArgumentException("chromaticParentBonusRolls cannot be negative");
    }
}
