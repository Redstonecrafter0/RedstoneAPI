package net.redstonecraft.redstoneapi;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;
import com.github.lalyos.jfiglet.FigletFont;
import net.redstonecraft.redstoneapi.json.JSONArray;
import net.redstonecraft.redstoneapi.json.parser.JSONParser;
import net.redstonecraft.redstoneapi.tools.HttpHeader;
import net.redstonecraft.redstoneapi.tools.HttpRequest;
import net.redstonecraft.redstoneapi.tools.Version;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class RedstoneAPI {

    private static final Version version = new Version("v1.2");

    private static final int width = 600;
    private static final int height = 250;

    public static void main(String[] args) {
        try {
            if (Arrays.asList(args).contains("nogui")) {
                throw new Exception();
            }
            JFrame frame = new JFrame("RedstoneAPI " + version);
            frame.setResizable(false);
            frame.setSize(width, height);
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation(screen.width / 2 - width / 2, screen.height / 2 - height / 2);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.getContentPane().setLayout(null);
            frame.getContentPane().setBackground(Color.BLACK);
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage(RedstoneAPI.class.getResource("/redstoneapi.png")));
            JLabel label = new JLabel("<html><h1>RedstoneAPI " + version + " by Redstonecrafter0</h1></html>");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setLocation(0, (height / 2) - 90);
            label.setSize(width, 30);
            label.setForeground(Color.WHITE);
            frame.add(label);
            JLabel label1 = new JLabel("<html>Github: <a href=\"\">https://github.com/Redstonecrafter0/RedstoneAPI</a></html>");
            label1.setHorizontalAlignment(SwingConstants.CENTER);
            label1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://github.com/Redstonecrafter0/RedstoneAPI"));
                    } catch (Exception ignored) {
                    }
                }
            });
            label1.setCursor(new Cursor(Cursor.HAND_CURSOR));
            label1.setLocation(0, (height / 2) - 30);
            label1.setSize(width, 15);
            label1.setForeground(Color.WHITE);
            frame.add(label1);
            JLabel label2 = new JLabel("<html>Spigot: <a href=\"\">https://www.spigotmc.org/resources/redstoneapi.88273/</a></html>");
            label2.setHorizontalAlignment(SwingConstants.CENTER);
            label2.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://www.spigotmc.org/resources/redstoneapi.88273/"));
                    } catch (Exception ignored) {
                    }
                }
            });
            label2.setCursor(new Cursor(Cursor.HAND_CURSOR));
            label2.setLocation(0, (height / 2));
            label2.setSize(width, 15);
            label2.setForeground(Color.WHITE);
            frame.add(label2);
            JLabel label3 = new JLabel("Checking for a new version...");
            label3.setHorizontalAlignment(SwingConstants.CENTER);
            label3.setLocation(0, (height / 2) + 30);
            label3.setSize(width, 15);
            label3.setForeground(Color.WHITE);
            frame.add(label3);
            frame.setVisible(true);
            try {
                label3.setText(getUpdate().getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                label3.setText("An error occured while checking for a new version");
            }
        } catch (Exception ignored) {
            try {
                String[] line1 = FigletFont.convertOneLine(" RedstoneAPI " + version + " ").split("\\n");
                String[] line2 = FigletFont.convertOneLine(" by Redstonecrafter0 ").split("\\n");
                String[] msg = new String[line1.length + line2.length];
                System.arraycopy(line1, 0, msg, 0, line1.length);
                System.arraycopy(line2, 0, msg, line1.length, line2.length);
                for (int i = 0; i < msg.length; i++) {
                    msg[i] = Ansi.colorize(msg[i], Attribute.BLUE_TEXT(), Attribute.BLACK_BACK(), Attribute.BOLD());
                }
                System.out.println("\n");
                for (String i : msg) {
                    System.out.println(i);
                }
                System.out.println("\n");
                System.out.println(Ansi.colorize("Checking for a new version...", Attribute.GREEN_TEXT()));
                try {
                    System.out.println(Ansi.colorize(getUpdate().getMessageColor(), Attribute.RED_TEXT()));
                } catch (Exception ignored1) {
                    System.out.println(Ansi.colorize("An error occured while checking for a new version", Attribute.RED_TEXT()));
                }
            } catch (Exception ignored1) {
            }
        }
    }

    public static Update getUpdate() throws IOException {
        HttpRequest response = HttpRequest.get("https://api.github.com/repos/Redstonecrafter0/RedstoneAPI/releases", new HttpHeader("Accept", "application/vnd.github.v3+json"));
        JSONArray arr = Objects.requireNonNull(JSONParser.parseArray(new String(response.getContent(), StandardCharsets.UTF_8)));
        Version version = RedstoneAPI.version;
        for (int i = 0; i < arr.size(); i++) {
            try {
                if (!arr.getObject(i).getBoolean("prerelease")) {
                    Version tmp = new Version(arr.getObject(i).getString("tag_name"));
                    if (tmp.isNewerThan(version)) {
                        version = tmp;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return version.equals(RedstoneAPI.version) ? new Update(Update.State.LATEST_VERSION, null) : new Update(Update.State.NEW_VERSION_AVAILABLE, version);
    }

    public static class Update {

        private final State state;
        private final Version version;
        private final String message;
        private final String messageColor;

        private Update(State state, Version version) {
            this.state = state;
            this.version = version;
            message = this.state.equals(State.LATEST_VERSION) ? "This is the latest version available" : "There is a newer version " + this.version + " available";
            messageColor = this.state.equals(State.LATEST_VERSION) ? Ansi.colorize("This is the latest version available", Attribute.GREEN_TEXT()) : Ansi.colorize("There is a newer version " + this.version + " available", Attribute.RED_TEXT());
        }

        public State getState() {
            return state;
        }

        public Version getVersion() {
            return version;
        }

        private String getMessage() {
            return message;
        }

        private String getMessageColor() {
            return messageColor;
        }

        public enum State {
            LATEST_VERSION, NEW_VERSION_AVAILABLE
        }
    }

    /**
     * Get the current {@link Version} of the RedstoneAPI
     *
     * @return the version currently used
     * */
    public static Version getVersion() {
        return version;
    }

}
