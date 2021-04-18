package net.redstonecraft.redstoneapi.tools.event;

public interface CancellableEvent {

    public void setCancelled(boolean cancelled);

    public boolean isCancelled();

}
