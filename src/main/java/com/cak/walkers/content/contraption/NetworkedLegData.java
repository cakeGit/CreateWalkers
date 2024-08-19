package com.cak.walkers.content.contraption;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class NetworkedLegData {
    
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
    
}
