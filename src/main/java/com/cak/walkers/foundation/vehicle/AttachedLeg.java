package com.cak.walkers.foundation.vehicle;

import com.cak.walkers.foundation.vehicle.balance.Quadrant;
import com.cak.walkers.foundation.vehicle.implementation.AbstractVehicleImplementation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@Deprecated
@ApiStatus.ScheduledForRemoval
/**Holds basic and final information about a leg
 * In future will have the animator and the physics type so that the vehicle can create appropriately*/
public class AttachedLeg {
    
    protected Vec3 offset;
    
    Quadrant quadrant;
    LegPhysics physics;
    
    public AttachedLeg(AbstractVehicleImplementation vehicle, Vec3 position, Function<AttachedLeg, LegPhysics> legPhysicsBuilder) {
        offset = position;
        quadrant = Quadrant.ofHorizontalVector(position, vehicle.getForwardsAxis());
        physics = legPhysicsBuilder.apply(this);
    }
    
    public Quadrant getQuadrant() {
        return quadrant;
    }
    
    public LegPhysics getPhysics() {
        return physics;
    }
    
    public Vec3 getOffset() {
        return offset;
    }
    
}
