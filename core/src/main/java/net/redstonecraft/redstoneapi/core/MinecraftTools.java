package net.redstonecraft.redstoneapi.core;

import java.util.Random;

/**
 * Small utility class for minecraft functions
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
@SuppressWarnings("unused")
public class MinecraftTools {

    /**
     * Return true if the provided chunk is a slime chunk
     *
     * @param seed the seed of the world
     * @param x the x coordinate of the chunk (not of the block)
     * @param z the z coordinate of the chunk (not of the block)
     * @return whether the chunk is a slime chunk or not
     * */
    public static boolean isSlimeChunk(long seed, int x, int z) {
        return new Random(seed + ((long) x * x * 0x4c1906) + (x * 0x5ac0dbL) + ((long) z * z) * 0x4307a7L + (z * 0x5f24fL) ^ 0x3ad8025fL).nextInt(10) == 0;
    }

    /**
     * Shortcut for converting a seed in String format to the used long
     *
     * @param seed seed as {@link String}
     * @return seed as long
     * */
    public static long textToSeed(String seed) {
        return seed.hashCode();
    }

}
