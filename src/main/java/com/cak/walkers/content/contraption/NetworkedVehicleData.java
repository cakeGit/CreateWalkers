package com.cak.walkers.content.contraption;

import com.cak.walkers.foundation.vehicle.AttachedLeg;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

import java.util.*;

public class NetworkedVehicleData {
    
    public static final HashMap<UUID, VehicleContraptionEntity> VEHICLE_ANIMATION_TARGETS = new HashMap<>();
    
    HashMap<BlockPos, NetworkedLegData> contraptionLegData = new HashMap<>();
    HashMap<AttachedLeg, NetworkedLegData> legsByAttachment = new HashMap<>();
    
    VehicleContraptionEntity owner;
    
    public NetworkedVehicleData(VehicleContraptionEntity owner) {
        this.owner = owner;
    }
    
    protected void update(VehicleContraptionEntity vce) {
        for (Map.Entry<BlockPos, AttachedLeg> entry : vce.vehicle.getLegsByStructureAnchor().entrySet()) {
            
            BlockPos anchor = entry.getKey();
            AttachedLeg leg = entry.getValue();
            
            if (!legsByAttachment.containsKey(leg)) {
                NetworkedLegData legData = new NetworkedLegData();
                legData.leg = leg;
                legsByAttachment.put(leg, legData);
                contraptionLegData.put(anchor, legData);
            }
        
            NetworkedLegData legData = legsByAttachment.get(leg);
            legData.update(leg);
        }
    }
    
    public CompoundTag write(CompoundTag tag, VehicleContraptionEntity vce) {
        update(vce);
        
        int i = 0;
        for (Map.Entry<BlockPos, NetworkedLegData> entry : contraptionLegData.entrySet()) {
            i++;
            
            tag.put("pos_" + i, NbtUtils.writeBlockPos(entry.getKey()));
            
            CompoundTag dataTag = new CompoundTag();
            entry.getValue().write(dataTag);
            tag.put("data_" + i, dataTag);
        }
        
        return tag;
    }
    
    public void read(CompoundTag tag) {
        List<BlockPos> entriesToRemove = new ArrayList<>(contraptionLegData.keySet().stream().toList());
        
        int i = 0;
        while (i < 1024) {
            i++;
            
            if (!tag.contains("pos_" + i))
                return;
            
            BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("pos_" + i));
            entriesToRemove.remove(pos);
            
            NetworkedLegData legData = contraptionLegData.getOrDefault(pos, new NetworkedLegData());
            legData.read(tag.getCompound("data_" + i));
            
            contraptionLegData.put(pos, legData);
        }
    
        for (BlockPos pos : entriesToRemove)
            contraptionLegData.remove(pos);
    }
    
    public void tickAnimations() {
        for (NetworkedLegData legData : contraptionLegData.values()) {
            legData.tickAnimation();
        }
    }
    
}
