package dev.cobblemonproject.chromatics.client.render;

import com.cobblemon.mod.common.pokemon.Pokemon;

import java.util.Optional;

public final class ChromaticGuiRenderContext {
    private static final ThreadLocal<Pokemon> CURRENT = new ThreadLocal<>();

    private ChromaticGuiRenderContext() {
    }

    public static Optional<Pokemon> current() {
        return Optional.ofNullable(CURRENT.get());
    }

    public static void render(Pokemon pokemon, Runnable renderCall) {
        Pokemon previous = CURRENT.get();
        CURRENT.set(pokemon);
        try {
            renderCall.run();
        } finally {
            if (previous == null) {
                CURRENT.remove();
            } else {
                CURRENT.set(previous);
            }
        }
    }
}
