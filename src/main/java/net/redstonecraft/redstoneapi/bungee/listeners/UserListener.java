package net.redstonecraft.redstoneapi.bungee.listeners;

import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.redstonecraft.redstoneapi.bungee.manager.UserManager;

public class UserListener implements Listener {

    private final UserManager userManager;

    public UserListener(UserManager userManager) {
        this.userManager = userManager;
    }

    @EventHandler
    public void onJoin(LoginEvent event) {
        userManager.updateUser(event.getConnection().getUniqueId(), event.getConnection().getName());
    }

}
