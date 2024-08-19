package com.cak.walkers.content.components.controller;

import com.cak.walkers.content.contraption.VehicleContraptionEntity;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class VehicleControllerMovingInteractionBehavior extends MovingInteractionBehaviour {
    
    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        if (contraptionEntity instanceof VehicleContraptionEntity vce) {
            if (!contraptionEntity.level().isClientSide)
                vce.disassembleNextTick();
            return true;
        }
        return false;
    }
    
}
