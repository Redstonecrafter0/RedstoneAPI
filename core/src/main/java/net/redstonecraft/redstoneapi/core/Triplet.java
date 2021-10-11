package net.redstonecraft.redstoneapi.core;

/**
 * A tuple with the length of 3
 *
 * @author Redstonecrafter0
 * @since 1.4
 * @param <F> type of the first element
 * @param <S> type of the second element
 * @param <T> type of the third element
 */
public record Triplet<F, S, T>(F first, S second, T third) {
}
