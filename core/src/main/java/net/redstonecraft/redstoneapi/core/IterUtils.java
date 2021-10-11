package net.redstonecraft.redstoneapi.core;

import java.util.*;

/**
 * A toolkit class for zipping {@link Iterator}s and {@link Iterable}s
 * to a single {@link Iterable} with the combined values of both given {@link Iterable}s.
 *
 * @author Redstonecrafter0
 * @since 1.4
 */
@SuppressWarnings("unused")
public class IterUtils {

    /**
     * Creates an iterable with the combined values of both given iterables.
     * Filling with null if one iterable is shorter than the other.
     *
     * @param f an iterable
     * @param l an iterable
     * @param <F> type of the first iterable
     * @param <L> type of the second iterable
     * @return an iterable of both given iterables combined
     */
    public static <F, L> Iterable<Pair<F, L>> zipFillNull(Iterable<F> f, Iterable<L> l) {
        return zipFillNull(f.iterator(), l.iterator());
    }

    /**
     * Creates an iterable with the combined values of both given iterators.
     * Filling with null if one iterator is shorter than the other.
     *
     * @param f an iterator
     * @param l an iterator
     * @param <F> type of the first iterator
     * @param <L> type of the second iterator
     * @return an iterable of both given iterator combined
     */
    public static <F, L> Iterable<Pair<F, L>> zipFillNull(Iterator<F> f, Iterator<L> l) {
        return new ZipIterator<>(true, f, l);
    }

    /**
     * Creates an iterable with the combined values of both given iterators.
     * In case one of the iterables is shorter than the other the returned iterable ends.
     *
     * @param f an iterable
     * @param l an iterable
     * @param <F> type of the first iterable
     * @param <L> type of the second iterable
     * @return an iterable of both given iterables combined
     */
    public static <F, L> Iterable<Pair<F, L>> zip(Iterable<F> f, Iterable<L> l) {
        return zip(f.iterator(), l.iterator());
    }

    /**
     * Creates an iterable with the combined values of both given iterators.
     * in case one of the iterator is shorter than the other the returned iterable ends.
     *
     * @param f an iterator
     * @param l an iterator
     * @param <F> type of the first iterator
     * @param <L> type of the second iterator
     * @return an iterable of both given iterator combined
     */
    public static <F, L> Iterable<Pair<F, L>> zip(Iterator<F> f, Iterator<L> l) {
        return new ZipIterator<>(false, f, l);
    }

    /**
     * Creates a map with the combined values of both given iterables.
     * Filling with null if one iterable is shorter than the other.
     *
     * @param f the iterable used for the key
     * @param l the iterable used for the values
     * @param <F> type of the first iterable
     * @param <L> type of the second iterable
     * @return a map of the combined iterables
     */
    public static <F, L> Map<F, L> zipToMapFillNull(Iterable<F> f, Iterable<L> l) throws NullPointerException {
        return zipToMapFillNull(f.iterator(), l.iterator());
    }

    /**
     * Creates a map with the combined values of both given iterators.
     * Filling with null if one iterator is shorter than the other.
     *
     * @param f the iterator used for the key
     * @param l the iterator used for the values
     * @param <F> type of the first iterator
     * @param <L> type of the second iterator
     * @return a map of the combined iterators
     */
    public static <F, L> Map<F, L> zipToMapFillNull(Iterator<F> f, Iterator<L> l) throws NullPointerException {
        Map<F, L> map = new HashMap<>();
        for (Pair<F, L> i : new ZipIterator<>(true, f, l)) {
            map.put(i.first(), i.second());
        }
        return map;
    }

    /**
     * Creates a map with the combined values of both given iterables.
     * Ends where one iterable is shorter than the other.
     *
     * @param f the iterable used for the key
     * @param l the iterable used for the values
     * @param <F> type of the first iterable
     * @param <L> type of the second iterable
     * @return a map of the combined iterables
     */
    public static <F, L> Map<F, L> zipToMap(Iterable<F> f, Iterable<L> l) {
        return zipToMap(f.iterator(), l.iterator());
    }

    /**
     * Creates a map with the combined values of both given iterators.
     * Ends where one iterator is shorter than the other.
     *
     * @param f the iterator used for the key
     * @param l the iterator used for the values
     * @param <F> type of the first iterator
     * @param <L> type of the second iterator
     * @return a map of the combined iterators
     */
    public static <F, L> Map<F, L> zipToMap(Iterator<F> f, Iterator<L> l) {
        Map<F, L> map = new HashMap<>();
        for (Pair<F, L> i : new ZipIterator<>(false, f, l)) {
            map.put(i.first(), i.second());
        }
        return map;
    }

    private record ZipIterator<F, L>(boolean fillNull, Iterator<F> f, Iterator<L> l) implements Iterator<Pair<F, L>>, Iterable<Pair<F, L>> {

        @Override
        public boolean hasNext() {
            return fillNull ? f.hasNext() || l.hasNext() : f.hasNext() && l.hasNext();
        }

        @Override
        public Pair<F, L> next() {
            if (fillNull && (f.hasNext() || l.hasNext())) {
                return new Pair<>(f.hasNext() ? f.next() : null, l.hasNext() ? l.next() : null);
            } else if (!fillNull && f.hasNext() && l.hasNext()) {
                return new Pair<>(f.next(), l.next());
            } else {
                throw new NoSuchElementException("No more elements");
            }
        }

        @Override
        public Iterator<Pair<F, L>> iterator() {
            return this;
        }

    }

}
