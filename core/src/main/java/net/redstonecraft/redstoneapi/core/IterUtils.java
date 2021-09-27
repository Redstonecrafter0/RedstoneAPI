package net.redstonecraft.redstoneapi.core;

import java.util.*;

/**
 * @author Redstonecrafter0
 * @since 1.4
 */
public class IterUtils {

    public static <F, L> List<Pair<F, L>> zip(Iterable<F> f, Iterable<L> l) {
        return zip(f.iterator(), l.iterator());
    }

    public static <F, L> List<Pair<F, L>> zip(Iterator<F> f, Iterator<L> l) {
        List<Pair<F, L>> list = new ArrayList<>();
        while (f.hasNext() && l.hasNext()) {
            list.add(new Pair<>(f.next(), l.next()));
        }
        return list;
    }

    public static <F, L> Map<F, L> zipToMap(Iterable<F> f, Iterable<L> l) {
        return zipToMap(f.iterator(), l.iterator());
    }

    public static <F, L> Map<F, L> zipToMap(Iterator<F> f, Iterator<L> l) {
        Map<F, L> map = new HashMap<>();
        while (f.hasNext() && l.hasNext()) {
            map.put(f.next(), l.next());
        }
        return map;
    }

}
