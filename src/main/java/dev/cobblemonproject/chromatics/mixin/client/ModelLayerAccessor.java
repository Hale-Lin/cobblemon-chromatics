package dev.cobblemonproject.chromatics.mixin.client;

import com.cobblemon.mod.common.client.render.ModelLayer;
import com.cobblemon.mod.common.client.render.ModelTextureSupplier;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value=ModelLayer.class, remap=false)
public interface ModelLayerAccessor {
    @Accessor("name") String chromatics$getName(); @Mutable @Accessor("name") void chromatics$setName(String value);
    @Accessor("enabled") boolean chromatics$isEnabled(); @Mutable @Accessor("enabled") void chromatics$setEnabled(boolean value);
    @Accessor("tint") Vector4f chromatics$getTint(); @Mutable @Accessor("tint") void chromatics$setTint(Vector4f value);
    @Accessor("texture") ModelTextureSupplier chromatics$getTexture(); @Mutable @Accessor("texture") void chromatics$setTexture(ModelTextureSupplier value);
    @Accessor("emissive") boolean chromatics$isEmissive(); @Mutable @Accessor("emissive") void chromatics$setEmissive(boolean value);
    @Accessor("translucent") boolean chromatics$isTranslucent(); @Mutable @Accessor("translucent") void chromatics$setTranslucent(boolean value);
    @Accessor("translucent_cull") boolean chromatics$isTranslucentCull(); @Mutable @Accessor("translucent_cull") void chromatics$setTranslucentCull(boolean value);
}
