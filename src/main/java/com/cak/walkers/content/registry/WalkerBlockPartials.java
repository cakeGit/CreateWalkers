package com.cak.walkers.content.registry;

import com.cak.walkers.Walkers;
import com.jozufozu.flywheel.core.PartialModel;

public class WalkerBlockPartials {
    
    public static final PartialModel
        BASE_LEG = partial("leg/default/default_leg");
    
    private static PartialModel partial(String id) {
        return new PartialModel(Walkers.asResource("partials/" + id));
    }
    
    public static void register() {}
    
}
