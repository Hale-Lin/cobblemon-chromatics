package dev.cobblemonproject.chromatics.mixin.effect;

import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.cobblemonproject.chromatics.common.effect.ChromaticEffectPolicy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EmptyPokeBallEntity.class, remap = false)
abstract class EmptyPokeBallShinyEffectMixin {
    @Redirect(
        method = "breakFree$lambda$3",
        at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/pokemon/Pokemon;getShiny()Z")
    )
    private static boolean chromatics$includeChromaticInBreakFreeAura(Pokemon pokemon) {
        return ChromaticEffectPolicy.usesShinyEffects(pokemon);
    }
}
