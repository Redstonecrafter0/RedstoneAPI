package net.redstonecraft.redstoneapi.core;

/**
 * @author Redstonecrafter0
 *
 * @since 2.0
 */
public class PubSub {
    
    private final String url;

    public PubSub() {
        this("https://pubsubhubbub.appspot.com/");
    }

    public PubSub(String url) {
        this.url = url;
    }

    public void subscribe() {
    }

}
