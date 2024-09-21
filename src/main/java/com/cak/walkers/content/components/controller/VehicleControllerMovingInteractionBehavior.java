package com.cak.walkers.content.components.controller;

import com.cak.walkers.content.contraption.VehicleContraptionEntity;
import com.google.common.base.Objects;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.UUID;

public class VehicleControllerMovingInteractionBehavior extends MovingInteractionBehaviour {
    
    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        ItemStack currentItem = player.getItemInHand(activeHand);
        
        if (contraptionEntity instanceof VehicleContraptionEntity vce) {
            
            if (currentItem.is(AllItems.WRENCH.get()) && !contraptionEntity.level().isClientSide)
                vce.disassembleNextTick();
            else {
                UUID currentlyControlling = contraptionEntity.getControllingPlayer().orElse(null);
                if (currentlyControlling != null) {
                    contraptionEntity.stopControlling(localPos);
                    if (Objects.equal(currentlyControlling, player.getUUID())) {
                        return true;
                    }
                }
                
                if (!contraptionEntity.startControlling(localPos, player)) {
                    return false;
                } else {
                    contraptionEntity.setControllingPlayer(player.getUUID());
                    if (player.level().isClientSide) {
                        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                            ControlsHandler.startControlling(contraptionEntity, localPos)
                        );
                    }
                }
            }
            
            return true;
        }
        return false;
    }
    
}
