package net.redstonecraft.redstoneapi.core.tools;

import net.redstonecraft.redstoneapi.core.http.HttpHeader;
import net.redstonecraft.redstoneapi.core.http.HttpRequest;
import net.redstonecraft.redstoneapi.core.http.HttpResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * A class for the <a href="https://pubsubhubbub.github.io/PubSubHubbub/pubsubhubbub-core-0.4.html">PubSub Spec</a>
 * to only subscribe or unsubscribe
 *
 * @author Redstonecrafter0
 * @since 2.0
 */
@SuppressWarnings("unused")
public class PubSub {

    private final String url;

    /**
     * Initializes class on the hub from Google https://pubsubhubbub.appspot.com/subscribe
     */
    public PubSub() {
        this("https://pubsubhubbub.appspot.com/subscribe");
    }

    /**
     * Initializes this class on a custom hub
     *
     * @param url of the hub
     */
    public PubSub(String url) {
        this.url = url;
    }

    /**
     * Subscribe to the hub
     *
     * @param callback the url where to send the data
     * @param topic to subscribe
     * @param leaseSeconds amount of seconds wanted to subscribe or null to use the hubs default
     * @return false if something went wrong
     */
    public boolean subscribe(String callback, String topic, Long leaseSeconds) {
        return request(callback, topic, leaseSeconds, "subscribe");
    }

    /**
     * Unsubscribe from the hub
     *
     * @param callback the url where the data is sent to
     * @param topic to unsubscribe
     * @return false if something went wrong
     */
    public boolean unsubscribe(String callback, String topic) {
        return request(callback, topic, null, "unsubscribe");
    }

    private boolean request(String callback, String topic, Long leaseSeconds, String mode) {
        StringBuilder sb = new StringBuilder("hub.callback=" + URLEncoder.encode(callback, StandardCharsets.UTF_8) +
                "&hub.mode=" + mode + "&hub.topic=" + URLEncoder.encode(topic, StandardCharsets.UTF_8));
        if (leaseSeconds != null) {
            sb.append("&hub.lease_seconds=").append(leaseSeconds);
        }
        try {
            HttpResponse response = HttpRequest.post(url, sb.toString().getBytes(StandardCharsets.UTF_8), new HttpHeader("Content-Type", "application/x-www-form-urlencoded"));
            return response.responseCode() / 100 == 2;
        } catch (IOException ignored) {
            return false;
        }
    }

}
