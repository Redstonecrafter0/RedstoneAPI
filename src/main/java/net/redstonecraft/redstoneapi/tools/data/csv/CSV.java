package net.redstonecraft.redstoneapi.tools.data.csv;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class is used to parse and serialize CSV
 *
 * @author Redstonecrafter0
 * @since 1.4
 */
public class CSV extends AbstractCSV<List<String>> {

    public CSV() {
        this(',');
    }

    public CSV(char delimiter) {
        super(delimiter);
    }

    public void addRow(List collection) {
        data.add((List<String>) collection.stream().map(i -> i.toString().replace("\n", "")).collect(Collectors.toList()));
    }

    public void addRow(Object... objects) {
        data.add(Arrays.stream(objects).map(i -> i.toString().replace("\n", "")).collect(Collectors.toList()));
    }

    @Override
    public Stream<List<String>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public Iterator<List<String>> iterator() {
        return data.iterator();
    }

    @Override
    public void forEach(Consumer<? super List<String>> action) {
        data.forEach(action);
    }

    @Override
    public Spliterator<List<String>> spliterator() {
        return data.spliterator();
    }

}
