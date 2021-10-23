package net.redstonecraft.redstoneapi.core.interfaces;

import java.util.stream.Stream;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public interface Streamable<T> {

    Stream<T> stream();

}
