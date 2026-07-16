package dev.cobblemonproject.chromatics.mixin.client;

import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.summary.Summary;
import com.cobblemon.mod.common.client.gui.summary.widgets.ModelWidget;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.cobblemonproject.chromatics.client.ChromaticStatusIcon;
import dev.cobblemonproject.chromatics.client.render.ChromaticGuiRenderContext;
import net.minecraft.client.gui.DrawContext;
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
            target = "Lcom/cobblemon/mod/common/client/gui/summary/widgets/ModelWidget;render(Lnet/minecraft/client/gui/DrawContext;IIF)V"
        )
    )
    private void chromatics$renderPreviewModel(
        ModelWidget widget,
        DrawContext context,
        int mouseX,
        int mouseY,
        float delta
    ) {
        ChromaticGuiRenderContext.render(
            previewPokemon,
            () -> widget.render(context, mouseX, mouseY, delta)
        );
    }

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
