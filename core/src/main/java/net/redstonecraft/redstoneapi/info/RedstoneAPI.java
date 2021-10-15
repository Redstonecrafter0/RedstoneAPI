package net.redstonecraft.redstoneapi.info;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;
import com.github.lalyos.jfiglet.FigletFont;
import net.redstonecraft.redstoneapi.core.HttpHeader;
import net.redstonecraft.redstoneapi.core.HttpRequest;
import net.redstonecraft.redstoneapi.core.HttpResponse;
import net.redstonecraft.redstoneapi.core.Version;
import net.redstonecraft.redstoneapi.data.json.JSONArray;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

/**
 * A class to get information of this library like
 * if there are new updates or what's the current version.
 *
 * @author Redstonecrafter0
 * @since 1.0
 */
public final class RedstoneAPI {

    private static final Version VERSION = Version.fromVersionString("2.0");

    private static final int WIDTH = 600;
    private static final int HEIGHT = 250;

    public static void main(String[] args) {
        try {
            //noinspection SpellCheckingInspection
            if (Arrays.asList(args).contains("nogui")) {
                throw new Exception();
            }
            JFrame frame = new JFrame("RedstoneAPI " + VERSION);
            frame.setResizable(false);
            frame.setSize(WIDTH, HEIGHT);
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation(screen.width / 2 - WIDTH / 2, screen.height / 2 - HEIGHT / 2);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.getContentPane().setLayout(null);
            frame.getContentPane().setBackground(Color.BLACK);
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage(RedstoneAPI.class.getResource("/redstoneapi.png")));
            JLabel label = new JLabel("<html><h1>RedstoneAPI " + VERSION + " by Redstonecrafter0</h1></html>");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setLocation(0, (HEIGHT / 2) - 90);
            label.setSize(WIDTH, 30);
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
            label1.setLocation(0, (HEIGHT / 2) - 30);
            label1.setSize(WIDTH, 15);
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
            label2.setLocation(0, (HEIGHT / 2));
            label2.setSize(WIDTH, 15);
            label2.setForeground(Color.WHITE);
            frame.add(label2);
            JLabel label3 = new JLabel("Checking for a new version...");
            label3.setHorizontalAlignment(SwingConstants.CENTER);
            label3.setLocation(0, (HEIGHT / 2) + 30);
            label3.setSize(WIDTH, 15);
            label3.setForeground(Color.WHITE);
            frame.add(label3);
            frame.setVisible(true);
            try {
                label3.setText(getUpdate().getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                label3.setText("An error occurred while checking for a new version");
            }
        } catch (Exception ignored) {
            try {
                String[] line1 = FigletFont.convertOneLine(" RedstoneAPI " + VERSION + " ").split("\\n");
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
                    System.out.println(Ansi.colorize("An error occurred while checking for a new version", Attribute.RED_TEXT()));
                }
            } catch (Exception ignored1) {
            }
        }
    }

    /**
     * Fetches a new update
     *
     * @return information about a new version or if it's the current
     * @throws IOException if any I/O error occurs
     */
    public static Update getUpdate() throws IOException {
        HttpResponse response = HttpRequest.get("https://api.github.com/repos/Redstonecrafter0/RedstoneAPI/releases", new HttpHeader("Accept", "application/vnd.github.v3+json"));
        JSONArray arr = response.getJsonArray();
        Version version = RedstoneAPI.VERSION;
        for (int i = 0; i < arr.size(); i++) {
            try {
                if (!arr.getObject(i).getBoolean("prerelease")) {
                    Version tmp = Version.fromVersionString(arr.getObject(i).getString("tag_name"));
                    if (tmp.isNewerThan(version)) {
                        version = tmp;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return version.equals(RedstoneAPI.VERSION) ? new Update(Update.State.LATEST_VERSION, null) : new Update(Update.State.NEW_VERSION_AVAILABLE, version);
    }

    /**
     * Information of a new version of the RedstoneAPI
     *
     * @author Redstonecrafter0
     */
    public static final class Update {

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

        /**
         * @return if a new version is available
         */
        public State getState() {
            return state;
        }

        /**
         * @return the newest version of the RedstoneAPI
         */
        public Version getVersion() {
            return version;
        }

        private String getMessage() {
            return message;
        }

        private String getMessageColor() {
            return messageColor;
        }

        /**
         * The state if there is a new version or if the current version is the latest
         *
         * @author Redstonecrafter0
         */
        public enum State {
            LATEST_VERSION,
            NEW_VERSION_AVAILABLE
        }
    }

    /**
     * Get the current {@link Version} of the RedstoneAPI
     *
     * @return the version currently used
     * */
    public static Version getVersion() {
        return VERSION;
    }

}
