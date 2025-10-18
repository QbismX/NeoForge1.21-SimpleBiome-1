package net.qbismx.simpbiomemod.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.qbismx.simpbiomemod.SimpBiomeMod;
import net.qbismx.simpbiomemod.block.ModBlocks;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SimpBiomeMod.MODID);

    public static final DeferredItem<BlockItem> MOD_PORTAL_ITEM = ITEMS.registerSimpleBlockItem(
            "mod_portal",
            ModBlocks.MOD_PORTAL,
            new Item.Properties()
    );


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
