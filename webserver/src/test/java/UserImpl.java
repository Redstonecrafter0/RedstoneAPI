import net.redstonecraft.redstoneapi.webserver.ext.login.User;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class UserImpl implements User {

    private final String id;

    public UserImpl(String username) {
        id = username;
    }

    @Override
    public String getId() {
        return id;
    }

}
