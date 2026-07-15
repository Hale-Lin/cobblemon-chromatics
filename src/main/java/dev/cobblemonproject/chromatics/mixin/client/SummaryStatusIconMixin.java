package dev.cobblemonproject.chromatics.mixin.client;

import com.cobblemon.mod.common.client.gui.summary.Summary;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.cobblemonproject.chromatics.client.ChromaticStatusIcon;
import net.minecraft.util.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Summary.class)
abstract class SummaryStatusIconMixin {
    @Shadow(remap = false)
    private Pokemon selectedPokemon;

    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lcom/cobblemon/mod/common/pokemon/Pokemon;getShiny()Z",
            remap = false
        )
    )
    private boolean chromatics$showStatusIcon(Pokemon pokemon) {
        return ChromaticStatusIcon.resolve(pokemon).visible();
    }

    @Redirect(
        method = "render",
        at = @At(
            value = "FIELD",
            target = "Lcom/cobblemon/mod/common/client/gui/summary/Summary;iconShinyResource:Lnet/minecraft/util/Identifier;",
            opcode = Opcodes.GETSTATIC
        )
    )
    private Identifier chromatics$selectStatusIcon() {
        return ChromaticStatusIcon.resolve(selectedPokemon).texture();
    }
}
