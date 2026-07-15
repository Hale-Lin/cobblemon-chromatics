package dev.cobblemonproject.chromatics.common.chromatic;

import com.cobblemon.mod.common.pokemon.FormData;

import java.util.Set;

public final class RegionalFormPolicy {
    private static final Set<String> EXCLUDED_LABELS = Set.of(
        "alolan_form",
        "galarian_form",
        "hisuian_form",
        "paldean_form"
    );

    private RegionalFormPolicy() {}

    public static boolean isRegional(FormData form) {
        return isRegionalLabels(form.getLabels());
    }

    static boolean isRegionalLabels(Set<String> labels) {
        return labels.stream().anyMatch(EXCLUDED_LABELS::contains);
    }
}
