package net.redstonecraft.redstoneapi.core;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Paginate through a {@link Collection} or an {@link java.lang.reflect.Array} of items you want to paginate
 *
 * @since 1.3
 * @author Redstonecrafter0
 * */
@SuppressWarnings("unused")
public class Pagination<T> implements Iterable<T> {

    private final List<T> items;
    private final int pageSize;
    private final int page;

    /**
     * @param items all items
     * @param pageSize how many items can be on a page
     * @param page index of the page
     */
    public Pagination(Collection<T> items, int pageSize, int page) {
        this.items = new ArrayList<>(items);
        this.pageSize = pageSize;
        this.page = page;
    }

    /**
     * @param items all items
     * @param pageSize how many items can be on a page
     * @param page index of the page
     */
    public Pagination(T[] items, int pageSize, int page) {
        this.items = new ArrayList<>(Arrays.asList(items));
        this.pageSize = pageSize;
        this.page = page;
    }

    /**
     * @return the section calculated for the pagination
     */
    public List<T> getItems() {
        if (pageSize * (page + 1) > items.size() - 1) {
            return new ArrayList<>();
        }
        return items.subList(Integer.max(Integer.min(pageSize * (page), items.size() - 1), 0), Integer.max(pageSize * (page + 1), 0));
    }

    /**
     * @return the specified page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @return the specified page index
     */
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

    /**
     * @param pageSize how many items can be on a page
     * @param page index of the page
     * @param <T> the type of the elements in the pagination
     * @return a {@link Collector} for the stream chain to get a {@link Pagination} object
     */
    public static <T> Collector<T, ?, Pagination<T>> collect(int pageSize, int page) {
        return new Collector<T, ArrayList<T>, Pagination<T>>() {
            @Override
            public Supplier<ArrayList<T>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<ArrayList<T>, T> accumulator() {
                return ArrayList::add;
            }

            @Override
            public BinaryOperator<ArrayList<T>> combiner() {
                return (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                };
            }

            @Override
            public Function<ArrayList<T>, Pagination<T>> finisher() {
                return list -> new Pagination<>(list, pageSize, page);
            }

            @Override
            public Set<Characteristics> characteristics() {
                return new HashSet<>();
            }
        };
    }

}
