package com.cak.walkers.content.components.controller;

import com.cak.walkers.content.contraption.VehicleContraption;
import com.cak.walkers.content.contraption.VehicleContraptionEntity;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VehicleControllerBlockEntity extends SmartBlockEntity {
    
    public VehicleControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    public InteractionResult blockInteracted(@NotNull Player player) {
        boolean assembleResult = tryAssembleContraption(player);
        
        return assembleResult ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }
    
    private boolean tryAssembleContraption(Player playerToNotify) {
        assert level != null;
        if (level.isClientSide) return true;
        
        try {
            VehicleContraption contraption = new VehicleContraption();
            
            if (!contraption.assemble(level, getBlockPos()))
                return false;
            
            Direction facing = getBlockState().getValue(VehicleControllerBlock.FACING);
            
            contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
            
            VehicleContraptionEntity vehicleContraption = VehicleContraptionEntity.create(level, contraption, facing);
            
            level.addFreshEntity(vehicleContraption);
            
            AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, worldPosition);
        } catch (AssemblyException e) {
            playerToNotify.displayClientMessage(Component.literal(e.getMessage()), true);
            return false;
        }
        return true;
    }
    
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> list) {}
    
}
