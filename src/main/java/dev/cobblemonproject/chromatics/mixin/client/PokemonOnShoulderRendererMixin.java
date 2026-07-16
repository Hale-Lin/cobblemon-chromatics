package dev.cobblemonproject.chromatics.mixin.client;

import com.cobblemon.mod.common.client.render.layer.PokemonOnShoulderRenderer;
import dev.cobblemonproject.chromatics.client.render.ChromaticShoulderRenderContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = PokemonOnShoulderRenderer.class, remap = false)
abstract class PokemonOnShoulderRendererMixin {
    private static final String SHOULDER_RENDER =
        "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;" +
            "ILnet/minecraft/entity/player/PlayerEntity;FFFFFFZ)V";

    @Inject(method = SHOULDER_RENDER, at = @At("HEAD"), remap = true)
    private void chromatics$beginShoulderRender(
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        PlayerEntity player,
        float limbAngle,
        float limbDistance,
        float tickDelta,
        float animationProgress,
        float headYaw,
        float headPitch,
        boolean left,
        CallbackInfo callback
    ) {
        NbtCompound shoulder = left ? player.getShoulderEntityLeft() : player.getShoulderEntityRight();
        ChromaticShoulderRenderContext.push(chromatics$pokemonId(shoulder));
    }

    @Inject(method = SHOULDER_RENDER, at = @At("RETURN"), remap = true)
    private void chromatics$endShoulderRender(
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        PlayerEntity player,
        float limbAngle,
        float limbDistance,
        float tickDelta,
        float animationProgress,
        float headYaw,
        float headPitch,
        boolean left,
        CallbackInfo callback
    ) {
        ChromaticShoulderRenderContext.pop();
    }

    private static UUID chromatics$pokemonId(NbtCompound shoulder) {
        if (shoulder.contains("Pokemon", NbtElement.COMPOUND_TYPE)) {
            NbtCompound pokemon = shoulder.getCompound("Pokemon");
            if (pokemon.contains("UUID")) {
                return pokemon.getUuid("UUID");
            }
        }
        return shoulder.contains("shoulder_uuid") ? shoulder.getUuid("shoulder_uuid") : null;
    }
}
