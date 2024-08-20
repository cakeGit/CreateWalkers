package com.cak.walkers.content.registry;

import com.cak.walkers.content.components.anchor.LegAnchorBlock;
import com.cak.walkers.content.components.controller.VehicleControllerBlock;
import com.cak.walkers.content.components.controller.VehicleControllerMovingInteractionBehavior;
import com.simibubi.create.AllInteractionBehaviours;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.tterrag.registrate.util.entry.BlockEntry;

import static com.cak.walkers.Walkers.REGISTRATE;

public class WalkersBlocks {
    
    public static BlockEntry<VehicleControllerBlock> VEHICLE_CONTROLLER = REGISTRATE
        .block("vehicle_controller", VehicleControllerBlock::new)
        .onRegister(block -> AllInteractionBehaviours.registerBehaviour(block, new VehicleControllerMovingInteractionBehavior()))
        .blockstate(BlockStateGen.horizontalBlockProvider(false))
        .simpleItem()
        .register();
    
    
    public static BlockEntry<LegAnchorBlock> LEG_ANCHOR = REGISTRATE
        .block("leg_anchor", LegAnchorBlock::new)
        .blockstate(BlockStateGen.horizontalBlockProvider(false))
        .simpleItem()
        .register();
    
    public static void register() {}
    
}
