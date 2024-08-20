package com.cak.walkers.content.contraption;

import com.cak.walkers.content.components.controller.VehicleControllerBlock;
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
    
    @Nullable ContraptionVehicleImplementation vehicle;
    
    //TODO: remove annotation
    @Nullable Map<BlockPos, Vec3> collectedLegPositions = new HashMap<>();
    
    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        Direction forwardsDirection = world.getBlockState(pos).getValue(VehicleControllerBlock.FACING);
        
        if (!searchMovedStructure(world, pos, null))
            return false;
        
        collectedLegPositions = Map.of(
            new BlockPos(-1, 0, -2),
            new Vec3(-1, 0, -2).add(0.5, 0.5, 0.5),
            
            new BlockPos(3, 0, -2),
            new Vec3(3, 0, -2).add(0.5, 0.5, 0.5),
            
            new BlockPos(-1, 0, 2),
            new Vec3(-1, 0, 2).add(0.5, 0.5, 0.5),
            
            new BlockPos(3, 0, 2),
            new Vec3(3, 0, 2).add(0.5, 0.5, 0.5)
        );
        
        //Check captured blocks form a valid vehicle, potentially throwing an AssemblyException
        ContraptionVehicleImplementation.validate(collectedLegPositions.values(), forwardsDirection.getAxis());
        
        //Now create the vehicle
        vehicle = new ContraptionVehicleImplementation(collectedLegPositions, forwardsDirection);
        
        startMoving(world);
        return true;
    }
    
    @Override
    protected void addBlock(BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair) {
        super.addBlock(pos, pair);
        //TODO look for leg blocks
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
        if (entity instanceof VehicleContraptionEntity vce) {
            vce.vehicle = vehicle;
        }
    }
    
    @Override
    protected boolean isAnchoringBlockAt(BlockPos pos) {
        return false;
    }
    
}
