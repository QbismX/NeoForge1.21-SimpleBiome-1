package net.qbismx.simpbiomemod.worldgen.biome;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Musics;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.qbismx.simpbiomemod.SimpBiomeMod;

public class ModBiomes {
    // 秋バイオームのリソースキー
    public static final ResourceKey<Biome> AUTUMN_BIOME = ResourceKey.create(Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(SimpBiomeMod.MODID, "autumn_biome"));

    // 悪夢バイオームのリソースキー
    public static final ResourceKey<Biome> NIGHTMARE_BIOME = ResourceKey.create(Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(SimpBiomeMod.MODID, "nightmare_biome"));

    public static void bootstrap(BootstrapContext<Biome> context) {
        context.register(AUTUMN_BIOME, autumnBiome(context));
        context.register(NIGHTMARE_BIOME, nightmareBiome(context));
    }

    public static void globalOverworldGeneration(BiomeGenerationSettings.Builder builder){
        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(builder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
        BiomeDefaultFeatures.addDefaultSprings(builder);
        BiomeDefaultFeatures.addSurfaceFreezing(builder);
    }

    // 秋バイオームの設定
    private static Biome autumnBiome(BootstrapContext<Biome> context){
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        // 以下のように設定しているが、友好な動物は基本的に明るさレベル10以上でないとスポーンしないので、キツネはスポーンしない
        spawnBuilder.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.FOX, 100, 4, 4));
        // 敵モブはスポーンするはず
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.BREEZE,100, 4, 4));

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));

        // the BiomeDefaultFeaturesでは、バニラのバイオームと同じ順序に従う必要がある
        globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addForestFlowers(biomeBuilder); // 森の花
        BiomeDefaultFeatures.addPlainGrass(biomeBuilder); // 平原の草
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder); // 鉱石

        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_PLAINS);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(0.8f)
                .temperature(0.7f)
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x39c5bb) // 初音ミクのブルーグリーン
                        .waterFogColor(0x39c5bb) // 水中かな？
                        .skyColor(0x00479d) // 空の色はサファイアブルー
                        .grassColorOverride(0xE2421F) // 草ブロックは紅葉色
                        .foliageColorOverride(0xca0048) // 葉の色は紅色
                        .fogColor(0x87ceeb) // 遠くの空は空色
                        .backgroundMusic(Musics.GAME)
                        .build())
                .build();
    }

    // 悪夢バイオームの設定
    private static Biome nightmareBiome(BootstrapContext<Biome> context){
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.RAVAGER, 100, 4, 4));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ILLUSIONER, 100, 4, 4));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.BLAZE, 100, 4, 4));

        BiomeGenerationSettings.Builder biomeBuilder =
                new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));

        // the BiomeDefaultFeaturesでは、バニラのバイオームと同じ順序に従う必要がある
        globalOverworldGeneration(biomeBuilder);
        BiomeDefaultFeatures.addForestFlowers(biomeBuilder);
        BiomeDefaultFeatures.addFerns(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);

        biomeBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_PLAINS);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .downfall(0.8f)
                .temperature(0.7f)
                .generationSettings(biomeBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x1c2d3f) // 水の色は暗めの青色
                        .waterFogColor(0x000000) // 水中かな？
                        .skyColor(0x000000) // 空の色は暗めの青色
                        .grassColorOverride(0x8a2be2) // 草ブロックはブルーバイオレット
                        .foliageColorOverride(0x8a2be2) // 葉の色はブルーバイオレット
                        .fogColor(0x9e76b4) // 遠くの空の色。アメジスト
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .backgroundMusic(Musics.GAME)
                        .build())
                .build();
    }

}
