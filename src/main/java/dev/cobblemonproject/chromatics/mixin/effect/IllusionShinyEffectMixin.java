package dev.cobblemonproject.chromatics.mixin.effect;

import com.cobblemon.mod.common.entity.pokemon.effects.IllusionEffect;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.cobblemonproject.chromatics.common.effect.ChromaticEffectPolicy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = IllusionEffect.class, remap = false)
abstract class IllusionShinyEffectMixin {
    @Redirect(
        method = "revert$lambda$0",
        at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/pokemon/Pokemon;getShiny()Z")
    )
    private static boolean chromatics$includeChromaticInIllusionAura(Pokemon pokemon) {
        return ChromaticEffectPolicy.usesShinyEffects(pokemon);
    }
}
