package com.cak.walkers.foundation.vehicle.implementation;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class TestingClientVehicle extends AbstractVehicleImplementation {
    
    public TestingClientVehicle() {
        super(Set.of(
            new Vec3(-1.5, 0, 3.5),
            new Vec3(-1.5, 0, -3.5),
            new Vec3(-1.5, 0, -1.5),
            new Vec3(-1.5, 0, 1.5),
            new Vec3(1.5, 0, 1.5),
            new Vec3(1.5, 0, -1.5),
            new Vec3(1.5, 0, 3.5),
            new Vec3(1.5, 0, -3.5)
        ), Direction.Axis.Z);
    }
    
}
