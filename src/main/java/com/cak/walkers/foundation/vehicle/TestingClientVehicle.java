package com.cak.walkers.foundation.vehicle;

import net.minecraft.world.phys.Vec3;

import java.util.Set;
import java.util.stream.Collectors;

public class TestingClientVehicle extends AbstractVehicleImplementation {
    
    public TestingClientVehicle() {
        legs = Set.of(
            new Vec3(-2, 0, 2),
            new Vec3(-2, 0, -2),
            new Vec3(-1, 0, -1),
            new Vec3(-1, 0, 1),
            new Vec3(1, 0, 1),
            new Vec3(1, 0, -1),
            new Vec3(2, 0, 2),
            new Vec3(2, 0, -2)
        ).stream().map(Leg::new).collect(Collectors.toSet());
    }
    
}
