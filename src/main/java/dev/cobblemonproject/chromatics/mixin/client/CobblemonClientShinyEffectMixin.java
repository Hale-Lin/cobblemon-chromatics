package dev.cobblemonproject.chromatics.mixin.client;

import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.cobblemonproject.chromatics.client.effect.ChromaticAuraEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CobblemonClient.class, remap = false)
abstract class CobblemonClientShinyEffectMixin {
    @Redirect(
        method = "initialize$lambda$5",
        at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/pokemon/Pokemon;getShiny()Z")
    )
    private static boolean chromatics$includeChromaticInAmbientSparkles(Pokemon pokemon) {
        return ChromaticAuraEffects.usesShinyEffects(pokemon);
    }
}
