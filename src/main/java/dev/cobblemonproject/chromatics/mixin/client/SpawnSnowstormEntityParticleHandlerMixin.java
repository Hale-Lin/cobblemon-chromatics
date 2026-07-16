package dev.cobblemonproject.chromatics.mixin.client;

import com.cobblemon.mod.common.client.net.effect.SpawnSnowstormEntityParticleHandler;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.net.messages.client.effect.SpawnSnowstormEntityParticlePacket;
import dev.cobblemonproject.chromatics.client.effect.ChromaticAuraEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = SpawnSnowstormEntityParticleHandler.class, remap = false)
abstract class SpawnSnowstormEntityParticleHandlerMixin {
    @Redirect(
        method = "handle",
        at = @At(
            value = "INVOKE",
            desc = @Desc(
                owner = SpawnSnowstormEntityParticlePacket.class,
                value = "getEffectId",
                ret = Identifier.class
            ),
            remap = false
        ),
        remap = false
    )
    private Identifier chromatics$routeAura(
        SpawnSnowstormEntityParticlePacket packet,
        SpawnSnowstormEntityParticlePacket handledPacket,
        MinecraftClient client
    ) {
        if (client.world == null) {
            return packet.getEffectId();
        }
        Entity entity = client.world.getEntityById(packet.getSourceEntityId());
        return entity instanceof PokemonEntity pokemonEntity
            ? ChromaticAuraEffects.routePacketEffect(packet.getEffectId(), pokemonEntity)
            : packet.getEffectId();
    }
}
