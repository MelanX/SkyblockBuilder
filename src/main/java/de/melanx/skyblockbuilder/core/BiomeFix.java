package de.melanx.skyblockbuilder.core;

import com.mojang.serialization.Codec;
import de.melanx.skyblockbuilder.util.LazyBiomeRegistryWrapper;
import de.melanx.skyblockbuilder.util.RandomUtility;
import de.melanx.skyblockbuilder.world.dimensions.end.SkyblockEndBiomeSource;
import de.melanx.skyblockbuilder.world.dimensions.multinoise.SkyblockMultiNoiseBiomeSource;
import de.melanx.skyblockbuilder.world.dimensions.multinoise.SkyblockNoiseBasedChunkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class BiomeFix {

    /**
     * Patched into {@link LinearPalette#write(FriendlyByteBuf)}} redirecting the call to
     * {@link IdMap#getId(Object)} passing the {@link IdMap} reference and all arguments.
     */
    public static int getId(IdMap<Object> lookup, Object value) {
        if (value instanceof Biome biome) {
            int id = lookup.getId(biome);
            if (id >= 0) {
                return id;
            } else {
                return RandomUtility.validateBiome(biome);
            }
        }

        return lookup.getId(value);
    }

    /**
     * Patched into {@link ChunkSerializer#write(ServerLevel, ChunkAccess)} redirecting the call to
     * change the biome registry if needed.
     */
    public static Codec<PalettedContainer<Holder<Biome>>> modifiedCodec(Registry<Biome> biomeRegistry, ServerLevel level) {
        BiomeSource biomeSource = level.getChunkSource().getGenerator().getBiomeSource();
        if (biomeSource instanceof SkyblockMultiNoiseBiomeSource || biomeSource instanceof SkyblockEndBiomeSource) {
            return ChunkSerializer.makeBiomeCodec(LazyBiomeRegistryWrapper.get(biomeRegistry));
        }

        return ChunkSerializer.makeBiomeCodec(biomeRegistry);
    }

    /**
     * Patched into {@link ChunkGenerator#findNearestMapFeature(ServerLevel, StructureFeature, BlockPos, int, boolean)}
     * redirecting to get the modified biome registry if needed to actually find the structure.
     */
    public static Registry<Biome> modifiedRegistry(Registry<Biome> biomeRegistry, ServerLevel level) {
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        if (generator instanceof SkyblockNoiseBasedChunkGenerator) {
            return LazyBiomeRegistryWrapper.get(biomeRegistry);
        }

        return biomeRegistry;
    }

    /**
     * Patched at head of {@link ServerLevel#findNearestBiome(Biome, BlockPos, int, int)} to change the way how to
     * search for the biomes.
     */
//    public static BlockPos findNearestBiome(ServerLevel level, Biome biome, BlockPos pos, int radius, int increment) {
//        ChunkGenerator generator = level.getChunkSource().getGenerator();
//        if (generator instanceof SkyblockNoiseBasedChunkGenerator) {
//            return generator.getBiomeSource().findBiomeHorizontal(pos.getX(), pos.getY(), pos.getZ(), radius, increment, target -> {
//                return Objects.equals(target.getRegistryName(), biome.getRegistryName());
//            }, level.random, true, generator.climateSampler());
//        }
//
//        return null;
//    }

    // TODO javadoc
    public static boolean isValidRegistry(Registry<?> thisRegistry, Registry<?> thatRegistry) {
        return thisRegistry.key() == thatRegistry.key();
    }

    public static void replaceMissingSections(LevelHeightAccessor levelHeightAccessor, Registry<Biome> biomeRegistry, LevelChunkSection[] chunkSections) {
        if (levelHeightAccessor instanceof ServerLevel level) {
            biomeRegistry = BiomeFix.modifiedRegistry(biomeRegistry, level);
        }

        for (int i = 0; i < chunkSections.length; ++i) {
            if (chunkSections[i] == null) {
                chunkSections[i] = new LevelChunkSection(levelHeightAccessor.getSectionIndexFromSectionY(i), biomeRegistry);
            }
        }
    }
}
