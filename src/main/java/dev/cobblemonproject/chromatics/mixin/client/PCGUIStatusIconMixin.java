package dev.cobblemonproject.chromatics.mixin.client;

import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.summary.Summary;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.cobblemonproject.chromatics.client.ChromaticStatusIcon;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PCGUI.class)
abstract class PCGUIStatusIconMixin {
    @Shadow(remap = false)
    private Pokemon previewPokemon;

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
            value = "INVOKE",
            target = "Lcom/cobblemon/mod/common/client/gui/summary/Summary$Companion;getIconShinyResource()Lnet/minecraft/util/Identifier;"
        )
    )
    private Identifier chromatics$selectStatusIcon(Summary.Companion companion) {
        return ChromaticStatusIcon.resolve(previewPokemon).texture();
    }
}
