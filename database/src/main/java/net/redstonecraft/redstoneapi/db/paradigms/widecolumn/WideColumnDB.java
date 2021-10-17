package net.redstonecraft.redstoneapi.db.paradigms.widecolumn;

import net.redstonecraft.redstoneapi.db.Database;
import net.redstonecraft.redstoneapi.db.paradigms.Paradigm;

public abstract class WideColumnDB extends Database {

    @Override
    public final Paradigm getParadigm() {
        return Paradigm.WIDE_COLUMN;
    }

}
