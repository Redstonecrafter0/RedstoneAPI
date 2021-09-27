package net.redstonecraft.redstoneapi.core.event;

public interface CancellableEvent {

    public void setCancelled(boolean cancelled);

    public boolean isCancelled();

}
