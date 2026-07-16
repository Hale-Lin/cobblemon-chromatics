package dev.cobblemonproject.chromatics.mixin.effect;

import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.cobblemonproject.chromatics.common.effect.ChromaticEffectPolicy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PokemonPastureBlockEntity.class, remap = false)
abstract class PastureShinyEffectMixin {
    @Redirect(
        method = "tether$lambda$0",
        at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/pokemon/Pokemon;getShiny()Z")
    )
    private static boolean chromatics$includeChromaticInPastureAura(Pokemon pokemon) {
        return ChromaticEffectPolicy.usesShinyEffects(pokemon);
    }
}
