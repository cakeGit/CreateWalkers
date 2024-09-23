package com.cak.walkers.content.contraption;

import com.cak.walkers.content.components.anchor.LegAnchorBlock;
import com.cak.walkers.content.components.controller.VehicleControllerBlock;
import com.cak.walkers.content.registry.WalkersBlocks;
import com.cak.walkers.content.registry.WalkersContraptionTypes;
import com.cak.walkers.foundation.vehicle.implementation.ContraptionVehicleImplementation;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class VehicleContraption extends Contraption {
    
    Map<BlockPos, Vec3> collectedLegPositions = new HashMap<>();
    Direction forwardsDirection;
    BlockPos assemblyAnchor;
    
    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        assemblyAnchor = pos;
        Direction forwardsDirection = world.getBlockState(pos).getValue(VehicleControllerBlock.FACING);
        
        if (!searchMovedStructure(world, pos, null))
            return false;
        
        //TODO: Check captured blocks form a valid vehicle, potentially throwing an AssemblyException
        
        this.forwardsDirection = forwardsDirection;
        startMoving(world);
        return true;
    }
    
    @Override
    protected void addBlock(BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair) {
        super.addBlock(pos, pair);
        if (pair.getLeft().state().is(WalkersBlocks.LEG_ANCHOR.get())) {
            collectedLegPositions.put(pos.subtract(anchor), LegAnchorBlock.getLegTargetPos(pos.subtract(anchor), pair.getLeft().state()));
        }
    }
    
    @Override
    public boolean canBeStabilized(Direction facing, BlockPos localPos) {
        return false;
    }
    
    @Override
    public ContraptionType getType() {
        return WalkersContraptionTypes.WALKER;
    }
    
    @Override
    public void onEntityCreated(AbstractContraptionEntity entity) {
        super.onEntityCreated(entity);
    }
    
    @Override
    protected boolean isAnchoringBlockAt(BlockPos pos) {
        return false;
    }
    
}
