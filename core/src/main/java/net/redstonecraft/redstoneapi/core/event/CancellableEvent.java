package net.redstonecraft.redstoneapi.core.event;

public interface CancellableEvent extends Event {

    public void setCancelled(boolean cancelled);

    public boolean isCancelled();

}
