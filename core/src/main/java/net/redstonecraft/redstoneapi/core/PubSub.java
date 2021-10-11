package net.redstonecraft.redstoneapi.core;

/**
 * @author Redstonecrafter0
 * @since 2.0
 */
public class PubSub {

    // TODO: https://pubsubhubbub.appspot.com/subscribe https://pubsubhubbub.github.io/PubSubHubbub/pubsubhubbub-core-0.4.html
    
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
