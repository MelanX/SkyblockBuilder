package de.melanx.skyblockbuilder.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import de.melanx.skyblockbuilder.compat.CuriosCompat;
import de.melanx.skyblockbuilder.config.ConfigHandler;
import de.melanx.skyblockbuilder.data.SkyblockSavedData;
import de.melanx.skyblockbuilder.data.Team;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.fml.ModList;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class RandomUtility {

    public static RegistryAccess dynamicRegistries = null;

    public static Biome modifyCopyBiome(Biome biome) {
        Biome newBiome = new Biome(biome.climateSettings, biome.getBiomeCategory(), biome.getSpecialEffects(), RandomUtility.modifyBiomeGenerationSettings(biome.getGenerationSettings()), biome.getMobSettings());
        if (biome.getRegistryName() != null) {
            newBiome.setRegistryName(biome.getRegistryName());
        }

        return newBiome;
    }

    public static BiomeGenerationSettings modifyBiomeGenerationSettings(BiomeGenerationSettings settings) {
        // Remove non-whitelisted features
        List<List<Holder<PlacedFeature>>> featureList = Lists.newArrayList();

        settings.features().forEach(list -> {
            ImmutableList.Builder<Holder<PlacedFeature>> features = ImmutableList.builder();
            for (Holder<PlacedFeature> feature : list) {
                ResourceLocation location = feature.value().feature().value().feature().getRegistryName();
                if (location != null) {
                    if (ConfigHandler.Structures.generationFeatures.test(location)) {
                        features.add(feature);
                    }
                }
            }
            featureList.add(features.build());
        });

        return new BiomeGenerationSettings(
                settings.carvers.entrySet().stream()
                        .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey,
                                Map.Entry::getValue)),
                featureList.stream()
                        .map(HolderSet::direct)
                        .collect(Collectors.toList()));
    }

    // TODO
//    public static void modifyStructureSettings(StructureSettings settings) {
//        // Remove non-whitelisted structures
//        Map<StructureFeature<?>, StructureFeatureConfiguration> map = Maps.newHashMap();
//
//        for (Map.Entry<StructureFeature<?>, StructureFeatureConfiguration> structure : settings.structureConfig.entrySet()) {
//            ResourceLocation location = structure.getKey().getRegistryName();
//            if (location != null) {
//                if (ConfigHandler.Structures.generationStructures.test(location)) {
//                    map.put(structure.getKey(), structure.getValue());
//                }
//            }
//        }
//
//        settings.structureConfig = map;
//        Map<StructureFeature<?>, ImmutableMultimap.Builder<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> hashMap = Maps.newHashMap();
//        StructureFeatures.registerStructures(((configuredStructureFeature, biomeResourceKey) -> {
//            hashMap.computeIfAbsent(configuredStructureFeature.feature, structure -> ImmutableMultimap.builder()).put(configuredStructureFeature, biomeResourceKey);
//        }));
//        settings.configuredStructures = hashMap.entrySet().stream().filter(entry -> map.get(entry.getKey()) != null).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> entry.getValue().build()));
//    }

    public static int validateBiome(Biome biome) {
        if (dynamicRegistries != null) {
            Registry<Biome> lookup = dynamicRegistries.registryOrThrow(Registry.BIOME_REGISTRY);
            return lookup.getId(lookup.get(biome.getRegistryName()));
        } else {
            return -1;
        }
    }

    // TODO
