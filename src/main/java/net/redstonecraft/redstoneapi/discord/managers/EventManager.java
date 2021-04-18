package net.redstonecraft.redstoneapi.discord.managers;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.redstonecraft.redstoneapi.discord.abs.DiscordEvent;
import net.redstonecraft.redstoneapi.discord.abs.DiscordEventListener;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class EventManager implements EventListener {

    private final HashMap<Class, List<ListenerBundle>> listeners = new HashMap<>();

    public void addEventListener(DiscordEventListener listener) {
        for (Method i : listener.getClass().getMethods()) {
            if (i.isAnnotationPresent(DiscordEvent.class) && !Modifier.isStatic(i.getModifiers()) && i.getParameterTypes().length == 1) {
                if (!listeners.containsKey(i.getParameterTypes()[0])) {
                    listeners.put(i.getParameterTypes()[0], new ArrayList<>());
                }
                i.setAccessible(true);
                listeners.get(i.getParameterTypes()[0]).add(new ListenerBundle(i, listener));
            }
        }
    }

    @Override
    public void onEvent(GenericEvent genericEvent) {
        List<ListenerBundle> bundle = new ArrayList<>();
        for (Map.Entry<Class, List<ListenerBundle>> i : listeners.entrySet()) {
            if (genericEvent.getClass().equals(i.getKey()) || i.getKey().isAssignableFrom(genericEvent.getClass())) {
                bundle.addAll(i.getValue());
            }
        }
        for (ListenerBundle i : bundle) {
            try {
                i.method.invoke(i.listener, genericEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class ListenerBundle {

        private final Method method;
        private final DiscordEventListener listener;

        private ListenerBundle(Method method, DiscordEventListener listener) {
            this.method = method;
            this.listener = listener;
        }

    }
}
