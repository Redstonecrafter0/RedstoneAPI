package net.redstonecraft.redstoneapi.data.csv;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Redstonecrafter0
 * @since 1.4
 */
public abstract class AbstractCSV<T> implements Iterable<T> {

    protected final List<List<String>> data = new ArrayList<>();
    protected final char delimiter;

    public AbstractCSV(char delimiter) {
        this.delimiter = delimiter;
    }

    public void read(File file) throws IOException, InvalidFileFormatException {
        FileReader reader = new FileReader(file);
        StringBuilder sb = new StringBuilder();
        int a;
        while ((a = reader.read()) != -1) {
            sb.append((char) a);
        }
        read(sb.toString());
    }

    public void read(String string) throws InvalidFileFormatException {
        data.clear();
        String d = String.valueOf(delimiter);
        for (String i : string.split("\n")) {
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
            data.add(rowBuffer);
        }
    }

    public String getDataString() {
        return data.stream().map(this::renderRow).collect(Collectors.joining("\n"));
    }

    protected String renderRow(List<String> collection) {
        return collection.stream().map(this::escapeString).collect(Collectors.joining(String.valueOf(delimiter)));
    }

    protected String escapeString(String string) {
        return string.contains(String.valueOf(delimiter)) || string.contains("\"") || string.startsWith(" ") || string.endsWith(" ") ? "\"" + string.replace("\"", "\"\"") + "\"" : string.replace("\"", "\"\"");
    }

    public void save(File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(getDataString());
        writer.flush();
        writer.close();
    }

    public abstract Stream<T> stream();

    public static class InvalidFormatException extends Exception {
    }

    public static class InvalidFileFormatException extends InvalidFormatException {
    }

}
