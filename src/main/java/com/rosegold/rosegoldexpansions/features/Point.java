package com.rosegold.rosegoldexpansions.features;

import net.minecraft.util.math.Vec3d;

public class Point {

    private Vec3d position;
    private boolean pathfind;

    public Point(Vec3d position, boolean pathfind) {
        this.position = position;
        this.pathfind = pathfind;
    }

    public Point(Vec3d position) {
        this.position = position;
        pathfind = false;
    }

    public Vec3d getPosition() {
        return this.position;
    }

    public boolean doPathfind() {
        return this.pathfind;
    }
}
