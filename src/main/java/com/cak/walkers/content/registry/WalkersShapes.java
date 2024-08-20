package com.cak.walkers.content.registry;

import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.BiFunction;

import static net.minecraft.core.Direction.UP;

/**Once more i wish to thank simi for the mit license, for i have suffered many difficulties, and shapes are not one i am ready for*/
public class WalkersShapes {
    
    public static final VoxelShaper
        VEHICLE_CONTROLLER = shape(0, 0, 8, 16, 16, 16).forHorizontal(Direction.NORTH),
        LEG_ANCHOR = shape(0, 0, 12, 16, 16, 16).forHorizontal(Direction.NORTH);
    
    
    private static Builder shape(VoxelShape shape) {
        return new Builder(shape);
    }
    
    private static Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }
    
    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }
    
    public static class Builder {
        
        private VoxelShape shape;
        
        public Builder(VoxelShape shape) {
            this.shape = shape;
        }
        
        public Builder add(VoxelShape shape) {
            this.shape = Shapes.or(this.shape, shape);
            return this;
        }
        
        public Builder add(double x1, double y1, double z1, double x2, double y2, double z2) {
            return add(cuboid(x1, y1, z1, x2, y2, z2));
        }
        
        public Builder erase(double x1, double y1, double z1, double x2, double y2, double z2) {
            this.shape = Shapes.join(shape, cuboid(x1, y1, z1, x2, y2, z2), BooleanOp.ONLY_FIRST);
            return this;
        }
        
        public VoxelShape build() {
            return shape;
        }
        
        public VoxelShaper build(BiFunction<VoxelShape, Direction, VoxelShaper> factory, Direction direction) {
            return factory.apply(shape, direction);
        }
        
        public VoxelShaper build(BiFunction<VoxelShape, Direction.Axis, VoxelShaper> factory, Direction.Axis axis) {
            return factory.apply(shape, axis);
        }
        
        public VoxelShaper forDirectional(Direction direction) {
            return build(VoxelShaper::forDirectional, direction);
        }
        
        public VoxelShaper forAxis() {
            return build(VoxelShaper::forAxis, Direction.Axis.Y);
        }
        
        public VoxelShaper forHorizontalAxis() {
            return build(VoxelShaper::forHorizontalAxis, Direction.Axis.Z);
        }
        
        public VoxelShaper forHorizontal(Direction direction) {
            return build(VoxelShaper::forHorizontal, direction);
        }
        
        public VoxelShaper forDirectional() {
            return forDirectional(UP);
        }
        
    }

}
