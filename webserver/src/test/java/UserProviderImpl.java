import net.redstonecraft.redstoneapi.webserver.ext.login.User;
import net.redstonecraft.redstoneapi.webserver.ext.login.UserProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class UserProviderImpl implements UserProvider<UserImpl> {

    private final Map<String, UserImpl> list = new HashMap<>();

    @Override
    public User login(String username, String password) {
        UserImpl a = new UserImpl(username);
        list.put(username, a);
        return a;
    }

    @Override
    public UserImpl getUserFromUserId(String uid) {
        return list.get(uid);
    }

}
