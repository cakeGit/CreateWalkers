package com.cak.walkers.foundation.vehicle;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Leg {
    
    Vec3 offset;
    Vec3 worldPosition;
    boolean onGround = false;
    boolean inGround = false;
    boolean buried = false;
    
    public Leg(Vec3 position) {
        offset = position;
        worldPosition = position;
    }
    
    public AABB getDebugFootOfLeg() {
        Vec3 anchor = worldPosition.subtract(0, 1.3, 0);
        return new AABB(
            anchor.subtract(0.4, 0.2, 0.4),
            anchor.add(0.4, 0.2, 0.4)
        );
    }
    
    public AABB getBuriedColliderOfLeg() {
        Vec3 anchor = worldPosition.subtract(0, 0.2, 0);
        return new AABB(
            anchor.subtract(0.4, 0, 0.4),
            anchor.add(0.4, 0.01, 0.4)
        );
    }
    
    public AABB getInGroundColliderOfLeg() {
        Vec3 anchor = worldPosition.subtract(0, 1.49, 0);
        return new AABB(
            anchor.subtract(0.4, 0, 0.4),
            anchor.add(0.4, 0.05, 0.4)
        );
    }
    
    public AABB getOnGroundColliderOfLeg() {
        Vec3 anchor = worldPosition.subtract(0, 1.5, 0);
        return new AABB(
            anchor.subtract(0.4, 0.05, 0.4),
            anchor.add(0.4, 0, 0.4)
        );
    }
    
    public boolean checkCollidesOnGround(Level level) {
        return onGround = checkForWorldCollision(getOnGroundColliderOfLeg(), level);
    }
    
    public boolean checkCollidesInGround(Level level) {
        return inGround = checkForWorldCollision(getInGroundColliderOfLeg(), level);
    }
    
    public boolean checkColliderBuried(Level level) {
        return buried = checkForWorldCollision(getBuriedColliderOfLeg(), level);
    }
    
    private boolean checkForWorldCollision(AABB collider, Level level) {
        for (VoxelShape shape : level.getCollisions(null, collider)) {
            for (AABB aabb : shape.toAabbs()) {
                if (aabb.intersects(collider))
                    return true;
            }
        }
        return false;
    }
    
    /**
     * Either this or visual position must be called before checking colliders
     */
    public Leg withUpdatedPosition(AbstractVehicleImplementation vehicle) {
        worldPosition = vehicle.legToWorldPosition(offset);
        return this;
    }
    
    //TODO: remove
    @Deprecated
    public Leg withVisualPosition(AbstractVehicleImplementation vehicle) {
        worldPosition = vehicle.legToWorldVisualPosition(offset);
        return this;
    }
    
}
