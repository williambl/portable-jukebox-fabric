package com.williambl.portablejukebox.noteblock;

import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PortableNoteBlockItem extends Item {

    public PortableNoteBlockItem(Item.Settings properties) {
        super(properties);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();

        world.playSound(null, player.getBlockPos(), getInstrumentFromBlock(world, pos), SoundCategory.RECORDS, 3.0F, getPitchFromPosition(pos));
        world.addParticle(ParticleTypes.NOTE, player.getX(), player.getY() + player.getEyeY(), player.getZ(), 1.0F, 0F, 0F);
        return ActionResult.SUCCESS;
    }

    private float getPitchFromPosition(BlockPos pos) {
        return (float) pos.getY() / 128;
    }

    private SoundEvent getInstrumentFromBlock(World worldIn, BlockPos pos) {
        return Instrument.fromBlockState(worldIn.getBlockState(pos)).getSound();
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        worldIn.playSound(null, playerIn.getBlockPos(), getInstrument(0), SoundCategory.RECORDS, 3.0F, getPitchFromPosition(playerIn.getBlockPos()));
        worldIn.addParticle(ParticleTypes.NOTE, playerIn.getX(), playerIn.getEyeY(), playerIn.getZ(), 1.0F, 0F, 0F);
        return super.use(worldIn, playerIn, handIn);
    }

    private SoundEvent getInstrument(int eventId) {
        if (eventId < 0 || eventId >= Instrument.values().length)
            eventId = 0;
        return Instrument.values()[eventId].getSound();
    }
}
