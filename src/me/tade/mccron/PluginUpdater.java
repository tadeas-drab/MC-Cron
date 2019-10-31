package me.tade.mccron;

import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author The_TadeSK
 */
public class PluginUpdater {

    private Cron plugin;
    private boolean needUpdate;
    private String[] updateInfo;

    public PluginUpdater(Cron plugin) {
        this.plugin = plugin;

        new BukkitRunnable() {
            public void run() {
                doUpdate();
            }
        }.runTaskTimerAsynchronously(plugin, 20, 1800 * 20);
    }

    public void doUpdate() {
        String response = getResponse();

        if (response == null) {
            System.out.println("Some sort of error happend! Can't get Web version of MC-Cron!");
            return;
        }
        updateInfo = response.split(";");
        System.out.println("Current MC-Cron version: " + plugin.getDescription().getVersion());
        System.out.println("Web MC-Cron version: " + updateInfo[0]);

        if (plugin.getDescription().getVersion().equalsIgnoreCase(updateInfo[0]))
            return;

        System.out.println(" ");
        System.out.println("MC-Cron I got new Update!");

        needUpdate = true;

        plugin.sendUpdateMessage();
    }

    public String getResponse() {
        try {
            System.out.println("Trying to get new version of MC-Cron...");
            URL post = new URL("https://raw.githubusercontent.com/TheTadeSK/MC-Cron/master/README.md");

            String result = get(post);
            return result;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String get(URL url) {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;

            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {

                sb.append(line);
                sb.append(System.lineSeparator());
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public boolean needUpdate() {
        return needUpdate;
    }

    public String[] getUpdateInfo() {
        return updateInfo;
    }
}
