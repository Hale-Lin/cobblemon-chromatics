package dev.cobblemonproject.chromatics.mixin.client;

import com.cobblemon.mod.common.client.entity.PokemonClientDelegate;
import com.cobblemon.mod.common.client.render.ModelLayer;
import com.cobblemon.mod.common.client.render.ModelTextureSupplier;
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState;
import com.cobblemon.mod.common.client.render.models.blockbench.repository.VaryingModelRepository;
import dev.cobblemonproject.chromatics.client.ChromaticClientProfiles;
import dev.cobblemonproject.chromatics.client.render.ChromaticTextureCache;
import dev.cobblemonproject.chromatics.common.chromatic.ChromaticProfile;
import dev.cobblemonproject.chromatics.common.chromatic.ChromaticRenderPolicy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.List;

@Mixin(value=VaryingModelRepository.class, remap=false)
abstract class VaryingModelRepositoryMixin {
    @Inject(method="getTexture",at=@At("RETURN"),cancellable=true,remap=false)
    private void chromatics$replaceMainTexture(Identifier species, PosableState state, CallbackInfoReturnable<Identifier> callback) { profile(state).filter(ChromaticRenderPolicy::transformsBase).ifPresent(profile->callback.setReturnValue(ChromaticTextureCache.resolve(callback.getReturnValue(),profile,"base",profile.baseHsv()))); }
    @Inject(method="getLayers",at=@At("RETURN"),cancellable=true,remap=false)
    private void chromatics$replaceLayers(Identifier species, PosableState state, CallbackInfoReturnable<Iterable<ModelLayer>> callback) { profile(state).filter(ChromaticRenderPolicy::transformsLayer).ifPresent(profile->{List<ModelLayer> layers=new ArrayList<>();for(ModelLayer layer:callback.getReturnValue())layers.add(copy(layer,state,profile));callback.setReturnValue(layers);}); }
    private static java.util.Optional<ChromaticProfile> profile(PosableState state){if(state instanceof PokemonClientDelegate d&&d.getCurrentEntity()!=null)return ChromaticClientProfiles.get(d.getCurrentEntity().getPokemon().getUuid());return java.util.Optional.empty();}
    private static ModelLayer copy(ModelLayer source,PosableState state,ChromaticProfile profile){ModelLayerAccessor original=(ModelLayerAccessor)(Object)source;ModelLayer copy=new ModelLayer();ModelLayerAccessor target=(ModelLayerAccessor)(Object)copy;target.chromatics$setName(original.chromatics$getName());target.chromatics$setEnabled(original.chromatics$isEnabled());target.chromatics$setTint(original.chromatics$getTint());target.chromatics$setEmissive(original.chromatics$isEmissive());target.chromatics$setTranslucent(original.chromatics$isTranslucent());target.chromatics$setTranslucentCull(original.chromatics$isTranslucentCull());ModelTextureSupplier texture=original.chromatics$getTexture();target.chromatics$setTexture(texture==null?null:s->ChromaticTextureCache.resolve(texture.invoke(s),profile,"layer:"+original.chromatics$getName(),profile.layerHsv()));return copy;}
}
