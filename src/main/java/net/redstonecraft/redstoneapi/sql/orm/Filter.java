package net.redstonecraft.redstoneapi.sql.orm;

import net.redstonecraft.redstoneapi.sql.orm.types.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class is used for filtering queries in orm.
 * It is semantic
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class Filter {

    String queryString;
    List values;

    private Filter(String queryString, List values) {
        this.queryString = queryString;
        this.values = values;
    }

    public Filter and(Filter filter) {
        queryString += " AND " + filter.queryString;
        values.addAll(filter.values);
        return this;
    }

    public Filter or(Filter filter) {
        queryString += " OR " + filter.queryString;
        values.addAll(filter.values);
        return this;
    }

    public static <T> Filter equals(BaseType<T> i, T i1) {
        return new Filter(i.getKey() + " = ?", new ArrayList(Collections.singletonList(i1)));
    }

    public static <T> Filter equalsNot(BaseType<T> i, T i1) {
        return new Filter(i.getKey() + " <> ?", new ArrayList(Collections.singletonList(i1)));
    }

    public static Filter like(Text i, String pattern) {
        return new Filter(i.getKey() + " LIKE ?", new ArrayList(Collections.singletonList(pattern)));
    }

    public static Filter notLike(Text i, String pattern) {
        return new Filter(i.getKey() + " NOT LIKE ?", new ArrayList(Collections.singletonList(pattern)));
    }

    public static <T extends Number> Filter greater(SQLNumber<T> i, T i1) {
        return new Filter(i.getKey() + " > ?", new ArrayList(Collections.singletonList(i1)));
    }

    public static <T extends Number> Filter greaterEquals(SQLNumber<T> i, T i1) {
        return new Filter(i.getKey() + " >= ?", new ArrayList(Collections.singletonList(i1)));
    }

    public static <T extends Number> Filter less(SQLNumber<T> i, T i1) {
        return new Filter(i.getKey() + " < ?", new ArrayList(Collections.singletonList(i1)));
    }

    public static <T extends Number> Filter lessEquals(SQLNumber<T> i, T i1) {
        return new Filter(i.getKey() + " <= ?", new ArrayList(Collections.singletonList(i1)));
    }

    public static <T extends Number> Filter between(SQLNumber<T> i, T i1, T i2) {
        return new Filter(i.getKey() + " BETWEEN ? AND ?", new ArrayList(Arrays.asList(i1, i2)));
    }

    public static <T extends Number> Filter notBetween(SQLNumber<T> i, T i1, T i2) {
        return new Filter(i.getKey() + " NOT BETWEEN ? AND ?", new ArrayList(Arrays.asList(i1, i2)));
    }

    public static <T> Filter in(BaseType<T> i, List<T> in) {
        if (in.size() > 0) {
            List<String> list = new ArrayList<>();
            for (int j = 0; j < in.size(); j++) {
                list.add("?");
            }
            return new Filter(i.getKey() + " IN (" + String.join(", ", list) + ")", new ArrayList(in));
        } else {
            throw new NullPointerException("List is empty");
        }
    }

    public static <T> Filter notIn(BaseType<T> i, List<T> in) {
        if (in.size() > 0) {
            List<String> list = new ArrayList<>();
            for (int j = 0; j < in.size(); j++) {
                list.add("?");
            }
            return new Filter(i.getKey() + " NOT IN (" + String.join(", ", list) + ")", new ArrayList(in));
        } else {
            throw new NullPointerException("List is empty");
        }
    }

    public String getQueryString() {
        return queryString;
    }

    public List getValues() {
        return values;
    }

}
