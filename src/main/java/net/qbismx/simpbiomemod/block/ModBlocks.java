package net.qbismx.simpbiomemod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.qbismx.simpbiomemod.SimpBiomeMod;
import net.qbismx.simpbiomemod.block.custom.ModPortalBlock;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SimpBiomeMod.MODID);

    // ポータルブロックの実装。setIdがないとエラーになります。
    public static final DeferredBlock<Block> MOD_PORTAL = BLOCKS.register("mod_portal",
            () -> new ModPortalBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(-1.0F)
                    .sound(SoundType.GLASS)
                    .lightLevel((x) -> {return 11;})
                    .noLootTable()
            ));
    // 例(ネザーポータル) NETHER_PORTAL = register("nether_portal", NetherPortalBlock::new, Properties.of().noCollission().randomTicks().strength(-1.0F).sound(SoundType.GLASS)
    // .lightLevel((p_50884_) -> { return 11; })
    // .pushReaction(PushReaction.BLOCK));

    // ブロックを追加するとき、そのアイテムも追加するのが常。だから、アイテムにもポータルブロックを追加する。

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
