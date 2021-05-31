package net.redstonecraft.redstoneapi.tools;

public interface Paginatable<T> {

    public Pagination<T> paginate(int pageSize, int page) throws Throwable;

}
