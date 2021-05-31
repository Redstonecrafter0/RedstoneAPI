package net.redstonecraft.redstoneapi.tools;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Pagination<T> implements Iterable<T> {

    private final List<T> items;
    private final int pageSize;
    private final int page;

    public Pagination(Collection<T> items, int pageSize, int page) {
        this.items = new ArrayList<>(items);
        this.pageSize = pageSize;
        this.page = page;
    }

    public Pagination(T[] items, int pageSize, int page) {
        this.items = new ArrayList<>(Arrays.asList(items));
        this.pageSize = pageSize;
        this.page = page;
    }

    public List<T> getItems() {
        if (pageSize * (page + 1) > items.size() - 1) {
            return new ArrayList<>();
        }
        return items.subList(Integer.max(Integer.min(pageSize * (page), items.size() - 1), 0), Integer.max(pageSize * (page + 1), 0));
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPage() {
        return page;
    }

    public Iterator<T> iterator() {
        return getItems().iterator();
    }

    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        getItems().forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return getItems().spliterator();
    }

}
