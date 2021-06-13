package net.redstonecraft.redstoneapi.discord.managers;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.redstonecraft.redstoneapi.discord.abs.DiscordEvent;
import net.redstonecraft.redstoneapi.discord.abs.DiscordEventListener;
import net.redstonecraft.redstoneapi.tools.Hashlib;
import net.redstonecraft.redstoneapi.tools.Pair;

import java.util.*;
import java.util.function.Consumer;

/**
 * Use this class to create {@link Button}s with callback
 *
 * @author Redstonecrafter0
 * @since 1.4
 */
public class ButtonManager implements DiscordEventListener {

    private final String randomString = String.valueOf(System.currentTimeMillis());
    private final Map<String, Pair<Long, Consumer<ButtonClickEvent>>> buttons = new LinkedHashMap<>();
    private final Timer timer = new Timer();
    private final int buttonLifeTime;

    public ButtonManager(int buttonLifetime) {
        this.buttonLifeTime = buttonLifetime;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<String> remove = new ArrayList<>();
                buttons.entrySet().stream().filter(e -> e.getValue().getFirst() < System.currentTimeMillis()).forEach(e -> remove.add(e.getKey()));
                remove.forEach(buttons::remove);
            }
        }, 0L, 1000L);
    }

    @DiscordEvent
    public void onButtonClick(ButtonClickEvent event) {
        Pair<Long, Consumer<ButtonClickEvent>> cb = buttons.get(event.getComponentId());
        if (cb != null) {
            cb.getSecond().accept(event);
            if (!event.isAcknowledged()) {
                event.deferEdit().queue();
            }
        } else {
            event.editButton(null).queue();
        }
    }

    public Button createButton(ButtonStyle buttonStyle, String label, Consumer<ButtonClickEvent> onClick) {
        return createButton(buttonStyle, label, true, onClick);
    }

    public Button createButton(ButtonStyle buttonStyle, String label, boolean active, Consumer<ButtonClickEvent> onClick) {
        return createButton(buttonStyle, label, active, buttonLifeTime, onClick);
    }

    public Button createButton(ButtonStyle buttonStyle, String label, boolean active, int overrideLifeTime, Consumer<ButtonClickEvent> onClick) {
        String id = Hashlib.md5(randomString + System.currentTimeMillis());
        if (buttonStyle.equals(ButtonStyle.UNKNOWN) || buttonStyle.equals(ButtonStyle.LINK)) {
            throw new IllegalArgumentException("Invalid ButtonStyle");
        }
        buttons.put(id, new Pair<>(System.currentTimeMillis() + (overrideLifeTime * 1000L), onClick));
        return Button.of(buttonStyle, id, label).withDisabled(!active);
    }

    public Button createButton(ButtonStyle buttonStyle, Emoji label, Consumer<ButtonClickEvent> onClick) {
        return createButton(buttonStyle, label, true, onClick);
    }

    public Button createButton(ButtonStyle buttonStyle, Emoji label, boolean active, Consumer<ButtonClickEvent> onClick) {
        return createButton(buttonStyle, label, active, buttonLifeTime, onClick);
    }

    public Button createButton(ButtonStyle buttonStyle, Emoji label, boolean active, int overrideLifeTime, Consumer<ButtonClickEvent> onClick) {
        String id = Hashlib.md5(randomString + System.currentTimeMillis());
        if (buttonStyle.equals(ButtonStyle.UNKNOWN) || buttonStyle.equals(ButtonStyle.LINK)) {
            throw new IllegalArgumentException("Invalid ButtonStyle");
        }
        buttons.put(id, new Pair<>(System.currentTimeMillis() + (overrideLifeTime * 1000L), onClick));
        return Button.of(buttonStyle, id, label).withDisabled(!active);
    }

    public Button createLinkButton(String url, String label) {
        return createLinkButton(url, label, true);
    }

    public Button createLinkButton(String url, String label, boolean active) {
        return Button.of(ButtonStyle.LINK, url, label).withDisabled(!active);
    }

    public void stop() {
        timer.cancel();
    }

}
