package com.cak.walkers.foundation.vehicle;

import com.cak.walkers.foundation.vehicle.balance.Quadrant;
import com.cak.walkers.foundation.vehicle.implementation.AbstractVehicleImplementation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.stream.Collectors;

public class LegPhysicsManager {
    
    AbstractVehicleImplementation vehicle;
    
    Set<LegPhysics> allLegPhysics = new HashSet<>();
    HashMap<AttachedLeg, LegPhysics> legPhysicsByAttachment = new HashMap<>();
    HashMap<Quadrant, Set<LegPhysics>> legPhysicsByQuadrant = new HashMap<>();
    
    Level level;
    
    public LegPhysicsManager(AbstractVehicleImplementation vehicle) {
        this.vehicle = vehicle;
        
        for (AttachedLeg leg : vehicle.getAttachedLegs()) {
            legPhysicsByAttachment.put(leg, leg.physics);
            allLegPhysics.add(leg.physics);
            
            Set<LegPhysics> quadrantHandlers = legPhysicsByQuadrant.getOrDefault(leg.quadrant, new HashSet<>());
            quadrantHandlers.add(leg.physics);
            legPhysicsByQuadrant.put(leg.quadrant, quadrantHandlers);
        }
    }
    
    public void tick() {
        for (LegPhysics legPhysics : allLegPhysics) {
            legPhysics.tick();
        }
    }
    
    public void renderDebug() {
        for (LegPhysics handler : allLegPhysics) {
            handler.renderDebug();
        }
    }
    
    public boolean isSupported() {
        HashMap<Quadrant, Boolean> supportedQuadrants = new HashMap<>();
        
        for (Map.Entry<Quadrant, Set<LegPhysics>> entry : legPhysicsByQuadrant.entrySet()) {
            Quadrant quadrant = entry.getKey();
            Set<LegPhysics> legPhysics = entry.getValue();
            supportedQuadrants.put(quadrant,
                legPhysics.stream().anyMatch(leg -> leg.isSupported)
            );
        }
        
        return supportedQuadrants.get(Quadrant.FRONT_LEFT) && supportedQuadrants.get(Quadrant.BACK_RIGHT)
            || supportedQuadrants.get(Quadrant.BACK_LEFT) && supportedQuadrants.get(Quadrant.FRONT_RIGHT);
    }
    
    public boolean isPartlySupported() {
        for (LegPhysics legPhysics : allLegPhysics) {
            if (legPhysics.isSupported) return true;
        }
        return false;
    }
    
    public float constrainRotation(float rotationDelta) {
        if (!isSupported()) {
            return 0;
        }
        float newRotation = vehicle.getYRot() + rotationDelta;
        
        for (LegPhysics legPhysics : allLegPhysics) {
            if (!legPhysics.isBearingWeight()) continue;
            
            Vec3 movementDelta = getRotationDelta(legPhysics, newRotation);
        
            Vec3 constrainedDelta = legPhysics.constrainMovement(movementDelta);
            
            if (movementDelta.dot(constrainedDelta) < 0)
                return 0;
            
            if (movementDelta.horizontalDistanceSqr() > constrainedDelta.horizontalDistanceSqr())
                rotationDelta = rotationDelta * (float) (
                    constrainedDelta.distanceToSqr(0, 0, 0) /
                    movementDelta.distanceToSqr(0, 0, 0)
                );
        }
        return rotationDelta;
    }
    
    public Vec3 constrainMovement(Vec3 movementDelta) {
        if (!isSupported()) {
            return new Vec3(0, 0, 0);
        }
        
        for (LegPhysics legPhysics : allLegPhysics) {
            if (!legPhysics.isBearingWeight()) continue;
            
            Vec3 constrainedDelta = legPhysics.constrainMovement(movementDelta);
            
            if (movementDelta.dot(constrainedDelta) < 0)
                return new Vec3(0, 0, 0);
            
            if (movementDelta.horizontalDistanceSqr() > constrainedDelta.horizontalDistanceSqr())
                movementDelta = constrainedDelta;
        }
        return movementDelta;
    }
    
