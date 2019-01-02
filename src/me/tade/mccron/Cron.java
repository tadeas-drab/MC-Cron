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
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author The_TadeSK
 */
public class Cron extends JavaPlugin {

    private HashMap<String, CronJob> jobs = new HashMap<>();
    private HashMap<EventType, List<EventJob>> eventJobs = new HashMap<>();
                                            
    @Override
    public void onEnable(){
        log("Loading plugin...");
        log("Loading config...");
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        log("Loading commands...");
        getCommand("timer").setExecutor(new TimerCommand(this));
        getCommand("mccron").setExecutor(new CronCommand(this));
        
        loadJobs();

        log("Loading managers...");
        new EventManager(this);
        
        log("Loading metrics...");
        Metrics metrics = new Metrics(this);
        
        log("Loading chstom chart for metrics...");
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
        log("Everything loaded!");
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
    }

    @Override
    public void onDisable(){
        reloadConfig();
        saveConfig();
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
}
