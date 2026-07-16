package dev.cobblemonproject.chromatics.mixin.client;

import com.cobblemon.mod.common.client.gui.pc.StorageSlot;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.cobblemonproject.chromatics.client.ChromaticStatusIcon;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StorageSlot.class, remap = false)
abstract class StorageSlotStatusIconMixin {
    @Shadow
    public abstract Pokemon getPokemon();

    @Inject(method = "renderSlot", at = @At("TAIL"))
    private void chromatics$drawStatusIcon(
        DrawContext context,
        int x,
        int y,
        float partialTicks,
        CallbackInfo callback
    ) {
        Pokemon pokemon = getPokemon();
        if (pokemon == null) {
            return;
        }
        ChromaticStatusIcon status = ChromaticStatusIcon.resolve(pokemon);
        if (status.visible()) {
            context.drawTexture(status.texture(), x + 1, y + 16, 8, 8, 0F, 0F, 16, 16, 16, 16);
        }
    }
}
