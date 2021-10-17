package net.redstonecraft.redstoneapi.db.paradigms.graph;

import net.redstonecraft.redstoneapi.db.Database;
import net.redstonecraft.redstoneapi.db.paradigms.Paradigm;

public abstract class GraphDB extends Database {

    @Override
    public final Paradigm getParadigm() {
        return Paradigm.GRAPH;
    }

}
