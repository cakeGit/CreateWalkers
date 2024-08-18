package com.cak.walkers.content.contraption;

import com.cak.walkers.content.components.controller.VehicleControllerBlock;
import com.cak.walkers.content.registry.WalkersContraptionTypes;
import com.cak.walkers.foundation.vehicle.implementation.ContraptionVehicleImplementation;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class VehicleContraption extends Contraption {
    
    ContraptionVehicleImplementation vehicle;
    
    @Nullable Set<Vec3> collectedLegPositions;
    
    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        Direction forwardsDirection = world.getBlockState(pos).getValue(VehicleControllerBlock.FACING);
        
        if (!searchMovedStructure(world, pos, null))
            return false;
        
        //Check captured blocks form a valid vehicle, potentially throwing an AssemblyException
        ContraptionVehicleImplementation.validate(collectedLegPositions, forwardsDirection.getAxis());
        
        //Now create the vehicle
        vehicle = new ContraptionVehicleImplementation(collectedLegPositions, forwardsDirection.getAxis());
        
        return true;
    }
    
    @Override
    public boolean canBeStabilized(Direction facing, BlockPos localPos) {
        return false;
    }
    
    @Override
    public ContraptionType getType() {
        return WalkersContraptionTypes.WALKER;
    }
    
}
