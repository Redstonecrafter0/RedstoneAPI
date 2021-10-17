package net.redstonecraft.redstoneapi.db.paradigms.relational;

import net.redstonecraft.redstoneapi.db.Database;
import net.redstonecraft.redstoneapi.db.paradigms.Paradigm;

public abstract class RelationalDB extends Database {

    @Override
    public final Paradigm getParadigm() {
        return Paradigm.RELATIONAL;
    }

}
