package dev.cobblemonproject.chromatics.common.chromatic;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.Optional;

public final class ChromaticProfileStorage {
    private static final String ROOT_KEY = "cobblemon_chromatics";
    private static final String PROFILE_KEY = "profile";

    private ChromaticProfileStorage() {}

    public static ReadResult read(Pokemon pokemon) {
        NbtCompound data = pokemon.getPersistentData();
        if (!data.contains(ROOT_KEY, NbtElement.COMPOUND_TYPE)) {
            return ReadResult.missing();
        }

        NbtCompound root = data.getCompound(ROOT_KEY);
        if (!root.contains(PROFILE_KEY, NbtElement.COMPOUND_TYPE)) {
            return ReadResult.missing();
        }

        try {
            return ReadResult.valid(ChromaticProfileCodec.decode(root.getCompound(PROFILE_KEY)));
        } catch (RuntimeException exception) {
            return ReadResult.invalid(exception);
        }
    }

    public static Optional<ChromaticProfile> get(Pokemon pokemon) {
        return read(pokemon).profile();
    }

    public static void put(Pokemon pokemon, ChromaticProfile profile) {
        NbtCompound data = pokemon.getPersistentData();
        NbtCompound root = data.contains(ROOT_KEY, NbtElement.COMPOUND_TYPE)
            ? data.getCompound(ROOT_KEY)
            : new NbtCompound();
        root.put(PROFILE_KEY, ChromaticProfileCodec.encode(profile));
        data.put(ROOT_KEY, root);
        pokemon.onChange(null);
    }

    public enum ReadStatus {
        MISSING,
        VALID,
        INVALID
    }

    public record ReadResult(ReadStatus status, ChromaticProfile value, RuntimeException error) {
        public static ReadResult missing() {
            return new ReadResult(ReadStatus.MISSING, null, null);
        }

        public static ReadResult valid(ChromaticProfile profile) {
            return new ReadResult(ReadStatus.VALID, profile, null);
        }

        public static ReadResult invalid(RuntimeException error) {
            return new ReadResult(ReadStatus.INVALID, null, error);
        }

        public Optional<ChromaticProfile> profile() {
            return Optional.ofNullable(value);
        }
    }
}
