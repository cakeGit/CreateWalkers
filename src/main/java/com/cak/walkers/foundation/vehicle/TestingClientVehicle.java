package com.cak.walkers.foundation.vehicle;

import net.minecraft.world.phys.Vec3;

import java.util.Set;
import java.util.stream.Collectors;

public class TestingClientVehicle extends AbstractVehicleImplementation {
    
    public TestingClientVehicle() {
        legs = Set.of(
            new Vec3(-1.5, 0, 3.5),
            new Vec3(-1.5, 0, -3.5),
            new Vec3(-1.5, 0, -1.5),
            new Vec3(-1.5, 0, 1.5),
            new Vec3(1.5, 0, 1.5),
            new Vec3(1.5, 0, -1.5),
            new Vec3(1.5, 0, 3.5),
            new Vec3(1.5, 0, -3.5)
        ).stream().map(Leg::new).collect(Collectors.toSet());
    }
    
}
