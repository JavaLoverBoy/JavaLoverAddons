package com.rosegold.rosegoldexpansions.utils;

public class MathUtils {

    public static double linearInterpolate(double start, double end, double partial) {
        return (end - start) * partial + start;
    }
}
