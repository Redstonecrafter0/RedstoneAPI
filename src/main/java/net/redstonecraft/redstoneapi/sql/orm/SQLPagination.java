package net.redstonecraft.redstoneapi.sql.orm;

import net.redstonecraft.redstoneapi.tools.Pagination;

import java.util.ArrayList;
import java.util.List;

/**
 * Paginate through a entries in your database
 *
 * @since 1.3
 * @author Redstonecrafter0
 * */
public class SQLPagination<T extends TableBase> extends Pagination<T> {

    private final List<T> items;

    SQLPagination(List<T> items, int pageSize, int page) {
        super(new ArrayList<>(), pageSize, page);
        this.items = items;
    }

    @Override
    public List<T> getItems() {
        return items;
    }

}
