package net.redstonecraft.redstoneapi.tools;

/**
 * @author Redstonecrafter0
 * @since 1.4
 */
public class Pair<F, S> {

    private final F f;
    private final S s;

    public Pair(F f, S s) {
        this.f = f;
        this.s = s;
    }

    public F getFirst() {
        return f;
    }

    public S getSecond() {
        return s;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + f +
                ", second=" + s +
                '}';
    }

}
