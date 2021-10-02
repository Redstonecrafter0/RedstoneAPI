package net.redstonecraft.redstoneapi.tools;

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
