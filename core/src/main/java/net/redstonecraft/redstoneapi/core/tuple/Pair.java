package net.redstonecraft.redstoneapi.core.tuple;

/**
 * A tuple with the length of 2
 *
 * @author Redstonecrafter0
 * @since 1.4
 * @param <F> the type of the first element
 * @param <S> the type of the second element
 */
public record Pair<F, S>(F first, S second) {
}
