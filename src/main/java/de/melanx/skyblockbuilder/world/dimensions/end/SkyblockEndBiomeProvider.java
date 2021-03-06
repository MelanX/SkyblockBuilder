package de.melanx.skyblockbuilder.world.dimensions.end;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.melanx.skyblockbuilder.config.LibXConfigHandler;
import de.melanx.skyblockbuilder.util.LazyBiomeRegistryWrapper;
import de.melanx.skyblockbuilder.util.WorldUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Objects;

public class SkyblockEndBiomeProvider extends BiomeProvider {

    public static final Codec<SkyblockEndBiomeProvider> CODEC = RecordCodecBuilder.create(
            (builder) -> builder.group(
                    Codec.LONG.fieldOf("seed").stable().forGetter((provider) -> provider.seed),
                    RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter(provider -> provider.lookupRegistry)
            ).apply(builder, builder.stable((seed, lookupRegistry) -> new SkyblockEndBiomeProvider(
                    new EndBiomeProvider(new LazyBiomeRegistryWrapper(lookupRegistry), seed)
            ))));

    private final EndBiomeProvider parent;
    private final long seed;
    public final Registry<Biome> lookupRegistry;

    public SkyblockEndBiomeProvider(EndBiomeProvider parent) {
        super(parent.getBiomes());
        this.parent = parent;
        this.seed = parent.seed;
        this.lookupRegistry = parent.lookupRegistry;
    }

    @Nonnull
    @Override
    protected Codec<? extends BiomeProvider> getBiomeProviderCodec() {
        return CODEC;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public BiomeProvider getBiomeProvider(long seed) {
        return new SkyblockEndBiomeProvider((EndBiomeProvider) this.parent.getBiomeProvider(seed));
    }

    @Nonnull
    @Override
    public Biome getNoiseBiome(int x, int y, int z) {
        if (LibXConfigHandler.World.SingleBiome.enabled && LibXConfigHandler.World.SingleBiome.singleBiomeDimension.getDimension().equals(WorldUtil.SingleBiomeDimension.THE_END.getDimension())) {
            Biome biome = this.lookupRegistry.getOrDefault(WorldUtil.SINGLE_BIOME);
            if (biome == null) {
                biome = this.lookupRegistry.getOrDefault(Biomes.THE_END.getLocation());
            }
            return Objects.requireNonNull(biome);
        } else {
            return this.parent.getNoiseBiome(x, y, z);
        }
    }
}
