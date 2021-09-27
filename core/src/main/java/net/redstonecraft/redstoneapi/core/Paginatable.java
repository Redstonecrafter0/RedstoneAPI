package net.redstonecraft.redstoneapi.core;

public interface Paginatable<T> {

    public Pagination<T> paginate(int pageSize, int page) throws Throwable;

}
