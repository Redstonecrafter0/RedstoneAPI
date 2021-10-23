package net.redstonecraft.redstoneapi.core.tools;

import java.util.Iterator;

/**
 * Class for enumerating like the python enumerate with the index and value.
 * <pre>{@code for (Enumerate.Item<E> i : new Enumerate(iterable)) {
 *     System.out.println(i.position() + " " + i.value());
 * }
 * }</pre>
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
@SuppressWarnings("unused")
public class Enumerate<T> implements Iterable<Enumerate.Item<T>>, Iterator<Enumerate.Item<T>> {

    private final Iterator<T> iterator;
    private int count = -1;

    /**
     * Constructs an enumerator from an iterator
     *
     * @param iterator iterator to enumerate
     */
    public Enumerate(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    /**
     * Constructs an enumerator from an iterable
     *
     * @param iterable iterable to enumerate
     */
    public Enumerate(Iterable<T> iterable) {
        this.iterator = iterable.iterator();
    }

    @Override
    public Iterator<Item<T>> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Item<T> next() {
        count++;
        return new Item<>(count, iterator.next());
    }

    public record Item<V>(int position, V value) {
    }

}
