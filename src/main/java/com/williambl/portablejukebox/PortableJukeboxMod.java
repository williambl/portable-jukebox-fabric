package com.williambl.portablejukebox;

import com.williambl.portablejukebox.jukebox.PortableJukeboxItem;
import com.williambl.portablejukebox.jukebox.PortableJukeboxLoadRecipe;
import com.williambl.portablejukebox.noteblock.PortableNoteBlockItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class PortableJukeboxMod implements ModInitializer {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final Item PORTABLE_JUKEBOX = Registry.register(Registry.ITEM, new Identifier("portablejukebox:portable_jukebox"),
            new PortableJukeboxItem(new Item.Settings().maxCount(1).group(ItemGroup.MISC))
    );

    public static final Item PORTABLE_NOTE_BLOCK = Registry.register(Registry.ITEM, new Identifier("portablejukebox:portable_note_block"),
            new PortableNoteBlockItem(new Item.Settings().maxCount(1).group(ItemGroup.MISC))
    );

    public static final RecipeSerializer<?> PORTABLE_JUKEBOX_LOAD = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier("portablejukebox:crafting_special_portable_jukebox_load"),
            new SpecialRecipeSerializer<>(PortableJukeboxLoadRecipe::new)
    );

    private static final List<Identifier> JUKEBOX_LOOT_TABLES = Arrays.asList(
            LootTables.DESERT_PYRAMID_CHEST,
            LootTables.ABANDONED_MINESHAFT_CHEST,
            LootTables.JUNGLE_TEMPLE_CHEST,
            LootTables.SIMPLE_DUNGEON_CHEST,
            LootTables.NETHER_BRIDGE_CHEST,
            LootTables.IGLOO_CHEST_CHEST
    );

    private static final LootTableEntry.Builder<?> entry = LootTableEntry.builder(new Identifier("portablejukebox:inject/loot_chests")).weight(10);

    @Override
    public void onInitialize() {
        LootTableLoadingCallback.EVENT.register(((resourceManager, lootManager, identifier, fabricLootSupplierBuilder, lootTableSetter) -> {
            if (JUKEBOX_LOOT_TABLES.contains(identifier)) {
                fabricLootSupplierBuilder.withPool(LootPool.builder().with(entry).build());
            }
        }));
    }
}
