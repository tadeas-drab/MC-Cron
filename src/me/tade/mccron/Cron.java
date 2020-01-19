package me.tade.mccron;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import me.tade.mccron.commands.CronCommand;
import me.tade.mccron.commands.TimerCommand;
import me.tade.mccron.job.CronJob;
import me.tade.mccron.job.EventJob;
import me.tade.mccron.managers.EventManager;
import me.tade.mccron.utils.EventType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author The_TadeSK
 */
public class Cron extends JavaPlugin {

    private HashMap<String, CronJob> jobs = new HashMap<>();
    private HashMap<EventType, List<EventJob>> eventJobs = new HashMap<>();
    private List<String> startUpCommands = new ArrayList<>();
    private PluginUpdater pluginUpdater;
                                            
    @Override
    public void onEnable(){
        log("Loading plugin...");
        log("Loading config...");
        this.saveDefaultConfig();
        
        log("Loading commands...");
        getCommand("timer").setExecutor(new TimerCommand(this));
        getCommand("mccron").setExecutor(new CronCommand(this));
        
        loadJobs();

        log("Loading managers...");
        new EventManager(this);
        
        log("Loading metrics...");
        Metrics metrics = new Metrics(this);
        
        log("Loading custom charts for metrics...");
        metrics.addCustomChart(new Metrics.SingleLineChart("running_jobs") {
            @Override
            public int getValue() {
                return jobs.size();
            }
        });

        metrics.addCustomChart(new Metrics.SingleLineChart("running_event_jobs") {
            @Override
            public int getValue() {
                int size = 0;
                for(EventType type : EventType.values())
                    if(getEventJobs().containsKey(type))
                        size += getEventJobs().get(type).size();
                return size;
            }
        });

        metrics.addCustomChart(new Metrics.SingleLineChart("running_startup_commands") {
            @Override
            public int getValue() {
                return getStartUpCommands().size();
            }
        });
        log("Everything loaded!");

        pluginUpdater = new PluginUpdater(this);

        new BukkitRunnable(){
            @Override
            public void run() {
                log("Dispatching startup commands..");
                for(String commands : getStartUpCommands()){
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commands);
                }
                log("Commands dispatched!");
            }
        }.runTaskLater(this, 20);
    }
    
    public void loadJobs(){
        log("Loading cron jobs....");
        for(String s : getConfig().getConfigurationSection("jobs").getKeys(false)){
            List<String> cmds = getConfig().getStringList("jobs." + s + ".commands");
            String time = getConfig().getString("jobs." + s + ".time");
            
            jobs.put(s, new CronJob(this, cmds, time, s));
            log("Created new job: " + s);
        }
        log("Total loaded jobs: " + jobs.size());
        log("Starting cron jobs...");
        for(CronJob j : new ArrayList<>(jobs.values())){
            try{
                log("Starting job: " + j.getName());
                j.startJob();
            }catch(IllegalArgumentException ex){
                log("Can't start job " + j.getName() + "! " + ex.getMessage());
            }
        }
        log("All jobs started!");

        for(String s : getConfig().getConfigurationSection("event-jobs").getKeys(false)){
            EventType type = EventType.isEventJob(s);
            if(type != null){
                List<EventJob> jobs = new ArrayList<>();
                for(String name : getConfig().getConfigurationSection("event-jobs." + s).getKeys(false)){
                    int time = getConfig().getInt("event-jobs." + s + "." + name + ".time");
                    List<String> cmds = getConfig().getStringList("event-jobs." + s + "." + name + ".commands");
                    jobs.add(new EventJob(this, name, time, cmds, type));
                    log("Created new event job: " + name + " (" + type.getConfigName() + ")");
                }

                eventJobs.put(type, jobs);
            }
        }
        log("All event jobs registered!");

        List<String> cmds = getConfig().getStringList("startup.commands");
        if(cmds != null) {
            for (String command : cmds) {
                startUpCommands.add(command);
                log("Created new startup command: " + command);
            }
        }
        log("All startup commands registered!");
    }

    public void sendUpdateMessage() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.isOp() || player.hasPermission("cron.update,info")) {
                        player.sendMessage(" ");
                        player.sendMessage("§a§lMC-Cron §6A new update has come! Released on §a" + pluginUpdater.getUpdateInfo()[1]);
                        player.sendMessage("§a§lMC-Cron §6New version number/your current version §a" + pluginUpdater.getUpdateInfo()[0] + "§7/§c" + getDescription().getVersion());
                        player.sendMessage("§a§lEfiMine §6Download update here: §ahttps://www.spigotmc.org/resources/37632/");
                    }
                }
            }
        }.runTaskLater(this, 30 * 20);
    }

    @Override
    public void onDisable(){
        //reloadConfig();
        //saveConfig();
    }
    
    public void log(String info){
        getLogger().info(info);
        logCustom(info);
    }
    
    private void logCustom(String info){
        try {
            File dataFolder = getDataFolder();
            if(!dataFolder.exists()){
                dataFolder.mkdir();
            }
            
            File saveTo = new File(getDataFolder(), "log.txt");
            if (!saveTo.exists()){
                saveTo.createNewFile();
            }
            
            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);
            
            pw.println("[" + new SimpleDateFormat("dd.MM.YYYY HH:mm:ss").format(new Date()) + "] "+ info);
            pw.flush();
            pw.close();
            
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public HashMap<String, CronJob> getJobs() {
        return jobs;
    }

    public HashMap<EventType, List<EventJob>> getEventJobs() {
        return eventJobs;
    }

    public List<String> getStartUpCommands() {
        return startUpCommands;
    }
}
