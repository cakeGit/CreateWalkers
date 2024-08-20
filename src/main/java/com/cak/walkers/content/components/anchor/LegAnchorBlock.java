package com.cak.walkers.content.components.anchor;

import com.cak.walkers.content.registry.WalkersShapes;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class LegAnchorBlock extends HorizontalDirectionalBlock {
    
    public LegAnchorBlock(Properties p) {
        super(p);
    }
    
    public static Vec3 getLegTargetPos(BlockPos relative, BlockState state) {
        return Vec3.atCenterOf(relative)
            .add(Vec3.atLowerCornerOf(state.getValue(FACING).getNormal()));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        super.createBlockStateDefinition(p_49915_);
        p_49915_.add(FACING);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_49820_) {
        return super.getStateForPlacement(p_49820_)
            .setValue(FACING, p_49820_.getHorizontalDirection().getOpposite());
    }
    
    @Override
    public boolean propagatesSkylightDown(BlockState p_49928_, BlockGetter p_49929_, BlockPos p_49930_) {
        return true;
    }
    
    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return WalkersShapes.LEG_ANCHOR.get(p_60555_.getValue(FACING));
    }
    
}
