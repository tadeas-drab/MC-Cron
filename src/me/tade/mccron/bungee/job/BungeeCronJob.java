package me.tade.mccron.bungee.job;

import me.tade.mccron.Cron;
import me.tade.mccron.bungee.BungeeCron;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.scheduler.BungeeTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author The_TadeSK
 */
public class BungeeCronJob {

    private BungeeCron cron;
    private List<String> cmds;
    private String time, name;
    private Calendar cal;
    private int t = 0;
    private int calDayMonth = 0;
    private int calDayWeek = 0;
    private String clockTime = "";
    private ScheduledTask task;
    
    public BungeeCronJob(BungeeCron cron, List<String> cmds, String time, String name){
        this.cron = cron;
        this.cmds = cmds;
        this.time = time;
        this.name = name;
    }
    
    public void startJob() throws IllegalArgumentException {
        getTimer();
        task = cron.getProxy().getScheduler().schedule(cron, new Runnable() {
            @Override
            public void run() {
                t--;
                if(cal != null){
                    if(t <= 0){
                        getTimer();
                        if(clockTime != "" && !isTime()){
                            return;
                        }
                        if(calDayMonth != 0 && cal.get(Calendar.DAY_OF_MONTH) == calDayMonth){
                            runCommands();
                        }else if(calDayWeek != 0 && cal.get(Calendar.DAY_OF_WEEK) == calDayWeek){
                            runCommands();
                        }
                    }
                    return;
                }
                if(clockTime != "" && !isTime()){
                    return;
                }
                if(t <= 0){
                    runCommands();
                    getTimer();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
    
    public void stopJob(){
        if(task != null) task.cancel();
    }
    
    private void getTimer() throws IllegalArgumentException {
        String[] args = time.split(" ");
        if(args.length >= 5){
            if(args.length == 7){
                if(args[5].contains("at")){
                    clockTime = args[6];
                }
            }
            cal = Calendar.getInstance();
            if(args[2].contains("day") && args[4].contains("month")){
                calDayMonth = Integer.parseInt(args[1]);
                t = clockTime == "" ? ((20 * 60) * 60 ) : 61;
            }else if(args[2].contains("day") && args[4].contains("week")){
                calDayWeek = Integer.parseInt(args[1]) + 1;
                t = clockTime == "" ? ((20 * 60) * 60 ) : 61;
            }else{
                throw new IllegalArgumentException("Invalid Time format: '" + time + "'");
            }
        }else if(args.length == 3){
            if(args[2].contains("second")){
                t = Integer.parseInt(args[1]);
            }else if(args[2].contains("minute")){
                t = Integer.parseInt(args[1]) * 60;
            }else if(args[2].contains("hour")){
                t = (Integer.parseInt(args[1]) * 60) * 60;
            }else if(args[2].contains("day") || args[2].contains("dayS")){
                t = ((Integer.parseInt(args[1]) * 60) * 60) * 24;
            }else{
                throw new IllegalArgumentException("Invalid Time format: '" + time + "'");
            }
        }else if(args.length == 2){
            if(args[0].contains("at")){
                clockTime = args[1];
                t = 61;
                return;
            }
            if(args[1].contains("second")){
                t = 1;
            }else if(args[1].contains("minute")){
                t = 60;
            }else if(args[1].contains("hour")){
                t = 60 * 60;
            }else if(args[1].contains("day") || args[1].contains("dayS")){
                t = (60 * 60) * 24;
            }else{
                throw new IllegalArgumentException("Invalid Time format: '" + time + "'");
            }
        }else{
            throw new IllegalArgumentException("Invalid Time format: '" + time + "'");
        }
    }
    
    public void runCommands(){
        for(String s : new ArrayList<>(cmds)){
            cron.getProxy().getPluginManager().dispatchCommand(cron.getProxy().getConsole(), s);
        }
    }
    
    public boolean isTime(){
        String[] args = clockTime.split(":");
        Calendar c = Calendar.getInstance();
        String hour, minute = "";
        
        hour = c.get(Calendar.HOUR_OF_DAY) + "";
        minute = c.get(Calendar.MINUTE) + "";
        
        String cHour = args[0];
        String cMinute = args[1];
        
        if(args[0].startsWith("0") && args[0].length() == 2){
            cHour = args[0].substring(1);
        }
        if(args[1].startsWith("0") && args[1].length() == 2){
            cMinute = args[1].substring(1);
        }
        return cMinute.equalsIgnoreCase(minute) && cHour.equalsIgnoreCase(hour);
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public List<String> getCommands() {
        return cmds;
    }
}
