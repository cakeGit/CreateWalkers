package com.cak.walkers.foundation.vehicle.implementation;

import com.cak.walkers.content.contraption.NetworkedContraptionLegData;
import com.cak.walkers.foundation.vehicle.balance.Quadrant;
import com.simibubi.create.content.contraptions.AssemblyException;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import java.util.EnumMap;
import java.util.Set;
import java.util.stream.Collectors;

public class ContraptionVehicleImplementation extends AbstractVehicleImplementation {
    
    /**The relative position of the center of the vehicle implementation to the center of the contraption*/
    Vec3 anchorOffset;
    
    public ContraptionVehicleImplementation(Set<Vec3> legPositions, Direction.Axis forwardsAxis) {
        super(centerLegPositions(legPositions), forwardsAxis);
        //legPositions does not correct to be centered
        anchorOffset = getCenter(legPositions);
    }
    
    protected static Vec3 getCenter(Set<Vec3> legPositions) {
        Vec3 centerPos = Vec3.ZERO;
        int legCount = legPositions.size();
        
        for (Vec3 anchor : legPositions) {
            centerPos = centerPos.add(anchor.scale(1f / legCount));
        }
        return centerPos;
    }
    
    protected static Set<Vec3> centerLegPositions(Set<Vec3> legPositions) {
        Vec3 anchorOffset = getCenter(legPositions);

        //Offset so that the center is 0, 0
        return legPositions.stream()
            .map(position -> position.subtract(anchorOffset))
            .collect(Collectors.toSet());
    }
    
    public static void validate(Set<Vec3> legPositions, Direction.Axis forwardsAxis) throws AssemblyException {
        legPositions = centerLegPositions(legPositions);
        
        //Todo, for now it checks that theres a leg in all quadrants, but this should maybe be extended in the future to handle more diverse types
        EnumMap<Quadrant, Boolean> providedQuadrants = new EnumMap<>(Quadrant.class);
        
        for (Vec3 anchor : legPositions) {
            providedQuadrants.put(Quadrant.ofHorizontalVector(anchor, forwardsAxis), true);
        }
        
        for (Quadrant quadrant : Quadrant.values())
            if (!providedQuadrants.containsKey(quadrant))
                throw new AssemblyException("bitch_i_need_full_quadrants");
    }
    
    public Vec3 getAnchorOffset() {
        return anchorOffset;
    }
    
    public void setAnimationDataNetworker(NetworkedContraptionLegData legAnimationData) {
    }
    
}
