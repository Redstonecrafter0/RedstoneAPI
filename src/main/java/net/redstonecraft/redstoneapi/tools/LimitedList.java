package net.redstonecraft.redstoneapi.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Redstonecrafter0
 * @since 1.5
 */
public class LimitedList<E> extends ArrayList<E> {

    private final int maxSize;

    public LimitedList(int maxSize) {
        super(maxSize);
        this.maxSize = maxSize;
    }

    public LimitedList(int maxSize, Collection<? extends E> c) {
        super(cut(c, maxSize));
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(E e) {
        if (maxSize == size()) {
            remove(0);
        }
        return super.add(e);
    }

    private static <E> Collection<? extends E> cut(Collection<? extends E> c, int maxSize) {
        List<E> list = new ArrayList(c);
        while (list.size() > maxSize) {
            list.remove(list.size() - 1);
        }
        return list;
    }

}
