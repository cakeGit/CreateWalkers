package com.cak.walkers.content.components.controller;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class VehicleControllerBlockEntity extends SmartBlockEntity {
    
    public VehicleControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    public InteractionResult blockInteracted() {
        boolean assembleResult = tryAssembleContraption();
        
        return assembleResult ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }
    
    private boolean tryAssembleContraption() {
    
    }
    
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> list) {}
    
}
