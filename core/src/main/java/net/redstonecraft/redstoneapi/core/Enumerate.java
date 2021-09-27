package net.redstonecraft.redstoneapi.core;

import java.util.Iterator;

/**
 * Class for enumerating like the python enumerate
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class Enumerate<T> implements Iterable<Enumerate.Item<T>>, Iterator<Enumerate.Item<T>> {

    private final Iterator<T> iterator;
    private int count = -1;

    public Enumerate(Iterator<T> iterator) {
        this.iterator = iterator;
    }

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

    public static class Item<V> {

        private final int count;
        private final V value;

        private Item(int count, V value) {
            this.count = count;
            this.value = value;
        }

        public int getCount() {
            return count;
        }

        public V getValue() {
            return value;
        }
    }

}
