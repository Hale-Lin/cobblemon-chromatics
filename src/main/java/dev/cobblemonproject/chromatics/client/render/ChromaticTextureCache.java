package dev.cobblemonproject.chromatics.client.render;

import dev.cobblemonproject.chromatics.CobblemonChromatics;
import dev.cobblemonproject.chromatics.common.chromatic.ChromaticProfile;
import dev.cobblemonproject.chromatics.common.chromatic.HsvTransform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ChromaticTextureCache {
    private static final Map<String, Identifier> CACHE = new ConcurrentHashMap<>();
    private ChromaticTextureCache() {}
    public static Identifier resolve(Identifier source, ChromaticProfile profile, String pass, HsvTransform transform) {
        if (!profile.chromatic()) return source;
        return CACHE.computeIfAbsent(source + "|" + pass + "|" + profile.stableHash(), ignored -> create(source, transform));
    }
    public static Identifier resolve(Identifier source, ChromaticProfile profile, String pass) { return resolve(source, profile, pass, profile.baseHsv()); }
    private static Identifier create(Identifier source, HsvTransform transform) {
        MinecraftClient client = MinecraftClient.getInstance();
        try {
            Resource resource = client.getResourceManager().getResourceOrThrow(source);
            try (InputStream input = resource.getInputStream()) {
                NativeImage image = NativeImage.read(input);
                for (int x = 0; x < image.getWidth(); x++) for (int y = 0; y < image.getHeight(); y++) image.setColor(x, y, transform(image.getColor(x, y), transform));
                return client.getTextureManager().registerDynamicTexture(CobblemonChromatics.MOD_ID, new NativeImageBackedTexture(image));
            }
        } catch (IOException exception) { return source; }
    }
    private static int transform(int argb, HsvTransform t) {
        int a = (argb >>> 24) & 255, r = (argb >>> 16) & 255, g = (argb >>> 8) & 255, b = argb & 255;
        if (a == 0) return argb;
        float rf = r / 255F, gf = g / 255F, bf = b / 255F, max = Math.max(rf, Math.max(gf, bf)), min = Math.min(rf, Math.min(gf, bf)), d = max - min;
        if (max < 0.12F || d < 0.04F || (max > 0.9F && d < 0.12F)) return argb;
        float h = max == rf ? 60 * ((gf - bf) / d % 6) : max == gf ? 60 * ((bf - rf) / d + 2) : 60 * ((rf - gf) / d + 4);
        if (h < 0) h += 360;
        float s = Math.min(1F, d / max * t.saturationScale()), v = Math.min(1F, max * t.valueScale()); h = (h + t.hueShiftDegrees()) % 360; if (h < 0) h += 360;
        float c = v * s, x = c * (1 - Math.abs((h / 60F) % 2 - 1)), m = v - c, rr = 0, gg = 0, bb = 0;
        if (h < 60) { rr = c; gg = x; } else if (h < 120) { rr = x; gg = c; } else if (h < 180) { gg = c; bb = x; } else if (h < 240) { gg = x; bb = c; } else if (h < 300) { rr = x; bb = c; } else { rr = c; bb = x; }
        return (a << 24) | (Math.round((rr + m) * 255) << 16) | (Math.round((gg + m) * 255) << 8) | Math.round((bb + m) * 255);
    }
}
