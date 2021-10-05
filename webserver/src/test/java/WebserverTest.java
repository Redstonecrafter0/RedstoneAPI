import net.redstonecraft.redstoneapi.webserver.RequestHandler;
import net.redstonecraft.redstoneapi.webserver.annotations.FormParam;
import net.redstonecraft.redstoneapi.webserver.annotations.QueryParam;
import net.redstonecraft.redstoneapi.webserver.annotations.Route;
import net.redstonecraft.redstoneapi.webserver.WebServer;
import net.redstonecraft.redstoneapi.webserver.annotations.methods.Get;
import net.redstonecraft.redstoneapi.webserver.annotations.methods.Post;
import net.redstonecraft.redstoneapi.webserver.ext.login.jwt.JWTLoginManager;
import net.redstonecraft.redstoneapi.webserver.WebRequest;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
@SuppressWarnings("deprecation")
public class WebserverTest extends RequestHandler {

    private final JWTLoginManager loginManager = new JWTLoginManager("apsuihdpaiuhsdpsaiuhdpahisdpaoihd");

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

}
