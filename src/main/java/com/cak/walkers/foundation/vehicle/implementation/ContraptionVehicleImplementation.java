package com.cak.walkers.foundation.vehicle.implementation;


import com.cak.walkers.foundation.vehicle.AttachedLeg;
import com.cak.walkers.foundation.vehicle.LegPhysics;
import com.cak.walkers.foundation.vehicle.balance.Quadrant;
import com.simibubi.create.content.contraptions.AssemblyException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.stream.Collectors;

public class ContraptionVehicleImplementation extends AbstractVehicleImplementation {
    
    /**The relative position of the center of the vehicle implementation to the center of the contraption*/
    Vec3 anchorOffset;
    HashMap<BlockPos, AttachedLeg> legsByStructureAnchor = new HashMap<>();
    
    public ContraptionVehicleImplementation(Map<BlockPos, Vec3> legPositions, Direction forwards) {
        super(centerLegPositions(legPositions.values()), forwards);
        //legPositions does not correct to be centered
        anchorOffset = getCenter(legPositions.values());
        
        for (Map.Entry<BlockPos, Vec3> entry : legPositions.entrySet()) {
            AttachedLeg attachedLeg = legs.stream().filter(leg -> leg.getOffset().equals(entry.getValue().subtract(anchorOffset)))
                .findFirst().orElseThrow();
            legsByStructureAnchor.put(entry.getKey(), attachedLeg);
        }
    }
    
    /**@return whether to send the leg data to client*/
    public void tickNetworkChanges() {
        for (LegPhysics legPhysics : legPhysicsManager.getAllLegPhysics()) {
            if (legPhysics.isChanged()) {
                legPhysics.notifyUpdated();
            }
        }
    }
    
    protected static Vec3 getCenter(Collection<Vec3> legPositions) {
        Vec3 centerPos = Vec3.ZERO;
        int legCount = legPositions.size();
        
        for (Vec3 anchor : legPositions) {
            centerPos = centerPos.add(anchor.scale(1f / legCount));
        }
        return centerPos;
    }
    
    protected static Collection<Vec3> centerLegPositions(Collection<Vec3> legPositions) {
        Vec3 anchorOffset = getCenter(legPositions);

        //Offset so that the center is 0, 0
        return legPositions.stream()
            .map(position -> position.subtract(anchorOffset))
            .collect(Collectors.toSet());
    }
    
    public static void validate(Collection<Vec3> legPositions, Direction.Axis forwardsAxis) throws AssemblyException {
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
    
    public Map<BlockPos, AttachedLeg> getLegsByStructureAnchor() {
        return legsByStructureAnchor;
    }
    
}