    public void tickStepping(Vec3 baseMovementDirection, float yRot) {
        List<LegPhysics> stepCandidates = new ArrayList<>(allLegPhysics.stream()
            .filter(handler -> !handler.isDown || handler.shouldActivateStep(baseMovementDirection.add(getRotationDelta(handler, yRot))))
            .toList());
        
        for (LegPhysics handler : stepCandidates) {
            Vec3 specificMovementDirection = baseMovementDirection.add(getRotationDelta(handler, yRot));
            
            if (!handler.isDown)
                handler.tickStepTarget(specificMovementDirection);
            else if (canStep(handler))
                handler.tryDoStep(specificMovementDirection.scale(1.5));
        }
    }
    
    private Vec3 getRotationDelta(LegPhysics handler, float yRot) {
        return handler.attachment.offset.yRot(yRot).subtract(handler.attachment.offset.yRot(handler.currentYRot));
    }
    
    private boolean canStep(LegPhysics legPhysics) {
        for (Quadrant neighbor : legPhysics.attachment.quadrant.getNeighbors()) {
            if (legPhysicsByQuadrant.get(neighbor).isEmpty())
                throw new AssertionError();
            if (legPhysicsByQuadrant.get(neighbor).stream().noneMatch(leg -> leg.isDown))
                return false;
        }
        return true;
    }
    
    private Set<AttachedLeg> allLegsOfBalances(Set<BalanceInfo.Balance> balances) {
        Set<AttachedLeg> legs = new HashSet<>();
        for (BalanceInfo.Balance balance : balances) {
            legs.addAll(balance.legs());
        }
        return legs;
    }
    
    public void tickLegYConstraints() {
        for (LegPhysics legPhysics : allLegPhysics) {
            legPhysics.tickLegYConstraints(vehicle.currentYAtOffset(legPhysics.attachment.offset.get(vehicle.getForwardsAxis())));
        }
    }
    
    protected Vec3 tickVehicleLegSetPositions(Set<LegPhysics> legs) {
        Vec3 totalPosition = Vec3.ZERO;
        int totalSupportingLegs = 0;
        Double minY = null;
        
        for (LegPhysics legPhysics : legs) {
            Vec3 legPos = legPhysics.currentPosition;
            
            if (!legPhysics.isDown) {
                legPos = legPos.lerp(legPhysics.stepTargetPosition, legPhysics.getAnimationStepProgress(0f));
            }
            
            double legMinY = legPos.y - 1;
            minY = minY == null ? legMinY : Math.max(legMinY, minY);
            
            legPos = legPos.yRot(-vehicle.getYRot());
            
            totalPosition = totalPosition.add(legPos);
            totalSupportingLegs++;
        }
        
        if (totalSupportingLegs == 0) return null;
        Vec3 averagePos = totalPosition.scale(1f / totalSupportingLegs);
        
        return averagePos
            .with(Direction.Axis.Y, Math.max(minY, averagePos.y));
    }
    
    public Vec3 tickVehicleFrontLegsPositions() {
        return tickVehicleLegSetPositions(
            allLegsOfBalances(vehicle.getBalanceInfo().getAllFrontBalances())
                .stream()
                .map(leg -> legPhysicsByAttachment.get(leg))
                .collect(Collectors.toSet())
        );
    }
    
    public Vec3 tickVehicleBackLegsPositions() {
        return tickVehicleLegSetPositions(
            allLegsOfBalances(vehicle.getBalanceInfo().getAllBackBalances())
                .stream()
                .map(leg -> legPhysicsByAttachment.get(leg))
                .collect(Collectors.toSet())
        );
    }
    
    public void setLevel(Level level) {
        for (LegPhysics physics : allLegPhysics)
            physics.level = level;
    }
    
    public void fall(Double fallVelocity) {
        for (LegPhysics legPhysics : allLegPhysics) {
            legPhysics.currentPosition = legPhysics.currentPosition.subtract(0, fallVelocity, 0);
        }
    }
    
}
