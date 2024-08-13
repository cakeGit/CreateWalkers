package com.cak.walkers.foundation.vehicle;

import com.jozufozu.flywheel.util.Pair;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.outliner.Outline;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class AbstractVehicleImplementation {
    
    protected Level level;
    protected boolean initialised = false;
    
    /**
     * Positions relative to the anchor of the vehicle
     */
    protected Set<Leg> legs = new HashSet<>();
    
    /**"physics" are done from these positions, but they can be noisy so use other values*/
    protected Vec3 physicalPosition = new Vec3(0, 0, 0);
    protected float physicalGradient = 0f;
    
    /**The position next tick (held separate from physics to avoid noise)*/
    protected Vec3 targetPosition = new Vec3(0, 0, 0);
    /**The gradient next tick (held separate from physics to avoid noise)*/
    protected float targetGradient = 0f;
    
    /**
     * Chases target position every tick
     */
    protected Vec3 position = new Vec3(0, 0, 0);
    /**
     * Chases target gradient every tick
     */
    protected float gradient = 0f;
    
    protected boolean balanced = true;
    protected Direction.Axis axis = Direction.Axis.X;
    
    /**
     * Handles movement limits, as well as the visual positions of the legs
     */
    protected LegStepManager legStepManager;
    /**
     * Handles balancing, i.e. tracking legs on the front and back
     */
    protected BalanceInfo balanceInfo;
    
    public void renderDebug() {
        showPoint("balanced", legToWorldVisualPosition(new Vec3(0, 0, 0), AnimationTickHolder.getPartialTicks()))
            .colored(balanced ? Color.GREEN : Color.RED);
        showLine("gradient",
            legToWorldVisualPosition(new Vec3(0, 0, -0.5), AnimationTickHolder.getPartialTicks()),
            legToWorldVisualPosition(new Vec3(0, 0, 0.5), AnimationTickHolder.getPartialTicks())
        )
            .colored(Color.WHITE);
        for (Leg leg : legs) {
            showPoint(leg + "local", legToWorldVisualPosition(leg.offset, AnimationTickHolder.getPartialTicks()));
            showPoint(leg + "balanced", legToWorldVisualPosition(new Vec3(0, 0, 0), AnimationTickHolder.getPartialTicks()))
                .colored(balanced ? Color.GREEN : Color.RED);
            showLine(leg + "rotationAxis",
                legToWorldVisualPosition(leg.offset.add(-0.5, 0, 0), AnimationTickHolder.getPartialTicks()),
                legToWorldVisualPosition(leg.offset.add(0.5, 0, 0), AnimationTickHolder.getPartialTicks())
            );
            showPoint(leg + "anchor", leg.worldPosition);

            showBox(leg + "groundcollider", leg.getOnGroundColliderOfLeg())
                .colored(leg.onGround ? Color.RED : Color.GREEN)
                .lineWidth(1 / 32f);
            showBox(leg + "ingroundcollider", leg.getInGroundColliderOfLeg())
                .colored(leg.inGround ? Color.RED : new Color(0x00aa11))
                .lineWidth(1 / 32f);
            showBox(leg + "buriedcollider", leg.getBuriedColliderOfLeg())
                .colored(leg.buried ? Color.BLACK : new Color(0x0000ff))
                .lineWidth(1 / 32f);
        }
        legStepManager.renderDebug();
    }
    
    protected Vec3 legToWorldVisualPosition(Vec3 offset, float partialTicks) {
        return offset.relative(Direction.UP, offset.get(axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X) * Mth.lerp(partialTicks, gradient, targetGradient))
            .add(position.lerp(targetPosition, partialTicks));
    }
    
    protected Vec3 legToWorldPosition(Vec3 offset) {
        return offset.relative(Direction.UP, offset.get(axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X) * physicalGradient)
            .add(physicalPosition);
    }
    
    public void tick() {
        if (!initialised)
            initialise();
        
        if (legs.isEmpty()) return;
        if (legs.stream().anyMatch(leg -> leg.withUpdatedPosition(this).checkColliderBuried(level)))
            return;
        
        //Make sure all legs are updated in their position before tick
        legs.forEach(leg -> leg.withUpdatedPosition(this));
        legStepManager.tickTargetPositions();
        
        Vec3 movementImpulse = new Vec3(0, 0, 0.05);
        Vec3 movementDelta = movementImpulse;
        Vec3 constrainedDelta = legStepManager.constrainMovement(movementDelta);
        if (movementDelta.dot(constrainedDelta) < 0)
            movementDelta = Vec3.ZERO;
        else
            movementDelta = constrainedDelta;
        physicalPosition = physicalPosition.add(movementDelta);
        
        if (!trySettlePosition()) return;
        
        //AOA of the vehicle
        //Relative balances across the vehicle
        if (balanceInfo.isUnstable()) return;
        boolean hasRebalanced = false;
        balanced = false;
        int maxIteration = 100;
        int iteration = 0;
        while (!balanced) {
            if (iteration > maxIteration) break;
            iteration++;
            //Check if supported
            boolean hasFrontBalance = balanceInfo
                .getAllFrontBalances().stream().anyMatch(
                    balance -> balance.legs().stream().anyMatch(
                        leg -> leg.withUpdatedPosition(this).checkCollidesOnGround(level)
                    )
                );
            boolean hasBackBalance = balanceInfo
                .getAllBackBalances().stream().anyMatch(
                    balance -> balance.legs().stream().anyMatch(
                        leg -> leg.withUpdatedPosition(this).checkCollidesOnGround(level)
                    )
                );
            balanced = hasFrontBalance && hasBackBalance;
            
            //If it is unbalanced
            if (!balanced) {
                Direction.AxisDirection imbalanceDirection = hasFrontBalance ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE;
                Pair<BalanceInfo.Balance, BalanceInfo.Balance> balancePair = balanceInfo.getBalancePair(imbalanceDirection);
                
                double gradientDelta = (imbalanceDirection.getStep() * -0.1f) / Math.abs(balancePair.first().offset());
                gradientDelta = Math.max(Math.min(physicalGradient + gradientDelta, 1), -1) - physicalGradient;
                
                if (gradientDelta == 0) break; //Prevent iterations when no change is or can be done
                
                double positionDelta = -(gradientDelta * balanceInfo.getBalancePair(imbalanceDirection).second().offset());
                
                physicalPosition = physicalPosition.add(0, positionDelta, 0);
                physicalGradient += (float) gradientDelta;
                hasRebalanced = true;
            }
        }
        
        if (hasRebalanced)
            trySettlePosition();
        
        //Make sure all legs are updated in their position before tick
        legs.forEach(leg -> leg.withUpdatedPosition(this));
        legStepManager.tickStepping(movementImpulse);
        
        gradient = targetGradient;
        position = targetPosition;
        
        //Gradient has a "wobble" tendency because the code is shit, TODO : change from iterative to not iterative because its really dumb
        if (Math.abs(physicalGradient - targetGradient) > 0.01)
            targetGradient = Mth.lerp(0.2f, targetGradient, physicalGradient);
        targetPosition = targetPosition.lerp(physicalPosition, 0.05f);
        
        //Reduce the target gradient slightly and increase the position, this helps to smooth small bumps when flat
        float gradientAbsorption = (float) Math.min(Math.max(Math.abs(targetGradient), 0), 0.01);
        targetPosition.add(0, gradientAbsorption * 2, 0);
        targetGradient = targetGradient - (targetGradient < 0 ? -gradientAbsorption : gradientAbsorption);
    }
    
    private void initialise() {
        legs.forEach(leg -> leg.withUpdatedPosition(this));
        Direction.Axis balanceAxis = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        balanceInfo = new BalanceInfo(legs, balanceAxis);
        legStepManager = new LegStepManager(this);
        initialised = true;
    }
    
    protected boolean trySettlePosition() {
        //Falling + Rising
        int maxIteration = 100; //Hard limits to prevent a freak
        int iteration = 0;
        
        boolean inGround =
            legs.stream().anyMatch(leg -> leg.withUpdatedPosition(this).checkCollidesInGround(level));
        boolean onGround =
            legs.stream().anyMatch(leg -> leg.withUpdatedPosition(this).checkCollidesOnGround(level));
        boolean onClearGround = !inGround && onGround;
        
        while (maxIteration > iteration) {
            if (onClearGround) break;
            
            iteration++;
            if (inGround) {
                if (
                    legs.stream()
                        .anyMatch(leg -> leg.withUpdatedPosition(this).checkCollidesInGround(level))
                ) {
                    physicalPosition = physicalPosition.add(0, 0.01, 0);
                } else break;
            } else {
                if (
                    legs.stream()
                        .noneMatch(leg -> leg.withUpdatedPosition(this).checkCollidesOnGround(level))
                ) {
                    physicalPosition = physicalPosition.subtract(0, 0.01, 0);
                } else break;
            }
        }
        
        inGround =
            legs.stream().anyMatch(leg -> leg.withUpdatedPosition(this).checkCollidesInGround(level));
        onGround =
            legs.stream().anyMatch(leg -> leg.withUpdatedPosition(this).checkCollidesOnGround(level));
        onClearGround = (!inGround) && onGround;
        
        return onClearGround;
    }
    
    public void setCurrentLevel(ClientLevel level) {
        this.level = level;
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
    
    protected static Outline.OutlineParams showBox(String slot, AABB box) {
        return CreateClient.OUTLINER.showAABB(slot, box).lineWidth(2 / 16f);
    }
    
    public boolean isInitialised() {
        return initialised;
    }
    
}
