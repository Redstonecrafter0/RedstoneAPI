package net.redstonecraft.redstoneapi.db;

import net.redstonecraft.redstoneapi.db.paradigms.Paradigm;
import net.redstonecraft.redstoneapi.db.paradigms.document.DocumentDB;
import net.redstonecraft.redstoneapi.db.paradigms.graph.GraphDB;
import net.redstonecraft.redstoneapi.db.paradigms.keyvalue.KeyValueDB;
import net.redstonecraft.redstoneapi.db.paradigms.relational.RelationalDB;
import net.redstonecraft.redstoneapi.db.paradigms.widecolumn.WideColumnDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {

    private final Map<Paradigm, List<Database>> databases = new HashMap<>();

    public void addDatabase(Database database) {
        if (!databases.containsKey(database.getParadigm())) {
            databases.put(database.getParadigm(), new ArrayList<>());
        }
        databases.get(database.getParadigm()).add(database);
    }

    public Database getDatabase(Paradigm paradigm) {
        List<Database> available = databases.get(paradigm);
        if (available != null) {
            return available.get(0);
        } else {
            Paradigm nextBest = paradigm.getNextBest(databases.keySet());
            available = databases.get(nextBest);
            if (available != null) {
                return available.get(0);
            } else {
                return null;
            }
        }
    }

    public KeyValueDB getKeyValueDatabase() {
        return (KeyValueDB) getDatabase(Paradigm.KEY_VALUE);
    }

    public WideColumnDB getWideColumnDatabase() {
        return (WideColumnDB) getDatabase(Paradigm.WIDE_COLUMN);
    }

    public DocumentDB getDocumentdatabase() {
        return (DocumentDB) getDatabase(Paradigm.DOCUMENT);
    }

    public RelationalDB getRelationalDatabase() {
        return (RelationalDB) getDatabase(Paradigm.RELATIONAL);
    }

    public GraphDB getGraphDatabase() {
        return (GraphDB) getDatabase(Paradigm.GRAPH);
    }

}
