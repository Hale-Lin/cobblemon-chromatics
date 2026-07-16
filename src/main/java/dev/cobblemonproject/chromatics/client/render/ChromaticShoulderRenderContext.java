package dev.cobblemonproject.chromatics.client.render;

import dev.cobblemonproject.chromatics.client.ChromaticClientProfiles;
import dev.cobblemonproject.chromatics.common.chromatic.ChromaticProfile;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.UUID;

public final class ChromaticShoulderRenderContext {
    private static final ThreadLocal<Deque<Optional<UUID>>> CURRENT = new ThreadLocal<>();

    private ChromaticShoulderRenderContext() {
    }

    public static void push(UUID pokemonId) {
        Deque<Optional<UUID>> stack = CURRENT.get();
        if (stack == null) {
            stack = new ArrayDeque<>();
            CURRENT.set(stack);
        }
        stack.addLast(Optional.ofNullable(pokemonId));
    }

    public static void pop() {
        Deque<Optional<UUID>> stack = CURRENT.get();
        if (stack == null) {
            return;
        }
        if (!stack.isEmpty()) {
            stack.removeLast();
        }
        if (stack.isEmpty()) {
            CURRENT.remove();
        }
    }

    public static Optional<ChromaticProfile> profile() {
        Deque<Optional<UUID>> stack = CURRENT.get();
        if (stack == null || stack.isEmpty()) {
            return Optional.empty();
        }
        return stack.peekLast().flatMap(ChromaticClientProfiles::get);
    }
}
