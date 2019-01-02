package me.tade.mccron.bungee.job;

import me.tade.mccron.Cron;
import me.tade.mccron.bungee.BungeeCron;
import me.tade.mccron.utils.EventType;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BungeeEventJob {

    private BungeeCron cron;
    private String name;
    private int time;
    private List<String> commands;
    private EventType eventType;

    public BungeeEventJob(BungeeCron cron, String name, int time, List<String> commands, EventType eventType) {
        this.cron = cron;
        this.name = name;
        this.time = time;
        this.commands = commands;
        this.eventType = eventType;
    }

    public void performJob(ProxiedPlayer player){
        BungeeCord.getInstance().getScheduler().schedule(cron, new Runnable() {
            @Override
            public void run() {
                if(eventType == EventType.JOIN_EVENT && !player.isConnected())
                    return;

                for(String command : commands){
                    command = command.replace("{player}", player.getName());

                    BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(), command);
                }
            }
        }, time, TimeUnit.SECONDS);
    }

    public String getName() {
        return name;
    }

    public EventType getEventType() {
        return eventType;
    }
}
