package net.redstonecraft.redstoneapi.core;

import java.util.*;

/**
 * @author Redstonecrafter0
 * @since 1.4
 */
public class IterUtils {

    public static <F, L> Iterable<Pair<F, L>> zipFillNull(Iterable<F> f, Iterable<L> l) {
        return zipFillNull(f.iterator(), l.iterator());
    }

    public static <F, L> Iterable<Pair<F, L>> zipFillNull(Iterator<F> f, Iterator<L> l) {
        return new ZipIterator<>(true, f, l);
    }

    public static <F, L> Iterable<Pair<F, L>> zip(Iterable<F> f, Iterable<L> l) {
        return zip(f.iterator(), l.iterator());
    }

    public static <F, L> Iterable<Pair<F, L>> zip(Iterator<F> f, Iterator<L> l) {
        return new ZipIterator<>(false, f, l);
    }

    public static <F, L> Map<F, L> zipToMapFillNull(Iterable<F> f, Iterable<L> l) throws NullPointerException {
        return zipToMapFillNull(f.iterator(), l.iterator());
    }

    public static <F, L> Map<F, L> zipToMapFillNull(Iterator<F> f, Iterator<L> l) throws NullPointerException {
        Map<F, L> map = new HashMap<>();
        for (Pair<F, L> i : new ZipIterator<>(true, f, l)) {
            map.put(i.first(), i.second());
        }
        return map;
    }

    public static <F, L> Map<F, L> zipToMap(Iterable<F> f, Iterable<L> l) {
        return zipToMap(f.iterator(), l.iterator());
    }

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
