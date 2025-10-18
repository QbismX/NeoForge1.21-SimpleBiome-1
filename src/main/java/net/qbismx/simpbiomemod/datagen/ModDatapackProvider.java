package net.qbismx.simpbiomemod.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.qbismx.simpbiomemod.SimpBiomeMod;
import net.qbismx.simpbiomemod.worldgen.biome.ModBiomes;
import net.qbismx.simpbiomemod.worldgen.dimension.ModDimensions;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModDatapackProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DIMENSION_TYPE, ModDimensions::bootstrapType) // ディメンション
            .add(Registries.BIOME, ModBiomes::bootstrap) // バイオームの追加
            .add(Registries.LEVEL_STEM, ModDimensions::bootstrapStem); // ディメンション

    public ModDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(SimpBiomeMod.MODID));
    }
}
