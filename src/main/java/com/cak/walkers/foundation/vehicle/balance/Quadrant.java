package com.cak.walkers.foundation.vehicle.balance;

import com.jozufozu.flywheel.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public enum Quadrant {
    FRONT_LEFT,
    FRONT_RIGHT,
    BACK_LEFT,
    BACK_RIGHT;
    
    public static Quadrant ofHorizontalVector(Vec3 offset, Direction.Axis frontAxis) {
        Direction.Axis secondaryAxis = frontAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        if (offset.get(frontAxis) >= 0)
            return offset.get(secondaryAxis) > 0 ? FRONT_RIGHT : FRONT_LEFT;
        else
            return offset.get(secondaryAxis) > 0 ? BACK_RIGHT : BACK_LEFT;
    }
    
    public List<Quadrant> getNeighbors() {
        return switch (this) {
            case BACK_LEFT -> List.of(BACK_RIGHT, FRONT_LEFT);
            case BACK_RIGHT -> List.of(BACK_LEFT, FRONT_RIGHT);
            case FRONT_LEFT -> List.of(FRONT_RIGHT, BACK_LEFT);
            case FRONT_RIGHT -> List.of(FRONT_LEFT, BACK_RIGHT);
        };
    }
}
