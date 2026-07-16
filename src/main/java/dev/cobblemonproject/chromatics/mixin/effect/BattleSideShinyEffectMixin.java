package dev.cobblemonproject.chromatics.mixin.effect;

import com.cobblemon.mod.common.battles.BattleSide;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.cobblemonproject.chromatics.common.effect.ChromaticEffectPolicy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BattleSide.class, remap = false)
abstract class BattleSideShinyEffectMixin {
    @Redirect(method = "playCries", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/pokemon/Pokemon;getShiny()Z"))
    private boolean chromatics$includeChromaticInBattleAura(Pokemon pokemon) {
        return ChromaticEffectPolicy.usesShinyEffects(pokemon);
    }
}
