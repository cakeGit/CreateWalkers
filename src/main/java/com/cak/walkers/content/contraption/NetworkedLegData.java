package com.cak.walkers.content.contraption;

import com.cak.walkers.foundation.vehicle.AttachedLeg;
import com.cak.walkers.foundation.vehicle.LegPhysics;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import static com.cak.walkers.foundation.vehicle.LegPhysics.GETTOTAL_STEP_TICKSFREEOFFINALTYRANTY;

@Deprecated
@ApiStatus.ScheduledForRemoval
public class NetworkedLegData {
    
    /**Server only*/
    @Nullable AttachedLeg leg;
    
    Vec3 currentPosition;
    float currentYRot;
    
    Vec3 stepTargetPosition;
    
    boolean isDown;
    int currentStepTicks = 0;
    
    public NetworkedLegData() {
        //Fill details about the leg being rendered like the type, but that doesent exist yet, what a shame
    }
    
    public void tickAnimation() {
        if (!isDown)
            currentStepTicks++;
    }
    
    public void write(CompoundTag tag) {
        writeVec3(tag, "currentPosition", currentPosition);
        writeVec3(tag, "stepTargetPosition", stepTargetPosition);
        writeVec3(tag, "stepTargetPosition", stepTargetPosition);
        tag.putFloat("currentYRot", currentYRot);
        tag.putBoolean("isDown", isDown);
        tag.putInt("currentStepTicks", currentStepTicks);
    }
    
    public void read(CompoundTag tag) {
        currentPosition = readVec3(tag, "currentPosition");
        stepTargetPosition = readVec3(tag, "stepTargetPosition");
        stepTargetPosition = readVec3(tag, "stepTargetPosition");
        currentYRot = tag.getFloat("currentYRot");
        isDown = tag.getBoolean("isDown");
        currentStepTicks = tag.getInt("currentStepTicks");
    }
    
    private void writeVec3(CompoundTag tag, String name, Vec3 vec) {
        CompoundTag vectorTag = new CompoundTag();
        vectorTag.putDouble("x", vec.x);
        vectorTag.putDouble("y", vec.y);
        vectorTag.putDouble("z", vec.z);
        tag.put(name, vectorTag);
    }
    
    private Vec3 readVec3(CompoundTag tag, String name) {
        CompoundTag vectorTag = tag.getCompound(name);
        return new Vec3(
            vectorTag.getDouble("x"),
            vectorTag.getDouble("y"),
            vectorTag.getDouble("z")
        );
    }
    
    public void update(AttachedLeg leg) {
        LegPhysics physics = leg.getPhysics();
        currentPosition = physics.getCurrentPosition();
        stepTargetPosition = physics.getCurrentStepTargetPosition();
        //TODO currentYRot
        isDown = physics.isDown();
        currentStepTicks = physics.getCurrentStepTicks();
    }
    
    public float getAnimationStepProgress(float partialTicks) {
        return Mth.clamp((currentStepTicks + partialTicks) / (GETTOTAL_STEP_TICKSFREEOFFINALTYRANTY()), 0, 1);
    }
    
    public Vec3 clampYToBounds(Vec3 pos, double vehicleY) {
        return pos.with(Direction.Axis.Y, Mth.clamp(pos.y, vehicleY-3, vehicleY+1));
    }
    
    public Vec3 getVisualPosition(double vehicleY, float partialTicks) {
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
        return clampYToBounds(animatedPosition, vehicleY);
    }
    
}
