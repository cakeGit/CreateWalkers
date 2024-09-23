package com.cak.walkers.foundation.vehicle.implementation;

import com.cak.walkers.foundation.vehicle.AttachedLeg;
import com.cak.walkers.foundation.vehicle.BalanceInfo;
import com.cak.walkers.foundation.vehicle.LegPhysics;
import com.cak.walkers.foundation.vehicle.LegPhysicsManager;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.outliner.Outline;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AbstractVehicleImplementation {
    
    protected Level level;
    protected boolean initialised = false;
    
    /**
     * Positions relative to the anchor of the vehicle
     */
    protected Set<AttachedLeg> legs = new HashSet<>();
    
    /**"physics" are done from these positions, but outside this implementation, the chasing position should be used*/
    protected Vec3 position = new Vec3(0, 0, 0);
    protected double gradient = 0f;
    protected float yRot = 0f;
    protected float rotationOffset;
    
    protected Vec3 chasingPosition = new Vec3(0, 0, 0);
    protected double chasingGradient = 0f;
    protected float chasingYRot = 0f;
    
    protected boolean balanced = true;
    private Direction.Axis forwardsAxis;
    private  Direction.Axis balanceAxis;
    
    /**
     * Handles movement limits, as well as the visual positions of the legs
     */
    protected LegPhysicsManager legPhysicsManager;
    /**
     * Handles balancing, i.e. tracking legs on the front and back
     */
    protected BalanceInfo balanceInfo;
    
    Double fallAcceleration = 9.8/40;
    Double fallVelocity = 0d;
    boolean isFalling = false;
    
    public AbstractVehicleImplementation(Collection<Vec3> legPositions, Direction forwardsDirection) {
        this.forwardsAxis = forwardsDirection.getAxis();
        this.rotationOffset = forwardsAxis == Direction.Axis.Z ? (float) Math.PI / 2f : 0;
        this.balanceAxis = this.forwardsAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        
        this.legs = legPositions.stream().map(
            legPosition -> new AttachedLeg(this, legPosition, leg -> new LegPhysics(leg, this))
        ).collect(Collectors.toSet());
    }
    
    public void renderDebug() {
        showPoint("balanced", legToWorldVisualPosition(new Vec3(0, 0, 0), AnimationTickHolder.getPartialTicks()))
            .colored(balanced ? Color.GREEN : Color.RED);
        showLine("gradient",
            legToWorldVisualPosition(new Vec3(0, 0, -0.5), AnimationTickHolder.getPartialTicks()),
            legToWorldVisualPosition(new Vec3(0, 0, 0.5), AnimationTickHolder.getPartialTicks())
        )
            .colored(Color.WHITE);
        for (AttachedLeg leg : legs) {
            showPoint(leg + "local", legToWorldVisualPosition(leg.getOffset(), AnimationTickHolder.getPartialTicks()));
        }
        legPhysicsManager.renderDebug();
    }
    
    public Vec3 legToWorldVisualPosition(Vec3 offset, float partialTicks) {
        return offset.relative(Direction.UP, offset.get(forwardsAxis) * Mth.lerp(partialTicks, chasingGradient, gradient))
            .yRot(Mth.lerp(partialTicks, chasingYRot, yRot))
            .add(chasingPosition.lerp(position, partialTicks));
    }
    
    public Vec3 legToWorldPosition(Vec3 offset) {
        return offset.relative(Direction.UP, offset.get(forwardsAxis) * gradient)
            .yRot(yRot)
            .add(position);
    }
    
    public double getYOffsetAtForwardOffset(double forwardOffset) {
        return forwardOffset * gradient;
    }
    
    protected Vec3 movementImpulse = new Vec3(0, 0, 0);
    protected float rotationImpulse = 0f;
    
    /**This is falling apart, i think its time i start adding some documentation*/
    public void tick() {
        chasingGradient = gradient;
        chasingPosition = position;
        chasingYRot = yRot;
        
        //Avoids falling on the first tick, cause that happens? i guess?
        boolean firstTick = false;
        if (!initialised) {
            firstTick = true;
            initialise();
        }
        
        if (level == null  || balanceInfo.isUnstable()) return;
        
        legPhysicsManager.tick();
        
        movementImpulse = movementImpulse.normalize().scale(0.1);
        movementImpulse = movementImpulse.yRot(yRot);
        
        legPhysicsManager.tickStepping(movementImpulse, yRot + rotationImpulse);
        
        rotationImpulse = Mth.clamp(
            legPhysicsManager.constrainRotation(rotationImpulse),
            -Math.abs(rotationImpulse), Math.abs(rotationImpulse)
        ) * 0.05f;
        yRot += rotationImpulse;
        
        movementImpulse = legPhysicsManager.constrainMovement(movementImpulse);
        
        double yPos = position.y - 2;
        legPhysicsManager.tickLegYConstraints();
        
        if (legPhysicsManager.isSupported()) {
            if (isFalling) {
                isFalling = false;
            }
            
            Vec3 backLegsAnchor = legPhysicsManager.tickVehicleBackLegsPositions();
            double backLegsOffset = backLegsAnchor.get(forwardsAxis);
            Vec3 frontLegsAnchor = legPhysicsManager.tickVehicleFrontLegsPositions();
            double frontLegsOffset = frontLegsAnchor.get(forwardsAxis);
            
            yPos = (backLegsAnchor.y + frontLegsAnchor.y) / 2;
            
            double totalOffset = frontLegsOffset - backLegsOffset;
            
            double gradient = (frontLegsAnchor.y - backLegsAnchor.y) / (totalOffset);
            
            //Max gradient = 1
            double backMin = frontLegsAnchor.y - (totalOffset * 1);
            double frontMin = backLegsAnchor.y - (totalOffset * 1);
            
            gradient = Mth.clamp(gradient, -1, 1);
            
            this.gradient = gradient;
            
            if (backLegsAnchor.y < backMin) {
                yPos += backLegsAnchor.y-backMin;
            } else if (frontLegsAnchor.y < frontMin) {
                yPos += frontLegsAnchor.y-frontMin;
            }
        } else if (!legPhysicsManager.isPartlySupported() && !firstTick) {
            if (!isFalling) {
                isFalling = true;
                fallVelocity = 0d;
            }
            
            fallVelocity += fallAcceleration;
            fallVelocity = Math.min(fallVelocity, 1);
            yPos -= fallVelocity;
            legPhysicsManager.fall(fallVelocity);
        }
        
        position = position
            .add(movementImpulse)
            .with(Direction.Axis.Y, yPos + 2);
        yRot += rotationImpulse;
    }
    
    private void initialise() {
        balanceInfo = new BalanceInfo(legs, getForwardsAxis());
        legPhysicsManager = new LegPhysicsManager(this);
        legPhysicsManager.setLevel(level);
        initialised = true;
    }
    
    public void setCurrentLevel(Level level) {
        this.level = level;
        if (legPhysicsManager != null)
            legPhysicsManager.setLevel(level);
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
    
    public Direction.Axis getForwardsAxis() {
        return forwardsAxis;
    }
    
    public Direction.Axis getSideAxis() {
        return balanceAxis;
    }
    
    public Set<AttachedLeg> getAttachedLegs() {
        return legs;
    }
    
    public double currentY() {
        return position.y;
    }
    
    public double currentY(float partialTicks) {
        return Mth.lerp(partialTicks, chasingPosition.y, position.y);
    }
    
    public double currentYAtOffset(double v) {
        return legToWorldPosition(Vec3.ZERO.with(forwardsAxis, v)).y;
    }
    
    public BalanceInfo getBalanceInfo() {
        return balanceInfo;
    }
    
    public float getYRot() {
        return yRot;
    }
    
    public float getPrimaryYRot() {
        return yRot;
    }
    
    public float getYRot(float partialTicks) {
        return Mth.lerp(partialTicks, chasingYRot, yRot);
    }
    
    public float getGradient(float partialTicks) {
        return (float) Mth.lerp(partialTicks, chasingGradient, gradient);
    }
    
    public Vec3 getPosition() {
        return position;
    }
    
    public void setPosition(int x, int y, int z) {
        this.position = new Vec3(x, y, z);
        this.chasingPosition = this.position;
    }
    
    public float getRotationOffset() {
        return rotationOffset;
    }
    
    public void setPosition(Vec3 position) {
        this.position = position;
        this.chasingPosition = this.position;
    }
    
}
