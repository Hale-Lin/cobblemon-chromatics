package dev.cobblemonproject.chromatics.common.chromatic;

import net.minecraft.nbt.NbtCompound;

public final class ChromaticProfileCodec {
    private ChromaticProfileCodec() {}
    public static NbtCompound encode(ChromaticProfile p) {
        NbtCompound t=new NbtCompound(); t.putInt("schemaVersion",p.schemaVersion()); t.putBoolean("chromatic",p.chromatic()); t.putLong("seed",p.seed()); t.put("baseHsv",hsv(p.baseHsv())); t.put("layerHsv",hsv(p.layerHsv())); t.putString("variant",p.variant()); return t;
    }
    public static ChromaticProfile decode(NbtCompound t) { return new ChromaticProfile(t.getInt("schemaVersion"),t.getBoolean("chromatic"),t.getLong("seed"),readHsv(t.getCompound("baseHsv")),readHsv(t.getCompound("layerHsv")),t.getString("variant")); }
    private static NbtCompound hsv(HsvTransform h){NbtCompound t=new NbtCompound();t.putFloat("h",h.hueShiftDegrees());t.putFloat("s",h.saturationScale());t.putFloat("v",h.valueScale());return t;}
    private static HsvTransform readHsv(NbtCompound t){return new HsvTransform(t.getFloat("h"),t.getFloat("s"),t.getFloat("v"));}
}
