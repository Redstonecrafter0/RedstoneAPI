package net.redstonecraft.redstoneapi.sql.orm;

import net.redstonecraft.redstoneapi.tools.Pagination;

import java.util.ArrayList;
import java.util.List;

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
