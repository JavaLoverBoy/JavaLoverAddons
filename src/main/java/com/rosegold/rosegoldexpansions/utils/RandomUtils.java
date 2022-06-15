package com.rosegold.rosegoldexpansions.utils;

import java.util.Random;

public class RandomUtils {

    private static final Random rand = new Random();
    public static double randBetween(final double a, final double b) {
        return rand.nextDouble() * (b - a) + a;
    }

    public static float randBetween(final float a, final float b) {
        return rand.nextFloat() * (b - a) + a;
    }
}
