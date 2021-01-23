package net.redstonecraft.redstoneapi.tools;

import java.util.Random;

public class MinecraftTools {

    public static boolean isSlimeChunk(long seed, int x, int z) {
        return new Random(seed + (int) (x * x * 0x4c1906) + (int) (x * 0x5ac0db) + (int) (z * z) * 0x4307a7L + (int) (z * 0x5f24f) ^ 0x3ad8025fL).nextInt(10) == 0;
    }

    public static long textToSeed(String seed) {
        return seed.hashCode();
    }

}
