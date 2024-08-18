package com.cak.walkers.content.registry;

import com.cak.walkers.content.components.controller.VehicleControllerBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.cak.walkers.Walkers.REGISTRATE;

public class WalkersBlockEntityTypes {
    
    public static BlockEntityEntry<VehicleControllerBlockEntity> VEHICLE_BLOCK_CONTROLLER_BLOCK_ENTITY = REGISTRATE
        .blockEntity("vehicle_controller_block_entity", VehicleControllerBlockEntity::new)
        .validBlock(WalkersRegistry.VEHICLE_BLOCK_CONTROLLER)
        .register();
    
    public static void register() {}
    
}
