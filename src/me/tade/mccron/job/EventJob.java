package me.tade.mccron.job;

import me.tade.mccron.Cron;
import me.tade.mccron.utils.EventType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class EventJob {

    private Cron cron;
    private String name;
    private int time;
    private List<String> commands;
    private EventType eventType;

    public EventJob(Cron cron, String name, int time, List<String> commands, EventType eventType) {
        this.cron = cron;
        this.name = name;
        this.time = time;
        this.commands = commands;
        this.eventType = eventType;
    }

    public void performJob(Player player){
        new BukkitRunnable(){
            @Override
            public void run() {
                if(eventType == EventType.JOIN_EVENT && !player.isOnline())
                    return;

                for(String command : commands){
                    command = command.replace("{player}", player.getName());

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
        }.runTaskLater(cron, time * 20);
    }

    public String getName() {
        return name;
    }

    public EventType getEventType() {
        return eventType;
    }
}
