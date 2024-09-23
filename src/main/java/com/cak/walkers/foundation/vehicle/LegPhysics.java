package com.cak.walkers.foundation.vehicle;

import com.cak.walkers.foundation.vehicle.implementation.AbstractVehicleImplementation;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.outliner.Outline;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@Deprecated
@ApiStatus.ScheduledForRemoval
public class LegPhysics {
    
    public static final int TOTAL_STEP_TICKS = 5;
    
    @Deprecated //REMOVE THIS AFTER THE SILLY IS OVER
    public static int GETTOTAL_STEP_TICKSFREEOFFINALTYRANTY() {
        return 25;
    }
    
    protected AttachedLeg attachment;
    protected AbstractVehicleImplementation vehicle;
    
    /**Used for network tracking, becomes true when a networked property is changed, and then has to be set to false once networked*/
    protected boolean changed = false;
    protected boolean isDown = true;
    protected boolean isSupported = true;
    protected int currentStepTicks = 0;
    protected float maximumDevianceRadius = 0.25f;
    protected float maximumDevianceRadiusSqr = maximumDevianceRadius * maximumDevianceRadius;
    
    //Todo, add y rot handling (just visual), once the vehicle itself has y rot handling
    /**
     * Kept as the last down position of this leg
     */
    Vec3 currentPosition;
    float currentYRot;
    /**
     * Kept to where the leg is moving to
     */
    protected Vec3 stepTargetPosition;
    protected float stepTargetYRot;
    /**
     * Constantly updated to the "intended" position of the leg
     */
    protected Vec3 targetPosition;
    
    Level level;
    
    public LegPhysics(AttachedLeg attachment, AbstractVehicleImplementation vehicle) {
        this.attachment = attachment;
        this.vehicle = vehicle;
        
        targetPosition = getTargetPos();
        currentPosition = targetPosition;
        stepTargetPosition = targetPosition;
        currentYRot = 0f;
        
        this.attachment = attachment;
    }
    
    private Vec3 getTargetPos() {
        Vec3 targetPos = vehicle.legToWorldPosition(attachment.getOffset()).subtract(0, 1.5, 0);
        
        if (level != null)
            targetPos = targetPos
                .with(Direction.Axis.Y, getUnsupportedYHeightOfStepAtPosition(targetPos));
        
        return targetPos;
    }
    
    public void tick() {
        maximumDevianceRadius = 3f;
        targetPosition = getTargetPos();
        Double y = getYHeightOfStepAtPosition(currentPosition);
        
        isSupported = y != null ;
        if (!isSupported) {
            currentPosition = getTargetPos();
            currentYRot = vehicle.getYRot();
            changed = true;
            if (!isDown) {
                currentStepTicks = 0;
                isDown = true;
            }
            return;
        }
        
        if (Math.abs(currentPosition.y - y) > 1 / 16f && isDown) {
            currentPosition = currentPosition.with(Direction.Axis.Y, y);
            changed = true;
        }
        
        if (!isDown)
            currentStepTicks++;
        if (currentStepTicks >= GETTOTAL_STEP_TICKSFREEOFFINALTYRANTY()) {
            currentStepTicks = 0;
            currentPosition = stepTargetPosition;
            currentYRot = vehicle.getYRot();
            isDown = true;
            changed = true;
        }
    }
    
    private @Nullable Double getYHeightOfStepAtPosition(Vec3 position) {
        //TODO: change to level clipping, but for now just check the center since its a ray
        
//        position = new Vec3(
//            Math.floor(position.x) + 0.5,
//            Math.floor(position.y) + 0.5,
//            Math.floor(position.z) + 0.5
//        );
        
        
        Vec3 legTop = position.add(0, 1.5, 0);
        
        BlockHitResult hitResult = level.clip(new ClipContext(
            legTop,
            position.subtract(0, 3.5, 0),
            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
            null
        ));
        
        //Validate
        if (
            hitResult.getType() == HitResult.Type.MISS ||
                hitResult.getLocation().distanceToSqr(legTop) < 0.05
        ) return null;
        
        return hitResult.getLocation().y;
    }
    
    private Double getUnsupportedYHeightOfStepAtPosition(Vec3 position) {
        Vec3 legTop = position.add(0, 1.5, 0);
        
        BlockHitResult hitResult = level.clip(new ClipContext(
            legTop,
            position.subtract(0, 3, 0),
            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
            null
        ));
        
        return hitResult.getLocation().y;
    }
    
    public Vec3 constrainMovement(Vec3 movementDelta) {
        if (!isDown) return new Vec3(0, 0, 0);
        
        Vec3 resultingPosition = targetPosition.add(movementDelta);
        Vec3 deltaLegPosition = resultingPosition.subtract(currentPosition);
        
        double deltaMagnitudeSqr = deltaLegPosition.horizontalDistanceSqr();
        
        if (deltaMagnitudeSqr > maximumDevianceRadiusSqr)
            deltaLegPosition = deltaLegPosition.normalize().scale(maximumDevianceRadiusSqr);
        
        return deltaLegPosition.add(currentPosition).subtract(targetPosition);
    }
    
