package net.redstonecraft.redstoneapi.tools;

/**
 * @author Redstonecrafter0
 * @since 1.4
 */
public class Triplet<F, S, T> {

    private final F f;
    private final S s;
    private final T t;

    public Triplet(F f, S s, T t) {
        this.f = f;
        this.s = s;
        this.t = t;
    }

    public F getFirst() {
        return f;
    }

    public S getSecond() {
        return s;
    }

    public T getThird() {
        return t;
    }

    @Override
    public String toString() {
        return "Triplet{" +
                "first=" + f +
                ", second=" + s +
                ", third=" + t +
                '}';
    }

}
