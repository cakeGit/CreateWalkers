package com.cak.walkers.content.contraption;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

import java.util.HashMap;
import java.util.Map;

public class NetworkedContraptionLegData {
    
    HashMap<BlockPos, NetworkedLegData> legData;
    
    public void write(CompoundTag tag) {
        for (Map.Entry<BlockPos, NetworkedLegData> entry : legData.entrySet()) {
            tag.put("pos", NbtUtils.writeBlockPos(entry.getKey()));
            
            CompoundTag dataTag = new CompoundTag();
            entry.getValue().write(dataTag);
            tag.put("data", dataTag);
        }
    }
    
    public void read(CompoundTag tag) {
    
    }
    
}
