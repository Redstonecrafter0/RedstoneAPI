package net.redstonecraft.redstoneapi.webserver.ext.login;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public interface UserProvider<T extends User> {

    User login(String username, String password);

    T getUserFromUserId(String uid);

}
