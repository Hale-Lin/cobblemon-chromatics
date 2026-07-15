package dev.cobblemonproject.chromatics.mixin;

import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.cobblemonproject.chromatics.common.chromatic.ChromaticLifecycle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Pokemon.class, remap = false)
abstract class PokemonInitializeMixin {
    @Inject(method = "initialize", at = @At("TAIL"), remap = false)
    private void chromatics$initializeProfile(CallbackInfoReturnable<Pokemon> callback) { ChromaticLifecycle.ensureProfile((Pokemon)(Object)this); }
}
