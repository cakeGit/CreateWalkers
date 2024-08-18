package com.cak.walkers.content.registry;

import com.cak.walkers.content.contraption.VehicleContraption;
import com.simibubi.create.content.contraptions.ContraptionType;

public class WalkersContraptionTypes {
    
    public static final ContraptionType WALKER = ContraptionType.register("walker", VehicleContraption::new);
    
    public static void register() {}
    
}
