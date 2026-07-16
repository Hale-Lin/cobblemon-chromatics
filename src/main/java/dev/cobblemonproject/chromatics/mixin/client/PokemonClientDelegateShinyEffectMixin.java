package dev.cobblemonproject.chromatics.mixin.client;

import com.cobblemon.mod.common.client.entity.PokemonClientDelegate;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.cobblemonproject.chromatics.client.effect.ChromaticAuraEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PokemonClientDelegate.class, remap = false)
abstract class PokemonClientDelegateShinyEffectMixin {
    @Redirect(
        method = {"playWildShinySounds", "onSyncedDataUpdated"},
        at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/pokemon/Pokemon;getShiny()Z")
    )
    private boolean chromatics$includeChromaticInClientEffects(Pokemon pokemon) {
        return ChromaticAuraEffects.usesShinyEffects(pokemon);
    }

    @Redirect(
        method = "onSyncedDataUpdated$lambda$5",
        at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/pokemon/Pokemon;getShiny()Z")
    )
    private static boolean chromatics$includeChromaticInScheduledSendOutSound(Pokemon pokemon) {
        return ChromaticAuraEffects.usesShinyEffects(pokemon);
    }

    @ModifyArg(
        method = "playWildShinySounds",
        at = @At(
            value = "INVOKE",
            target = "Lcom/cobblemon/mod/common/client/entity/PokemonClientDelegate;playShinyEffect(Ljava/lang/String;)V",
            ordinal = 1
        ),
        index = 0
    )
    private String chromatics$routeWildAura(String effectId) {
        PokemonEntity entity = ((PokemonClientDelegate) (Object) this).getCurrentEntity();
        return ChromaticAuraEffects.routeWildEffect(effectId, entity);
    }

    @ModifyArg(
        method = "playWildShinySounds",
        at = @At(
            value = "INVOKE",
            target = "Lcom/cobblemon/mod/common/client/entity/PokemonClientDelegate;playShinyEffect(Ljava/lang/String;)V",
            ordinal = 0
        ),
        index = 0
    )
    private String chromatics$routeWildAmbientAura(String effectId) {
        PokemonEntity entity = ((PokemonClientDelegate) (Object) this).getCurrentEntity();
        return ChromaticAuraEffects.routeWildAmbientEffect(effectId, entity);
    }

    @ModifyArg(
        method = "spawnShinyParticle",
        at = @At(
            value = "INVOKE",
            target = "Lcom/cobblemon/mod/common/client/entity/PokemonClientDelegate;playShinyEffect(Ljava/lang/String;)V"
        ),
        index = 0
    )
    private String chromatics$routeLookedAtAmbientAura(String effectId) {
        PokemonEntity entity = ((PokemonClientDelegate) (Object) this).getCurrentEntity();
        return ChromaticAuraEffects.routeAmbientEffect(effectId, entity);
    }
}
