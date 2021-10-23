package net.redstonecraft.redstoneapi.webserver.ext.pubsub;

import net.redstonecraft.redstoneapi.core.http.HttpResponseCode;
import net.redstonecraft.redstoneapi.core.tools.PubSub;
import net.redstonecraft.redstoneapi.core.utils.NumberUtils;
import net.redstonecraft.redstoneapi.core.tuple.Pair;
import net.redstonecraft.redstoneapi.webserver.HttpMethod;
import net.redstonecraft.redstoneapi.webserver.RequestHandler;
import net.redstonecraft.redstoneapi.webserver.WebRequest;
import net.redstonecraft.redstoneapi.webserver.annotations.QueryParam;
import net.redstonecraft.redstoneapi.webserver.annotations.Route;
import net.redstonecraft.redstoneapi.webserver.annotations.RouteParam;
import net.redstonecraft.redstoneapi.webserver.annotations.methods.Get;
import net.redstonecraft.redstoneapi.webserver.annotations.methods.Post;
import net.redstonecraft.redstoneapi.webserver.obj.WebResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * RedstoneAPI
 *
 * @see PubSub to subscribe/unsubscribe
 * @author Redstonecrafter0
 */
@SuppressWarnings("unused")
public class PubSubCallback extends RequestHandler {

    @SuppressWarnings("SpellCheckingInspection")
    private static final char[] chars = "qwertzuiopasdfghjklyxcvbnmQWERTZUIOPASDFGHJKLYXCVBNM1234567890".toCharArray();

    private final BiPredicate<String, Pair<Boolean, Long>> saver;
    private final Predicate<String> reader;
    private final BiConsumer<String, String> callback;
    private final Map<String, Long> awaiting = new ConcurrentHashMap<>();

    /**
     * @param saver saves ids of webhooks to listen for supplying the id and a pair of
     *              <ol>
     *              <li>a boolean that indicates whether to add the if or remove the id and</li>
     *              <li>the amount of seconds until the hub does not send any more webhooks or null if not given</li>
     *              </ol>
     * @param reader returns a boolean whether the supplied id is registered
     * @param callback is called to process the webhook supplying
     *                 <ol>
     *                 <li>the id to identify the webhook and</li>
     *                 <li>the content of the webhook</li>
     *                 </ol>
     */
    public PubSubCallback(BiPredicate<String, Pair<Boolean, Long>> saver, Predicate<String> reader, BiConsumer<String, String> callback) {
        this.saver = saver;
        this.reader = reader;
        this.callback = callback;
    }

    public String register(long timeout) {
        clean();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append(NumberUtils.secureRandom(0, chars.length - 1));
        }
        String id = sb.toString();
        awaiting.put(id, System.currentTimeMillis() + timeout);
        return id;
    }

    @Get
    @Post
    @Route("/pubsub/webhook/<id>")
    public WebResponse callback(WebRequest request,
                                @RouteParam("id") String id,
                                @QueryParam("hub.mode") String mode,
                                @QueryParam("hub.topic") String topic,
                                @QueryParam("hub.challenge") String challenge,
                                @QueryParam("hub.lease_seconds") String leaseSeconds) {
        clean();
        if (mode == null && topic == null && challenge == null && leaseSeconds == null && request.getMethod().equals(HttpMethod.POST)) {
            if (reader.test(id)) {
                callback.accept(id, request.getContentAsString());
                return WebResponse.create().setResponseCode(HttpResponseCode.NO_CONTENT).build();
            } else {
                return WebResponse.create().setResponseCode(HttpResponseCode.NOT_FOUND).build();
            }
        } else if (mode != null && (mode.equals("subscribe") || mode.equals("unsubscribe")) &&  topic != null && challenge != null && request.getMethod().equals(HttpMethod.GET) && awaiting.containsKey(id)) {
            if (saver.test(id, new Pair<>(mode.equals("subscribe"), NumberUtils.toLong(leaseSeconds)))) {
                awaiting.remove(id);
                return WebResponse.create().setContent(challenge).build();
            } else {
                return WebResponse.create().setResponseCode(HttpResponseCode.NOT_FOUND).build();
            }
        } else {
            return WebResponse.create().setResponseCode(HttpResponseCode.NOT_FOUND).build();
        }
    }

    private void clean() {
        for (Map.Entry<String, Long> i : awaiting.entrySet()) {
            if (i.getValue() < System.currentTimeMillis()) {
                awaiting.remove(i.getKey());
            }
        }
    }

}
