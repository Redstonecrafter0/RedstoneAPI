import net.redstonecraft.redstoneapi.webserver.RequestHandler;
import net.redstonecraft.redstoneapi.webserver.annotations.QueryParam;
import net.redstonecraft.redstoneapi.webserver.annotations.Route;
import net.redstonecraft.redstoneapi.webserver.WebServer;
import net.redstonecraft.redstoneapi.webserver.obj.WebResponse;
import net.redstonecraft.redstoneapi.webserver.WebRequest;

import static net.redstonecraft.redstoneapi.webserver.HttpMethod.GET;
import static net.redstonecraft.redstoneapi.webserver.HttpMethod.POST;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class WebserverTest extends RequestHandler {

    public static void main(String[] args) throws Throwable {
        WebServer webServer = new WebServer();
        webServer.addHandler(new WebserverTest());
    }

    @Route(value = "/", methods = {GET, POST})
    public WebResponse root(WebRequest request) {
        return new WebResponse("");
    }

    @Route("/home")
    public WebResponse home(WebRequest request, @QueryParam("name") String name) {
        return new WebResponse("");
    }

}
