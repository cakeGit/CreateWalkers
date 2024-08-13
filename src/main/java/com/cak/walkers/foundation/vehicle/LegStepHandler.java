package com.cak.walkers.foundation.vehicle;

import com.cak.walkers.foundation.vehicle.balance.Quadrant;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.outliner.Outline;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LegStepHandler {
    
    public static final int TOTAL_STEP_TICKS = 20;
    
    Quadrant quadrant;
    Leg leg;
    
    boolean isDown = true;
    float currentStepTicks = 0;
    float maximumDevianceRadius = 1f;
    float maximumDevianceRadiusSqr = maximumDevianceRadius * maximumDevianceRadius;
    
    //Todo, add y rot handling, once the vehicle itself has y rot handling
    /**Kept as the last down position of this leg*/
    Vec3 currentPosition;
    float previousYRot;
    /**Kept to where the leg is moving to*/
    Vec3 stepTargetPosition;
    float stepTargetYRot;
    /**Constantly updated to the "intended" position of the leg*/
    Vec3 targetPosition;
    float targetYRot;
    
    public LegStepHandler(Leg leg, Vec3 currentPos) {
        currentPosition = currentPos;
        targetPosition = currentPos;
        stepTargetPosition = currentPos;
        
        this.leg = leg;
    }
    
    public void tick() {
        targetPosition = leg.worldPosition;
        if (!isDown)
            currentStepTicks++;
        if (currentStepTicks == TOTAL_STEP_TICKS) {
            currentStepTicks = 0;
            currentPosition = stepTargetPosition;
            isDown = true;
        }
    }
    
    public Vec3 constrainMovement(Vec3 movementDelta) {
        if (!isDown) return new Vec3(0, 0, 0);
        
        Vec3 resultingPosition = targetPosition.add(movementDelta);
        Vec3 deltaLegPosition = resultingPosition.subtract(currentPosition);
        
        double deltaMagnitudeSqr = deltaLegPosition.horizontalDistanceSqr();
        if (deltaMagnitudeSqr > maximumDevianceRadiusSqr)
            deltaLegPosition = deltaLegPosition.normalize().scale(maximumDevianceRadiusSqr);
        
        return deltaLegPosition.add(currentPosition).subtract(targetPosition).with(Direction.Axis.Y, movementDelta.y);
    }
    
    private Vec3 getVisualPosition(float partialTicks) {
        if (isDown) return currentPosition;
        float realTime = Math.min(currentStepTicks + partialTicks, TOTAL_STEP_TICKS);
        if (realTime > 20) return stepTargetPosition;
        Vec3 midPoint = currentPosition.lerp(stepTargetPosition, 0.5)
            .add(0, 1, 0);
        if (realTime == 10) return midPoint;
        if (realTime < 10) return currentPosition.lerp(midPoint, realTime/10);
        return midPoint.lerp(stepTargetPosition, (realTime-10)/10);
    }
    
    protected static Outline.OutlineParams showBox(String slot, AABB box) {
        return CreateClient.OUTLINER.showAABB(slot, box).lineWidth(2 / 16f);
    }
    
    public double getTargetDeviance() {
        return targetPosition.subtract(currentPosition).horizontalDistanceSqr();
    }
    
    public void doStep() {
        isDown = false;
        currentStepTicks = 0;
        stepTargetPosition = targetPosition;
    }
    
    public Quadrant getQuadrant() {
        return quadrant;
    }
    
    public void renderDebug() {
        showLine(this+"prev", currentPosition, currentPosition);
        showLine(this+"current",
            getVisualPosition(AnimationTickHolder.getPartialTicks()).subtract(0, 1.5, 0),
            getVisualPosition(AnimationTickHolder.getPartialTicks()).add(0, 0.2, 0)
        )
            .lineWidth(6/16f)
            .colored(new Color(0x2255aa));
    }
    
    protected static Outline.OutlineParams showLine(String slot, Vec3 from, Vec3 to) {
        return CreateClient.OUTLINER.showLine(slot,
            from, to
        ).lineWidth(2 / 16f);
    }
    
    protected static Outline.OutlineParams showPoint(String slot, Vec3 point) {
        return CreateClient.OUTLINER.showLine(slot,
            point, point
        ).lineWidth(6 / 16f);
    }
    
}
