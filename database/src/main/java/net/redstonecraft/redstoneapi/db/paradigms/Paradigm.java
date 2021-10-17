package net.redstonecraft.redstoneapi.db.paradigms;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public enum Paradigm {

    KEY_VALUE(1, 2, 3, 4, 5),
    WIDE_COLUMN(2, 1, 3, 4, 5),
    DOCUMENT(3, 4, 5, 2, 1),
    RELATIONAL(4, 5, 3, 2, 1),
    GRAPH(5, 4, 3, 2, 1);

    private final int complexityLevel;
    private final int[] nextBest;
    private static final Map<Integer, Paradigm> map = new HashMap<>();

    Paradigm(int complexityLevel, int... nextBest) {
        this.complexityLevel = complexityLevel;
        this.nextBest = nextBest;
    }

    private static void registerParadigms(Paradigm... paradigms) {
        for (Paradigm i : paradigms) {
            map.put(i.getComplexityLevel(), i);
        }
    }

    static {
        registerParadigms(KEY_VALUE, WIDE_COLUMN, DOCUMENT, RELATIONAL, GRAPH);
    }

    public int getComplexityLevel() {
        return complexityLevel;
    }

    public static Paradigm getByComplexityLevel(int complexityLevel) {
        for (Paradigm i : values()) {
            if (i.getComplexityLevel() == complexityLevel) {
                return i;
            }
        }
        return null;
    }

    public Paradigm getNextBest(Paradigm... available) {
        return getNextBest(Arrays.asList(available));
    }

    public Paradigm getNextBest(Collection<Paradigm> available) {
        for (int i : nextBest) {
            Paradigm paradigm = getByComplexityLevel(i);
            if (paradigm != null && available.contains(paradigm)) {
                return paradigm;
            }
        }
        return null;
    }

}
