package com.williambl.portablejukebox.jukebox;

import com.williambl.portablejukebox.PortableJukeboxMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PortableJukeboxItem extends Item {

    private List<ItemStack> jukeboxes = null;

    public PortableJukeboxItem(Item.Settings settings) {
        super(settings);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand handIn) {

        ItemStack stack = player.getStackInHand(handIn);

        CompoundTag tag = stack.getOrCreateSubTag("Disc");
        Item discItem = ItemStack.fromTag(tag).getItem();

        if (!(discItem instanceof MusicDiscItem))
            return TypedActionResult.pass(player.getStackInHand(handIn));
        MusicDiscItem disc = (MusicDiscItem) discItem;

        if (player.isSneaking()) {
            stack.removeSubTag("Disc");
            stack.getOrCreateTag().put("Disc", ItemStack.EMPTY.toTag(new CompoundTag()));
            player.giveItemStack(new ItemStack(disc));

            if (!world.isClient) {
                ((ServerChunkManager)player.getEntityWorld().getChunkManager()).sendToNearbyPlayers(player, ServerPlayNetworking.createS2CPacket(new Identifier("portablejukebox:stop"), PacketByteBufs.create().writeUuid(player.getUuid())));
            }

            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        }

        if (!world.isClient) {
            ((ServerChunkManager)player.getEntityWorld().getChunkManager()).sendToNearbyPlayers(player, ServerPlayNetworking.createS2CPacket(new Identifier("portablejukebox:play"), PacketByteBufs.create().writeUuid(player.getUuid()).writeString(Registry.ITEM.getId(disc).toString())));
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        CompoundTag tag = stack.getOrCreateSubTag("Disc");

        ItemStack discStack = ItemStack.fromTag(tag);

        if (discStack.getItem() != Items.AIR)
            tooltip.add(new LiteralText("Disc: ").append(((MusicDiscItem) discStack.getItem()).getDescription()));
        else
            tooltip.add(new LiteralText("Empty"));
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> items) {
        if (this.isIn(group)) {
            items.add(new ItemStack(this));
            items.addAll(getJukeboxes());
        }
    }

    public boolean onDroppedByPlayer(ItemStack stack, PlayerEntity player) {
        if (!player.world.isClient) {
            ((ServerChunkManager)player.getEntityWorld().getChunkManager()).sendToNearbyPlayers(player, ServerPlayNetworking.createS2CPacket(new Identifier("portablejukebox:stop"), PacketByteBufs.create().writeUuid(player.getUuid())));
        }
        return true;
    }

    private List<ItemStack> getJukeboxes() {
        if (jukeboxes == null || jukeboxes.isEmpty()) {
            jukeboxes = new ArrayList<>();
            Registry.ITEM.stream()
                    .filter(it -> it instanceof MusicDiscItem)
                    .map(item -> item.getDefaultStack().toTag(new CompoundTag()))
                    .forEach(tag -> {
                        ItemStack stack = new ItemStack(PortableJukeboxMod.PORTABLE_JUKEBOX);
                        stack.getOrCreateTag().put("Disc", tag);
                        jukeboxes.add(stack);
                    });
        }
        return jukeboxes;
    }
}
