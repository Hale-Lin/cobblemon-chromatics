package dev.cobblemonproject.chromatics.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public record ChromaticSearchTerms(String cobblemonQuery, Requirement requirement) {
    public enum Requirement {
        ANY,
        CHROMATIC,
        NOT_CHROMATIC
    }

    public static ChromaticSearchTerms parse(String query) {
        List<String> remaining = new ArrayList<>();
        Requirement requirement = Requirement.ANY;
        for (String token : query.trim().split("\\s+")) {
            String normalized = token.toLowerCase(Locale.ROOT);
            if (normalized.equals("chromatic") || normalized.equals("chromatics")) {
                requirement = Requirement.CHROMATIC;
            } else if (normalized.equals("!chromatic") || normalized.equals("!chromatics")) {
                requirement = Requirement.NOT_CHROMATIC;
            } else if (!token.isBlank()) {
                remaining.add(token);
            }
        }
        return new ChromaticSearchTerms(String.join(" ", remaining), requirement);
    }

    public boolean matches(boolean chromatic) {
        return switch (requirement) {
            case ANY -> true;
            case CHROMATIC -> chromatic;
            case NOT_CHROMATIC -> !chromatic;
        };
    }
}
