package jas7277.unstrip;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AxeEvents {
    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack equippedItem = player.getItemInHand(hand);

            if (equippedItem.is(holder -> holder.is(ItemTags.AXES))) {
                if (!player.isShiftKeyDown() || hand != InteractionHand.MAIN_HAND) {
                    return InteractionResult.PASS;
                }

                Block clickedBlock = world.getBlockState(hitResult.getBlockPos()).getBlock();

                Block replacement = WoodMappings.UNSTRIP_MAP.get(clickedBlock);

                if (replacement != null) {
                    if (!world.isClientSide()) {
                        Unstrip.LOGGER.info("Unstripping wood!");

                        BlockState newState = replacement.defaultBlockState()
                                        .setValue(
                                                net.minecraft.world.level.block.RotatedPillarBlock.AXIS,
                                                world.getBlockState(hitResult.getBlockPos())
                                                        .getValue(net.minecraft.world.level.block.RotatedPillarBlock.AXIS)
                                        );

                        world.setBlock(
                                hitResult.getBlockPos(),
                                newState,
                                3
                        );
                        equippedItem.hurtAndBreak(
                                1,
                                player,
                                player.getUsedItemHand()
                        );
                        world.playSound(null,
                                hitResult.getBlockPos(),
                                SoundEvents.AXE_STRIP.value(),
                                SoundSource.BLOCKS,
                                1.0f,
                                1.0f
                        );
                    }

                    return InteractionResult.SUCCESS;
                }
            }

            return InteractionResult.PASS;
        });
    }
}
