package dev.cobblemonproject.chromatics.client;

import dev.cobblemonproject.chromatics.common.chromatic.ChromaticProfile;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ChromaticClientProfiles {
    private static final Map<UUID, ChromaticProfile> PROFILES = new ConcurrentHashMap<>();
    private ChromaticClientProfiles() {}
    public static void put(UUID id, ChromaticProfile profile) { PROFILES.put(id,profile); }
    public static Optional<ChromaticProfile> get(UUID id) { return Optional.ofNullable(PROFILES.get(id)); }
    public static void clear() { PROFILES.clear(); }
}
