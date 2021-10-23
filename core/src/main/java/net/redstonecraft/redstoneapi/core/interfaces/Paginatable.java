package net.redstonecraft.redstoneapi.core.interfaces;

import net.redstonecraft.redstoneapi.core.tools.Pagination;

/**
 * @param <T> element type to paginate
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public interface Paginatable<T> {

    /**
     * @param pageSize how many items can be on a page
     * @param page index of the page
     * @return a {@link Pagination} object
     * @throws Throwable if any error occurs
     */
    Pagination<T> paginate(int pageSize, int page) throws Throwable;

}
