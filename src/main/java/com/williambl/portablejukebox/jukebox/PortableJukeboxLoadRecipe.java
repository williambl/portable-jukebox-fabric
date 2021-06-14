package com.williambl.portablejukebox.jukebox;

import com.williambl.portablejukebox.PortableJukeboxMod;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class PortableJukeboxLoadRecipe extends SpecialCraftingRecipe {
    public PortableJukeboxLoadRecipe(Identifier idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        ItemStack jukebox = ItemStack.EMPTY;
        ItemStack disc = ItemStack.EMPTY;

        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stackInSlot = inv.getStack(i);
            if (!stackInSlot.isEmpty()) {
                if (stackInSlot.getItem() == PortableJukeboxMod.PORTABLE_JUKEBOX) {
                    if (!jukebox.isEmpty()) { //There can only be one!
                        return false;
                    }
                    if (jukebox.hasTag()) {
                        if (ItemStack.fromNbt(jukebox.getSubTag("Disc")).getItem() != Items.AIR)
                            return false;
                    }
                    jukebox = stackInSlot;
                } else {
                    if (ItemTags.getTagGroup().getTagOrEmpty(new Identifier("minecraft:music_discs")).contains(stackInSlot.getItem()) || stackInSlot.getItem() instanceof MusicDiscItem) {
                        if (!disc.isEmpty()) { //There can only be one!
                            return false;
                        }
                        disc = stackInSlot;
                    }
                }
            }
        }

        return !jukebox.isEmpty() && !disc.isEmpty();
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        ItemStack jukebox = ItemStack.EMPTY;
        ItemStack disc = ItemStack.EMPTY;

        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stackInSlot = inv.getStack(i);
            if (!stackInSlot.isEmpty()) {
                Item item = stackInSlot.getItem();
                if (item instanceof PortableJukeboxItem) {
                    if (!jukebox.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    if (jukebox.hasTag()) {
                        if (jukebox.getTag().contains("Disc"))
                            return ItemStack.EMPTY;
                    }

                    jukebox = stackInSlot.copy();
                } else {
                    if (stackInSlot.getItem() instanceof MusicDiscItem) {
                        if (!disc.isEmpty()) { //There can only be one!
                            return ItemStack.EMPTY;
                        }
                        disc = stackInSlot.copy();
                    }
                }
            }
        }

        if (!jukebox.isEmpty() && !disc.isEmpty()) {
            jukebox.getOrCreateTag().put("Disc", disc.writeNbt(new NbtCompound()));
            return jukebox;
        } else {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PortableJukeboxMod.PORTABLE_JUKEBOX_LOAD;
    }
}