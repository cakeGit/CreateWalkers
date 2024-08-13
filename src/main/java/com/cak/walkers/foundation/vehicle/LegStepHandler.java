package com.cak.walkers.foundation.vehicle;

import net.minecraft.world.phys.Vec3;

public class LegStepHandler {
    
    AbstractVehicleImplementation.Leg leg;
    
    boolean isDown = true;
    boolean outOfBounds = false;
    float stepProgress = 1f;
    
    //Todo, add y rot handling, once the vehicle itself has y rot handling
    Vec3 previousPosition;
    float previousYRot;
    Vec3 targetPosition;
    float targetYRot;
    
    public LegStepHandler(AbstractVehicleImplementation.Leg leg, Vec3 currentPos) {
        previousPosition = currentPos;
        targetPosition = currentPos;
        
        this.leg = leg;
    }
    
    public void tick() {
        targetPosition = leg.worldPosition;
    }
    
    public void tick() {
        targetPosition = leg.worldPosition;
    }
    
}
