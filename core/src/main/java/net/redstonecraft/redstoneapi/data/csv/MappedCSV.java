package net.redstonecraft.redstoneapi.data.csv;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is used to parse and serialize keyed CSV
 *
 * @author Redstonecrafter0
 * @since 1.4
 */
public class MappedCSV extends AbstractCSV<Map.Entry<String, String>> {

    private final List<String> keys;

    public MappedCSV(Collection<String> keys) throws InvalidFormatException {
        this(',', keys);
    }

    public MappedCSV(char delimiter, Collection<String> keys) throws InvalidFormatException {
        super(delimiter);
        if (new HashSet<>(keys).size() != keys.size()) {
            throw new InvalidFormatException();
        }
        this.keys = new ArrayList<>(keys);
    }

    @Override
    public void read(String string) throws InvalidFormatException {
        String i = string.split("\n", 2)[0];
        int quotes = 0;
        StringBuilder stringBuffer = new StringBuilder();
        List<String> rowBuffer = new ArrayList<>();
        for (char j : i.toCharArray()) {
            if (j == '"') {
                if (quotes % 2 == 0 && quotes > 0) {
                    stringBuffer.append('"');
                }
                quotes++;
            } else {
                if (j == delimiter) {
                    if (quotes % 2 == 1) {
                        stringBuffer.append(delimiter);
                    } else {
                        rowBuffer.add(stringBuffer.toString());
                        stringBuffer = new StringBuilder();
                    }
                } else {
                    stringBuffer.append(j);
                }
            }
        }
        rowBuffer.add(stringBuffer.toString());
        if (rowBuffer.size() != keys.size()) {
            throw new InvalidFileFormatException();
        }
        for (int j = 0; j < rowBuffer.size(); j++) {
            if (!rowBuffer.get(j).equals(keys.get(j))) {
                throw new InvalidFileFormatException();
            }
        }
        super.read(string.split("\n", 2)[1]);
    }

    @Override
    public String getDataString() {
        List<List<String>> innerData = new ArrayList<>();
        innerData.add(keys);
        innerData.addAll(data);
        return innerData.stream().map(this::renderRow).collect(Collectors.joining("\n"));
    }

    private Map<String, String> getData() {
        Map<String, String> map = new LinkedHashMap<>();
        data.forEach(i -> {
            for (int j = 0; j < keys.size(); j++) {
                map.put(keys.get(j), j <= i.size() - 1 ? i.get(j) : null);
            }
        });
        return map;
    }

    public void addRow(Map<String, String> map) {
        List<String> list = new ArrayList<>();
        keys.forEach(i -> list.add(map.get(i)));
        data.add(list);
    }

    @Override
    public Stream<Map.Entry<String, String>> stream() {
        return getData().entrySet().stream();
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return getData().entrySet().iterator();
    }

    @Override
    public void forEach(Consumer<? super Map.Entry<String, String>> action) {
        getData().entrySet().forEach(action);
    }

    @Override
    public Spliterator<Map.Entry<String, String>> spliterator() {
        return getData().entrySet().spliterator();
    }

}
