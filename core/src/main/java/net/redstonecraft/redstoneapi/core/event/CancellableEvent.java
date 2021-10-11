package net.redstonecraft.redstoneapi.core.event;

/**
 * This interface needs to be used if you want to make an {@link Event} cancellable,
 * so it stops calling the next {@link EventListener}.
 * By implementing this {@link Event} is automatically implemented.
 *
 * @author Redstonecrafter0
 */
@SuppressWarnings("unused")
public interface CancellableEvent extends Event {

    /**
     * Sets cancelled to the given value that is returned on {@link #isCancelled()}.
     *
     * @param cancelled the value the cancel state to set
     */
    void setCancelled(boolean cancelled);

    /**
     * Looks up if the event is canceled.
     *
     * @return if the event has to be cancelled
     */
    boolean isCancelled();

}
