package net.redstonecraft.redstoneapi.tools.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * A complete eventmanager
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class EventManager {

    private final Map<Class<? extends Event>, List<HandlerBundle>> registry = new HashMap<>();

    public void registerEventListener(EventHandler handler) {
        List<Class<? extends Event>> toSort = new ArrayList<>();
        for (Method i : handler.getClass().getMethods()) {
            if (i.isAnnotationPresent(EventListener.class) && !Modifier.isStatic(i.getModifiers()) && i.getReturnType().equals(void.class) && i.getParameterTypes().length == 1 && (Event.class.isAssignableFrom(i.getParameterTypes()[0]) || Event.class.equals(i.getParameterTypes()[0]))) {
                if (!registry.containsKey((Class<? extends Event>) i.getParameterTypes()[0])) {
                    registry.put((Class<? extends Event>) i.getParameterTypes()[0], new ArrayList<>());
                }
                i.setAccessible(true);
                registry.get((Class<? extends Event>) i.getParameterTypes()[0]).add(new HandlerBundle(i, handler, i.getAnnotation(EventListener.class).priority()));
                toSort.add((Class<? extends Event>) i.getParameterTypes()[0]);
            }
        }
        for (Class<? extends Event> i : toSort) {
            registry.get(i).sort(Comparator.comparingInt(bundle -> bundle.priority));
        }
    }
    
    public void unregisterEventListener(EventHandler handler) {
        registry.forEach((eventType, handlerBundleList) -> handlerBundleList.removeIf(handlerBundle -> handlerBundle.eventHandler.equals(handler)));
        List<Class<? extends Event>> tmp = new ArrayList<>();
        registry.entrySet().stream().filter(e -> e.getValue().size() == 0).forEach(e -> tmp.add(e.getKey()));
        tmp.forEach(registry::remove);
    }

    public void fireEvent(Event event) {
        List<HandlerBundle> handlers = registry.get(event.getClass());
        if (handlers != null) {
            if (CancellableEvent.class.isAssignableFrom(event.getClass())) {
                for (HandlerBundle i : handlers) {
                    try {
                        i.method.invoke(i.eventHandler, event);
                        if (((CancellableEvent) event).isCancelled()) {
                            break;
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                handlers.forEach(handlerBundle -> {
                    try {
                        handlerBundle.method.invoke(handlerBundle.eventHandler, event);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private static class HandlerBundle {

        private final Method method;
        private final EventHandler eventHandler;
        private final int priority;

        private HandlerBundle(Method method, EventHandler eventHandler, int priority) {
            this.method = method;
            this.eventHandler = eventHandler;
            this.priority = priority;
        }

    }

}
