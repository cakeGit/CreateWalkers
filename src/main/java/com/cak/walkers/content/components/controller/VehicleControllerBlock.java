package com.cak.walkers.content.components.controller;

import com.cak.walkers.content.registry.WalkersBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VehicleControllerBlock extends HorizontalDirectionalBlock implements IBE<VehicleControllerBlockEntity> {
    
    public VehicleControllerBlock(Properties p) {
        super(p);
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        super.createBlockStateDefinition(p_49915_);
        p_49915_.add(FACING);
    }
    
    //TODO: make sure the contraption direction is assembling correctly, which it basically is tbf
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_49820_) {
        return super.getStateForPlacement(p_49820_)
            .setValue(FACING, p_49820_.getHorizontalDirection().getOpposite());
    }
    
    @SuppressWarnings("Deprecated") //Safe to override
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        return onBlockEntityUse(level, pos, be -> be.blockInteracted(player));
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