    public float getAnimationStepProgress(float partialTicks) {
        return Mth.clamp((currentStepTicks + partialTicks) / (GETTOTAL_STEP_TICKSFREEOFFINALTYRANTY()), 0, 1);
    }
    
    /**
     * TODO： Move to separate animator class?
     */
    public Vec3 getVisualPosition(float partialTicks) {
        Vec3 visualCurrentPosition = currentPosition.add(0, 1.5, 0);
        Vec3 visualCurrentStepPosition = stepTargetPosition.add(0, 1.5, 0);
        
        if (isDown) return visualCurrentPosition;
        
        float animationProgress = getAnimationStepProgress(partialTicks);
        
        double yRaiseHeight = Math.max(visualCurrentStepPosition.y + 1, visualCurrentPosition.y + 0.5);
        
        Vec3 animatedPosition = visualCurrentPosition
            .lerp(visualCurrentStepPosition, Mth.clamp((animationProgress * 1.4) - 0.2, 0, 1))
            .with(Direction.Axis.Y,
                animationProgress < 0.2 ? Mth.lerp(animationProgress / 0.2, visualCurrentPosition.y, yRaiseHeight) :
                    animationProgress > 0.8 ? Mth.lerp((animationProgress - 0.8) / 0.2, yRaiseHeight, visualCurrentStepPosition.y) :
                        yRaiseHeight
            );
        return clampYToBounds(animatedPosition, vehicle.currentY(partialTicks));
    }
    
    /**
     * Requires the level to calculate the target height
     *
     * @return whether the step was taken or not
     */
    public void tryDoStep(Vec3 movementMomentum) {
        Vec3 moveToPos = getStepTargetPos(movementMomentum);
        
        if (moveToPos == null || yOutOfBounds(moveToPos.y, vehicle.currentYAtOffset(attachment.offset.get(vehicle.getForwardsAxis())))) return;
        
        isDown = false;
        changed = true;
        currentStepTicks = 0;
        stepTargetPosition = moveToPos;
    }
    
    public @Nullable Vec3 getStepTargetPos(Vec3 movementMomentum) {
        movementMomentum = movementMomentum.normalize().scale(maximumDevianceRadius * 0.5f);
        Vec3 position = targetPosition.add(movementMomentum);
        Double y = getYHeightOfStepAtPosition(position);
        
//        //Try move a little further TODO reimplement later, look for a usable spot to step
//        Vec3 otherPosition = targetPosition.add(movementMomentum.normalize().add(movementMomentum));
//        Double otherY = getYHeightOfStepAtPosition(otherPosition);
//
//        if (otherY != null && (y == null || otherY > y)) {
//            position = otherPosition;
//            y = otherY;
//        }
//
        return y == null ? null : position.with(Direction.Axis.Y, y);
    }
    
    protected static Outline.OutlineParams showBox(String slot, AABB box) {
        return CreateClient.OUTLINER.showAABB(slot, box).lineWidth(2 / 16f);
    }
    
    public double getTargetDeviance() {
        return targetPosition.subtract(currentPosition).horizontalDistanceSqr();
    }
    
    public boolean isBearingWeight() {
        return isDown && isSupported;
    }
    
    public void renderDebug() {
        showLine(this + "prev", currentPosition, currentPosition);
        showLine(this + "current",
            getVisualPosition(AnimationTickHolder.getPartialTicks()).subtract(0, 1.5, 0),
            getVisualPosition(AnimationTickHolder.getPartialTicks()).add(0, 0.2, 0)
        )
            .lineWidth(6 / 16f)
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
    
    public boolean shouldActivateStep(Vec3 movementDirection) {
        Vec3 targetDelta = targetPosition.add(movementDirection).subtract(currentPosition);
        //If the leg wants to move in the same direction
        return targetDelta.dot(movementDirection) > 0;
    }
    
    public void tickStepTarget(Vec3 movementDirection) {
        if (getAnimationStepProgress(0) > 0.8) return;
        
        Vec3 updatedPos = getStepTargetPos(movementDirection);
        if (updatedPos != null) {
            changed = true;
            stepTargetPosition = updatedPos;
        }
    }
    
    public boolean yOutOfBounds(double thisY, double vehicleY) {
        return thisY > vehicleY + 1 || thisY < vehicleY - 3;
    }
    
    public Vec3 clampYToBounds(Vec3 pos, double vehicleY) {
        return pos.with(Direction.Axis.Y, Mth.clamp(pos.y, vehicleY-3, vehicleY+1));
    }
    
    public void tickLegYConstraints(double vehicleY) {
       currentPosition = clampYToBounds(currentPosition, vehicleY);
    }
    
    public boolean isChanged() {
        return changed;
    }
    
    public void notifyUpdated() {
        changed = false;
    }
    
    public Vec3 getCurrentPosition() {
        return currentPosition;
    }
    
    public Vec3 getCurrentStepTargetPosition() {
        return stepTargetPosition;
    }
    
    public boolean isDown() {
        return isDown;
    }
    
    public Vec3 getStepTargetPosition() {
        return stepTargetPosition;
    }
    
    public int getCurrentStepTicks() {
        return currentStepTicks;
    }
    
}
