package com.artezio.artping.service.utils;

public class MathUtils {
    public static int getRandomNumber(int min, int max) {
        int rand = (int) ((Math.random() * (max+1 - min)) + min);
        return Math.min(rand, max);
    }
}
