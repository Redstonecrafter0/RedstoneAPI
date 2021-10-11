package net.redstonecraft.redstoneapi.core.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * An annotated EventManager with {@link CancellableEvent}s and priorities.
 * If an {@link CancellableEvent} was cancelled no more {@link EventHandler}s are called.
 * If an {@link Event}s class is been extended further, then the event class itself and all subclasses apply to the {@link EventHandler}.
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
@SuppressWarnings("unused")
public class EventManager {

    private final Map<Class<? extends Event>, List<HandlerBundle>> registry = new HashMap<>();

    /**
     * Registers all the methods of an {@link EventListener}
     * annotated by {@link EventHandler} that have {@link Event}
     * as its single parameter or a subclass of it, are public and returning void.
     *
     * @param listener The {@link EventListener} to register.
     */
    public void registerEventListener(EventListener listener) {
        List<Class<? extends Event>> toSort = new ArrayList<>();
        for (Method i : listener.getClass().getMethods()) {
            if (i.isAnnotationPresent(EventHandler.class) && !Modifier.isStatic(i.getModifiers()) && i.getReturnType().equals(void.class) && i.getParameterTypes().length == 1 && (Event.class.isAssignableFrom(i.getParameterTypes()[0]) || Event.class.equals(i.getParameterTypes()[0]))) {
                if (!registry.containsKey((Class<? extends Event>) i.getParameterTypes()[0])) {
                    registry.put((Class<? extends Event>) i.getParameterTypes()[0], new ArrayList<>());
                }
                registry.get((Class<? extends Event>) i.getParameterTypes()[0]).add(new HandlerBundle(i, listener, i.getAnnotation(EventHandler.class).priority()));
                toSort.add((Class<? extends Event>) i.getParameterTypes()[0]);
            }
        }
        for (Class<? extends Event> i : toSort) {
            registry.get(i).sort(Comparator.comparingInt(HandlerBundle::priority));
        }
    }

    /**
     * Undo {@link #registerEventListener(EventListener)} so the resources get free.
     *
     * @param listener The {@link EventListener} to unregister.
     */
    public void unregisterEventListener(EventListener listener) {
        registry.forEach((eventType, handlerBundleList) -> handlerBundleList.removeIf(handlerBundle -> handlerBundle.eventListener.equals(listener)));
        List<Class<? extends Event>> tmp = new ArrayList<>();
        registry.entrySet().stream().filter(e -> e.getValue().size() == 0).forEach(e -> tmp.add(e.getKey()));
        tmp.forEach(registry::remove);
    }

    /**
     * Fire an {@link Event} so that {@link EventHandler}s of registered {@link EventListener} receive that event.
     * This method is blocking to allow seeing if a {@link CancellableEvent}s is cancelled or not.
     * To do so call {@link CancellableEvent#isCancelled()} on the instance used as the parameter.
     *
     * @param event the event to fire
     */
    public void fireEvent(Event event) {
        List<HandlerBundle> handlers = new LinkedList<>();
        for (Map.Entry<Class<? extends Event>, List<HandlerBundle>> i : registry.entrySet()) {
            if (i.getKey().equals(event.getClass()) || i.getKey().isAssignableFrom(event.getClass())) {
                handlers.addAll(i.getValue());
            }
        }
        if (handlers != null) {
            if (CancellableEvent.class.isAssignableFrom(event.getClass())) {
                for (HandlerBundle i : handlers) {
                    try {
                        i.method.invoke(i.eventListener, event);
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
                        handlerBundle.method.invoke(handlerBundle.eventListener, event);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private record HandlerBundle(Method method, EventListener eventListener, int priority) {
    }

}
