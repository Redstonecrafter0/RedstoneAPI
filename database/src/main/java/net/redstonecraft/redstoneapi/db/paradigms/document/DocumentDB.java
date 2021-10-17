package net.redstonecraft.redstoneapi.db.paradigms.document;

import net.redstonecraft.redstoneapi.db.Database;
import net.redstonecraft.redstoneapi.db.paradigms.Paradigm;

public abstract class DocumentDB extends Database {

    @Override
    public final Paradigm getParadigm() {
        return Paradigm.DOCUMENT;
    }

}
