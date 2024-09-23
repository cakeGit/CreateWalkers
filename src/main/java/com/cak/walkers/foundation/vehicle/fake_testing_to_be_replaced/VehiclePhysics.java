package com.cak.walkers.foundation.vehicle.fake_testing_to_be_replaced;

import net.minecraft.world.phys.Vec3;

import java.util.List;

public class VehiclePhysics {
    
    Vec3 position;
    List<VehicleLeg> legs;
    
    public VehiclePhysics(Vec3 position, List<VehicleLeg> legs) {
        this.position = position;
        this.legs = legs;
    }
    
    public void tick() {
        for (VehicleLeg leg : legs) {
            leg.worldPosition = position.add(leg.offset);
        }
    }
    
    public Vec3 getPosition() {
        return position;
    }
    
    public List<VehicleLeg> getLegs() {
        return legs;
    }
    
    public static class VehicleLeg {
        
        Vec3 worldPosition;
        Vec3 offset;
        
        public VehicleLeg(Vec3 offset) {
            this.worldPosition = Vec3.ZERO;
            this.offset = offset;
        }
        
        public Vec3 getWorldPosition(float partialTicks) {
            return worldPosition;
        }
        
        public Vec3 getOffset() {
            return offset;
        }
        
        public void setOffset(Vec3 offset) {
            this.offset = offset;
        }
        
    }
    
}