//    public static StructureSettings modifiedStructureSettings(StructureSettings settings) {
//        Map<StructureFeature<?>, StructureFeatureConfiguration> structureConfig = new HashMap<>();
//
//        for (Map.Entry<StructureFeature<?>, StructureFeatureConfiguration> entry : settings.structureConfig.entrySet()) {
//            StructureFeature<?> structureFeature = entry.getKey();
//            StructureFeatureConfiguration config = entry.getValue();
//            StructureFeatureConfiguration newConfig = new StructureFeatureConfiguration(
//                    Math.max(1, (int) (config.spacing() * ConfigHandler.World.structureModifier)),
//                    Math.max(0, (int) (config.separation() * ConfigHandler.World.structureModifier)),
//                    config.salt()
//            );
//
//            structureConfig.put(structureFeature, newConfig);
//        }
//        settings.structureConfig = structureConfig;
//
//        return settings;
//    }

    public static void dropInventories(Player player) {
        if (player.isSpectator() || player.isCreative()) {
            return;
        }

        player.getInventory().dropAll();
        if (ModList.get().isLoaded("curios")) {
            CuriosCompat.dropInventory(player);
        }
    }

    public static String formattedCooldown(long ticks) {
        int realTime = (int) (ticks / 20);
        int min = realTime / 60;
        String sec = String.format("%02d", realTime % 60);

        return String.format("%s:%s", min, sec);
    }

    public static Set<GameProfile> getGameProfiles(ServerLevel level) {
        MinecraftServer server = level.getServer();

        net.minecraft.server.players.GameProfileCache profileCache = server.getProfileCache();
        Set<GameProfile> profiles = Sets.newHashSet();
        Set<UUID> handledIds = Sets.newHashSet(Util.NIL_UUID);

        // load the cache and look for all profiles
        profileCache.load().forEach(profileInfo -> {
            GameProfile profile = profileInfo.getProfile();
            profiles.add(profile);
            handledIds.add(profile.getId());
        });

        // check if all the members were in the cache and add these tags if needed
        for (Team team : SkyblockSavedData.get(level).getTeams()) {
            for (UUID id : team.getPlayers()) {
                if (handledIds.contains(id)) {
                    continue;
                }

                Optional<GameProfile> gameProfile = profileCache.get(id);
                if (gameProfile.isPresent()) {
                    profiles.add(gameProfile.get());
                } else {
                    GameProfile profile = server.getSessionService().fillProfileProperties(new GameProfile(id, null), true);

                    if (profile.getName() != null) {
                        profileCache.add(profile);
                        profiles.add(profile);
                    } else {
                        profiles.add(new GameProfile(profile.getId(), "Unknown"));
                    }
                }
            }
        }

        return profiles;
    }

    // [Vanilla copy]
    public static void fillTemplateFromWorld(StructureTemplate template, Level level, BlockPos pos, Vec3i box, boolean withEntities, Collection<Block> toIgnore) {
        if (box.getX() >= 1 && box.getY() >= 1 && box.getZ() >= 1) {
            BlockPos blockpos = pos.offset(box).offset(-1, -1, -1);
            List<StructureTemplate.StructureBlockInfo> specialBlocks = Lists.newArrayList();
            List<StructureTemplate.StructureBlockInfo> blocksWithTag = Lists.newArrayList();
            List<StructureTemplate.StructureBlockInfo> normalBlocks = Lists.newArrayList();
            BlockPos minPos = new BlockPos(Math.min(pos.getX(), blockpos.getX()), Math.min(pos.getY(), blockpos.getY()), Math.min(pos.getZ(), blockpos.getZ()));
            BlockPos maxPos = new BlockPos(Math.max(pos.getX(), blockpos.getX()), Math.max(pos.getY(), blockpos.getY()), Math.max(pos.getZ(), blockpos.getZ()));
            template.size = box;

            for (BlockPos actPos : BlockPos.betweenClosed(minPos, maxPos)) {
                BlockPos relPos = actPos.subtract(minPos);
                BlockState state = level.getBlockState(actPos);
                if (toIgnore.isEmpty() || !toIgnore.contains(state.getBlock())) {
                    BlockEntity blockEntity = level.getBlockEntity(actPos);
                    StructureTemplate.StructureBlockInfo blockInfo;
                    if (blockEntity != null) {
                        blockInfo = new StructureTemplate.StructureBlockInfo(relPos, state, blockEntity.saveWithId());
                    } else {
                        blockInfo = new StructureTemplate.StructureBlockInfo(relPos, state, null);
                    }

                    StructureTemplate.addToLists(blockInfo, specialBlocks, blocksWithTag, normalBlocks);
                }
            }

            List<StructureTemplate.StructureBlockInfo> sortedBlocks = StructureTemplate.buildInfoList(specialBlocks, blocksWithTag, normalBlocks);
            template.palettes.clear();
            template.palettes.add(new StructureTemplate.Palette(sortedBlocks));
            if (withEntities) {
                template.fillEntityList(level, minPos, maxPos.offset(1, 1, 1));
            } else {
                template.entityInfoList.clear();
            }
        }
    }

    public static String normalize(String s) {
        return s.toLowerCase(Locale.ROOT).replaceAll("\\W+", "_");
    }

    public static String getFilePath(String folderPath, String name) {
        return getFilePath(folderPath, name, "nbt");
    }

    public static String getFilePath(String folderPath, String name, String extension) {
        int index = 0;
        String filename;
        String filepath;
        do {
            filename = (name == null ? "template" : RandomUtility.normalize(name)) + ((index == 0) ? "" : "_" + index) + "." + extension;
            index++;
            filepath = folderPath + "/" + filename;
        } while (Files.exists(Paths.get(filepath)));

        return filepath;
    }
}
