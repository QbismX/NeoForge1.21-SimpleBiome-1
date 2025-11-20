package net.qbismx.simpbiomemod.worldgen.dimension;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.qbismx.simpbiomemod.SimpBiomeMod;
import net.qbismx.simpbiomemod.worldgen.biome.ModBiomes;

import java.util.List;
import java.util.OptionalLong;
import java.util.Set;

public class ModDimensions {
    public static final ResourceKey<LevelStem> INTRO_DIM_KEY = ResourceKey.create(Registries.LEVEL_STEM,
            ResourceLocation.fromNamespaceAndPath(SimpBiomeMod.MODID, "introdim"));

    public static final ResourceKey<Level> INTRO_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(SimpBiomeMod.MODID, "introdim"));

    public static final ResourceKey<DimensionType> INTRO_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath(SimpBiomeMod.MODID, "introdim_type"));

    // ディメンションの設定 DimensionTypes.classを参考にすると良いです
    public static void bootstrapType(BootstrapContext<DimensionType> context) {
        context.register(INTRO_DIM_TYPE, new DimensionType(
                OptionalLong.empty(), // fixedTimeである。OptionalLong.of(数字)とすると時間が固定される。OptionalLong.empty()で時間が流れる
                false, // hasSkylight 空由来の光が存在するかどうか。
                false, // hasCeiling trueで岩盤の天井が生成される。理論的なものであり、場合によっては天井が生成されないことがある。
                false, // ultrraWarm trueで水の蒸発、スポンジの乾燥、溶岩の粘度低下が起こる。
                true, // natural trueだと、コンパスやベッドが正しく機能し、ネザーポータルからゾンビピグリンがスポーンする。
                1.0, // coordinateScale そのディメンションから離れた際に、座標が何倍されるか。0.00001以上3000万以下の数値で指定する。
                true, // bedWorks trueでピグリンやホグリンがゾンビ化する。
                false, // respawnAnchorWorks falseでリスポーンアンカーの使用時に爆破が起きる。
                -64, // minY ブロックを設置できる最低位置。-2032以上2031以下かつ、16の倍数でなければならない（したがって事実上最大値は2016）。
                320, // height ブロックの配置可能な範囲の垂直方向の幅。16以上4064かつ16の倍数でなくてはならない。建築可能な最大高度は = min y + height - 1であり、この値が2031以上になるような設定はできない。
                320, // logicalHeight コーラスフルーツやネザーポータルによって移動できる最高高度。このデータパックの適用前に既に上限外で繋がっているネザーポータルは通常通り機能する。
                BlockTags.INFINIBURN_OVERWORLD, // infiniteburn
                BuiltinDimensionTypes.OVERWORLD_EFFECTS, // effectsLocation
                0.3F, // ambientLight 明るさの仕様。0なら完全に空とブロック由来の明るさに従い、1なら明るさを使用せずワールド全体が明るく表示される
                new DimensionType.MonsterSettings(true, true, ConstantInt.of(7), 7)
        ));
    }


    public static void bootstrapStem(BootstrapContext<LevelStem> context) {
        HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> dimTypes = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> noiseGenSettings = context.lookup(Registries.NOISE_SETTINGS);

        // 以下の①②③のいずれか１つ使いたいものだけ書いてよい
        // ① FixedBiomeSource １つのバイオームだけ生成する
        NoiseBasedChunkGenerator wrappedChunkGenerator = new NoiseBasedChunkGenerator(
                new FixedBiomeSource(biomeRegistry.getOrThrow(Biomes.RIVER)),
                noiseGenSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD)
        );


        // ② 複数のバイオームをランダムな配置で生成する(Climate.parametersの引数は前から順に、気温・湿度・大陸性・地形の平坦度(erosion)・地面からの深さ(depth)・奇異性・offsetを表す)
        NoiseBasedChunkGenerator noiseBasedChunkGenerator = new NoiseBasedChunkGenerator(
                MultiNoiseBiomeSource.createFromList(
                        new Climate.ParameterList<>(List.of(
                                Pair.of(
                                        Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), biomeRegistry.getOrThrow(ModBiomes.AUTUMN_BIOME)),
                                Pair.of(
                                        Climate.parameters(0.1F, 0.2F, 0.0F, 0.2F, 0.0F, 0.0F, 0.0F), biomeRegistry.getOrThrow(ModBiomes.NIGHTMARE_BIOME))
                        ))),
                noiseGenSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD)); // オーバーワールド風のディメンションとする



        // ③ 格子状にバイオームを生成する(size=1で2×2チャンク, size=2で4×4チャンク, size=3で8×8チャンク)
        NoiseBasedChunkGenerator checkerboardChunkGenerator = new NoiseBasedChunkGenerator(
                new CheckerboardColumnBiomeSource(HolderSet.direct(List.of(biomeRegistry.getOrThrow(ModBiomes.AUTUMN_BIOME), biomeRegistry.getOrThrow(ModBiomes.NIGHTMARE_BIOME))), 3)
                ,noiseGenSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD)); // オーバーワールド風のディメンションとする


        // ① LevelStem stem = new LevelStem(dimTypes.getOrThrow(ModDimensions.INTRO_DIM_TYPE), wrappedChunkGenerator);
        // ② LevelStem stem = new LevelStem(dimTypes.getOrThrow(ModDimensions.INTRO_DIM_TYPE), noiseBasedChunkGenerator);
         LevelStem stem = new LevelStem(dimTypes.getOrThrow(ModDimensions.INTRO_DIM_TYPE), checkerboardChunkGenerator);

        context.register(INTRO_DIM_KEY, stem);
    }
}
