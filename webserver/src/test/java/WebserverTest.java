import net.redstonecraft.redstoneapi.core.HttpHeader;
import net.redstonecraft.redstoneapi.core.Pair;
import net.redstonecraft.redstoneapi.webserver.HttpMethod;
import net.redstonecraft.redstoneapi.webserver.RequestHandler;
import net.redstonecraft.redstoneapi.webserver.annotations.FormParam;
import net.redstonecraft.redstoneapi.webserver.annotations.QueryParam;
import net.redstonecraft.redstoneapi.webserver.annotations.Route;
import net.redstonecraft.redstoneapi.webserver.WebServer;
import net.redstonecraft.redstoneapi.webserver.annotations.RouteParam;
import net.redstonecraft.redstoneapi.webserver.annotations.methods.Get;
import net.redstonecraft.redstoneapi.webserver.annotations.methods.Post;
import net.redstonecraft.redstoneapi.webserver.ext.forms.FormValidator;
import net.redstonecraft.redstoneapi.webserver.ext.login.LoginManager;
import net.redstonecraft.redstoneapi.webserver.WebRequest;
import net.redstonecraft.redstoneapi.webserver.obj.WebResponse;
import net.redstonecraft.redstoneapi.webserver.ws.WebsocketEvent;
import net.redstonecraft.redstoneapi.webserver.ws.events.WebsocketConnectedEvent;
import net.redstonecraft.redstoneapi.webserver.ws.events.WebsocketDisconnectedEvent;
import net.redstonecraft.redstoneapi.webserver.ws.events.WebsocketMessageEvent;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
@SuppressWarnings("deprecation")
public class WebserverTest extends RequestHandler {

    private final LoginManager<UserImpl> loginManager = new LoginManager<>("apsuihdpaiuhsdpsaiuhdpahisdpaoihd", new UserProviderImpl(), null, false);
    private final FormValidator formValidator = new FormValidator("oadjdoasihdpoaihsdpaasiosdpiaoshdaosihd", false);

    public static void main(String[] args) throws Throwable {
        WebServer webServer = new WebServer();
        webServer.addHandler(new WebserverTest());
    }

    @Route("/")
    public String root(WebRequest request) {
        return "root";
    }

    @Get
    @Post
    @Route("/home")
    @Route("/test")
    public String home(WebRequest request, @QueryParam("name") String name, @FormParam("par") String form) {
        return name + form;
    }

    @Route("/test2/<param>")
    public String test2(WebRequest request, @RouteParam("param") String param) {
        return param;
    }

    @Get
    @Post
    @Route("/login")
    public Object login(WebRequest request, @FormParam("username") String username, @FormParam("password") String password, @FormParam("csrf") String csrf) {
        try {
            Map<String, String> data = new HashMap<>();
            if (request.getMethod().equals(HttpMethod.POST)) {
                if (formValidator.isValid(request, csrf, 60000)) {
                    WebResponse.Builder response = redirect("/userdata");
                    loginManager.loginUser(username, password, new Date(System.currentTimeMillis() + 120000000), response);
                    return response;
                } else {
                    data.put("msg", "Timeout");
                }
            }
            Pair<HttpHeader, String> tokens = formValidator.generate();
            data.put("csrf", tokens.second());
            return renderTemplate("login.html.j2", data).addHeader(tokens.first()).addHeader(new HttpHeader("test", "lol"));
        } catch (Throwable e) {
            e.printStackTrace();
            return e;
        }
    }

    @Route("/userdata")
    public Object userdata(WebRequest request) {
        try {
            return loginManager.getUserWithRefreshToken(request) == null ? "null" : loginManager.getUserWithRefreshToken(request).getId();
        } catch (Throwable e) {
            e.printStackTrace();
            return e;
        }
    }

    @WebsocketEvent("/ws")
    public void onConnect(WebsocketConnectedEvent event) {
        System.out.println(event.getWebSocketConnection().getChannel().socket().getInetAddress().getHostAddress() + " connected.");
    }

    @WebsocketEvent("/ws")
    public void onMessage(WebsocketMessageEvent event) {
        System.out.println(event.getMessage());
        try {
            event.getWebSocketConnection().send(event.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @WebsocketEvent("/ws")
    public void onDisconnect(WebsocketDisconnectedEvent event) {
        System.out.println(event.getWebSocketConnection().getChannel().socket().getInetAddress().getHostAddress() + " disconnected.");
    }

}
