package com.cak.walkers.content.components.controller;

import com.cak.walkers.content.registry.WalkersBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class VehicleControllerBlock extends HorizontalDirectionalBlock implements IBE<VehicleControllerBlockEntity> {
    
    public VehicleControllerBlock(Properties p) {
        super(p);
    }
    
    @SuppressWarnings("Deprecated") //Safe to override
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        return onBlockEntityUse(level, pos, VehicleControllerBlockEntity::blockInteracted);
    }
    
    @Override
    public Class<VehicleControllerBlockEntity> getBlockEntityClass() {
        return VehicleControllerBlockEntity.class;
    }
    
    @Override
    public BlockEntityType<? extends VehicleControllerBlockEntity> getBlockEntityType() {
        return WalkersBlockEntityTypes.VEHICLE_BLOCK_CONTROLLER_BLOCK_ENTITY.get();
    }
    
}
