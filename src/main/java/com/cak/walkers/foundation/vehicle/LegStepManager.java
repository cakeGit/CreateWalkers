package com.cak.walkers.foundation.vehicle;

import net.minecraft.world.phys.Vec3;

import java.util.HashMap;

public class LegStepManager {
    
    AbstractVehicleImplementation vehicle;
    HashMap<AbstractVehicleImplementation.Leg, LegStepHandler> legHandlers = new HashMap<>();
    
    public LegStepManager(AbstractVehicleImplementation vehicle) {
        this.vehicle = vehicle;
    }
    
    public void tickStepping() {
        for (LegStepHandler handler : legHandlers.values()) {
            handler.
        }
    }
    
    /*Requires at least one front and one back balance to be able to move*/
    public Vec3 constrainMovement(Vec3 movementDelta) {
        vehicle
    }
    
}
