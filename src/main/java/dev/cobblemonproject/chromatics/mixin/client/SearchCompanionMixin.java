package dev.cobblemonproject.chromatics.mixin.client;

import com.cobblemon.mod.common.api.storage.pc.search.PokemonFilter;
import com.cobblemon.mod.common.api.storage.pc.search.Search;
import dev.cobblemonproject.chromatics.client.ChromaticSearchTerms;
import dev.cobblemonproject.chromatics.common.chromatic.ChromaticProfileStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mixin(value = Search.Companion.class, remap = false)
abstract class SearchCompanionMixin {
    @Inject(method = "of", at = @At("HEAD"), cancellable = true)
    private void chromatics$addChromaticFilter(String query, CallbackInfoReturnable<Search> callback) {
        ChromaticSearchTerms terms = ChromaticSearchTerms.parse(query);
        if (terms.requirement() == ChromaticSearchTerms.Requirement.ANY) {
            return;
        }

        Search base = ((Search.Companion)(Object)this).of(terms.cobblemonQuery());
        PokemonFilter combined = pokemon -> base.passes(pokemon) && terms.matches(
            ChromaticProfileStorage.get(pokemon).map(profile -> profile.chromatic()).orElse(false)
        );
        callback.setReturnValue(new Search(Set.of(combined), new HashSet<UUID>(), new HashSet<UUID>()));
    }
}
