package dev.cobblemonproject.chromatics.common.network;

import dev.cobblemonproject.chromatics.CobblemonChromatics;
import dev.cobblemonproject.chromatics.common.chromatic.ChromaticProfile;
import dev.cobblemonproject.chromatics.common.chromatic.HsvTransform;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import java.util.UUID;

public record ChromaticProfileSyncPayload(UUID pokemonId, ChromaticProfile profile) implements CustomPayload {
    public static final Id<ChromaticProfileSyncPayload> ID = new Id<>(Identifier.of(CobblemonChromatics.MOD_ID, "chromatic_profile"));
    public static final PacketCodec<RegistryByteBuf, ChromaticProfileSyncPayload> CODEC = PacketCodec.of(ChromaticProfileSyncPayload::write, ChromaticProfileSyncPayload::read);
    private static ChromaticProfileSyncPayload read(RegistryByteBuf b) { UUID id=b.readUuid(); int schema=b.readVarInt(); boolean active=b.readBoolean(); long seed=b.readLong(); HsvTransform base=new HsvTransform(b.readFloat(),b.readFloat(),b.readFloat()); HsvTransform layer=new HsvTransform(b.readFloat(),b.readFloat(),b.readFloat()); return new ChromaticProfileSyncPayload(id,new ChromaticProfile(schema,active,seed,base,layer,b.readString(128))); }
    private void write(RegistryByteBuf b) { b.writeUuid(pokemonId); b.writeVarInt(profile.schemaVersion()); b.writeBoolean(profile.chromatic()); b.writeLong(profile.seed()); b.writeFloat(profile.baseHsv().hueShiftDegrees()); b.writeFloat(profile.baseHsv().saturationScale()); b.writeFloat(profile.baseHsv().valueScale()); b.writeFloat(profile.layerHsv().hueShiftDegrees()); b.writeFloat(profile.layerHsv().saturationScale()); b.writeFloat(profile.layerHsv().valueScale()); b.writeString(profile.variant(),128); }
    @Override public Id<? extends CustomPayload> getId(){return ID;}
}
