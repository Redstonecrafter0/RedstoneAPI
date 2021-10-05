import net.redstonecraft.redstoneapi.webserver.ext.login.User;
import net.redstonecraft.redstoneapi.webserver.ext.login.UserProvider;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class UserProviderImpl implements UserProvider<UserImpl> {

    @Override
    public User login(String username, String password) {
        return null;
    }

    @Override
    public UserImpl getUserFromUserId(String uid) {
        return null;
    }

}
