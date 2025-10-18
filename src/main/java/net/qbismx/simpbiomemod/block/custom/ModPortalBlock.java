package net.qbismx.simpbiomemod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.qbismx.simpbiomemod.block.ModBlocks;
import net.qbismx.simpbiomemod.worldgen.dimension.ModDimensions;
import org.jetbrains.annotations.Nullable;

public class ModPortalBlock extends Block implements Portal {

    public ModPortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.canUsePortal(false)) {
            entity.setAsInsidePortal(this, pos);
        }
    }

    @Nullable
    @Override
    public DimensionTransition getPortalDestination(ServerLevel serverLevel, Entity entity, BlockPos blockPos) {
        // Modのディメンションなら、オーバーワールドのkeyを代入、そうでなければModのディメンションのkeyをresourcekeyに代入する
        ResourceKey<Level> resourcekey = serverLevel.dimension() == ModDimensions.INTRO_LEVEL_KEY ? Level.OVERWORLD : ModDimensions.INTRO_LEVEL_KEY;
        //上手くいかないときには、ResourceKey<Level> resourcekey = serverLevel.dimension() == Level.NETHER ? Level.OVERWORLD : Level.NETHER; に置き換えて検証しましょう
        ServerLevel serverlevel = serverLevel.getServer().getLevel(resourcekey);
        if (serverlevel == null) {
            return null;
        } else {
            //上手くいかないときには、if (resourcekey == Level.NETHER){ に置き換えて検証しましょう
            if (resourcekey == ModDimensions.INTRO_LEVEL_KEY) {
                return setDestination(serverlevel, entity, blockPos, true);
            } else {
                return setDestination(serverlevel, entity, blockPos, false);
            }
        }
    }

    private DimensionTransition setDestination(ServerLevel destinationWorld, Entity entity, BlockPos blockPos, boolean insideDimension) {
        BlockPos destinationPos = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()); // 目的地にあるポータルの出口の位置(入ったところの座標と同じにします)

        entity.setPos(blockPos.getX(), destinationPos.getY(), blockPos.getZ()- 1.5); // 出口の位置を設定する

        // 出口ポータルブロックの設置
        if (insideDimension) {
            boolean doSetExitBlock = true;
            // 近くに出口用ブロックがないかの確認
            for (BlockPos checkPos : BlockPos.betweenClosed(destinationPos.below(10).west(10),
                    destinationPos.above(10).east(10))) {
                if (destinationWorld.getBlockState(checkPos).getBlock() instanceof ModPortalBlock) {
                    doSetExitBlock = false;
                    break;
                }
            }

            if (doSetExitBlock) {
                // ポータル周りに空間を作っておきます。
                destinationWorld.removeBlock(destinationPos.above(), true);
                // 北
                destinationWorld.removeBlock(destinationPos.north(), true);
                destinationWorld.removeBlock(destinationPos.north().above(), true);
                destinationWorld.removeBlock(destinationPos.north(2), true);
                destinationWorld.removeBlock(destinationPos.north(2).above(), true);

                //足場
                destinationWorld.setBlock(destinationPos.below(), Blocks.SEA_LANTERN.defaultBlockState(), 3);
                destinationWorld.setBlock(destinationPos.below().north(), Blocks.SEA_LANTERN.defaultBlockState(), 3);
                destinationWorld.setBlock(destinationPos.below().north(2), Blocks.SEA_LANTERN.defaultBlockState(), 3);

                // 出口用のポータルブロックを設置
                destinationWorld.setBlock(destinationPos, ModBlocks.MOD_PORTAL.get().defaultBlockState(), 3);
            }
        }
        Vec3 vec3 = new Vec3(blockPos.getX(), destinationPos.getY(), blockPos.getZ() - 1.5);
        return new DimensionTransition(destinationWorld, vec3, Vec3.ZERO, entity.getYRot(), entity.getXRot(), DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET));
    }
}
