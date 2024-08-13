package com.cak.walkers.foundation.vehicle;

import com.cak.walkers.foundation.vehicle.balance.Quadrant;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class LegStepManager {
    
    AbstractVehicleImplementation vehicle;
    HashMap<Leg, LegStepHandler> legHandlers = new HashMap<>();
    
    /**Time for the stepping animation (takes 20 but its given a buffer to look nicer)*/
    int legStepTime = LegStepHandler.TOTAL_STEP_TICKS;
    float legStepRate;
    float legStepTick = 0f;
    
    HashMap<Quadrant, Set<LegStepHandler>> handlersByQuadrant = new HashMap<>();
    
    public LegStepManager(AbstractVehicleImplementation vehicle) {
        this.vehicle = vehicle;
        
        for (Leg leg : vehicle.legs) {
            LegStepHandler handler = new LegStepHandler(leg, leg.worldPosition);
            legHandlers.put(leg, handler);
            handler.quadrant = Quadrant.ofHorizontalVector(leg.offset, vehicle.axis);
            Set<LegStepHandler> quadrantHandlers = handlersByQuadrant.getOrDefault(handler.quadrant, new HashSet<>());
            quadrantHandlers.add(handler);
            handlersByQuadrant.put(handler.quadrant, quadrantHandlers);
        }
        legStepRate = vehicle.legs.size() / ((float) legStepTime * 2);
    }
    
    public void renderDebug() {
        for (LegStepHandler handler : legHandlers.values()) {
            handler.renderDebug();
        }
    }
    
    public void tickTargetPositions() {
        for (LegStepHandler handler : legHandlers.values()) {
            handler.tick();
        }
    }
    
    /*Requires at least one front and one back balance to be able to move*/
    public Vec3 constrainMovement(Vec3 movementDelta) {
        if (vehicle.balanceInfo.isUnstable())
            return new Vec3(0, 1, 0);//Some bs value to get me to fix the inevitable issue
        
        List<Set<BalanceInfo.Balance>> balanceSets = List.of(
            vehicle.balanceInfo.getAllBackBalances(),
            vehicle.balanceInfo.getAllFrontBalances()
        );
        List<Vec3> maximumDeltas = new ArrayList<>();
        
        for (Set<BalanceInfo.Balance> entry : balanceSets) {
            Vec3 closestDelta = new Vec3(0, 0, 0);
            Set<Leg> legs = allLegsOfBalances(entry);
            for (Leg leg : legs) {
                LegStepHandler stepHandler = legHandlers.get(leg);
                //TODO, extend this to calculate rotational movements
                Vec3 legMaxDelta = stepHandler.constrainMovement(movementDelta);
                if (legMaxDelta.distanceToSqr(Vec3.ZERO) > closestDelta.distanceToSqr(Vec3.ZERO))
                    closestDelta = legMaxDelta;
            }
            maximumDeltas.add(closestDelta);
        }
        
        if (maximumDeltas.get(0).distanceToSqr(Vec3.ZERO) > maximumDeltas.get(0).distanceToSqr(Vec3.ZERO))
            return maximumDeltas.get(1);
        return maximumDeltas.get(0);
    }
    
    public void tickStepping(Vec3 movementMomentum) {
        movementMomentum = movementMomentum.scale(2);
        legStepTick += legStepRate;
        
        if (legStepTick >= 1) {
            List<LegStepHandler> stepCandidates = new ArrayList<>(legHandlers.values().stream()
                .filter(leg -> leg.isDown)
                .sorted(Comparator.comparingDouble(leg -> -leg.getTargetDeviance()))
                .toList());
            
            for (int i = 0; i < legStepTick; i++) {
                if (i >= stepCandidates.size()) {
                    break;
                }
                boolean canStep = canStep(stepCandidates.get(i));
                if (canStep) {
                    stepCandidates.get(i).doStep(vehicle.level, movementMomentum);
                } else {
                    legStepTick++;
                }
            }
        }
        
        legStepTick = legStepTick % 1f;
    }
    
    private boolean canStep(LegStepHandler legStepHandler) {
        if (handlersByQuadrant.get(legStepHandler.quadrant).stream().anyMatch(leg -> leg.isDown && leg != legStepHandler)) {
            return true;
        }
        for (Quadrant neighbor : legStepHandler.quadrant.getNeighbors()) {
            if (handlersByQuadrant.get(neighbor).isEmpty())
                throw new AssertionError();
            if (handlersByQuadrant.get(neighbor).stream().noneMatch(leg -> leg.isDown))
                return false;
        }
        return true;
    }
    
    private Set<Leg> allLegsOfBalances(Set<BalanceInfo.Balance> balances) {
        Set<Leg> legs = new HashSet<>();
        for (BalanceInfo.Balance balance : balances) {
            legs.addAll(balance.legs());
        }
        return legs;
    }
    
    
}
