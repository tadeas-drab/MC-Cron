package me.tade.mccron.bungee.commands;

import me.tade.mccron.Cron;
import me.tade.mccron.bungee.BungeeCron;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author The_TadeSK
 */
public class BungeeTimerCommand extends Command {

    private BungeeCron cron;

    public BungeeTimerCommand(BungeeCron cron) {
        super("timer");
        this.cron = cron;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            sender.sendMessage("§cOnly console can perform this command!");
            return;
        }
        if(args.length == 0){
            sender.sendMessage("§aUse /timer <time> <command>");
            return;
        }else if(args.length >= 2){
            String c = "";
            for(int i = 1;i < args.length;i++){
                c = c + " " + args[i];
            }
            c = c.substring(1);

            int time = Integer.valueOf(args[0]);
            if(time > 300){
                sender.sendMessage("§cMaximum is 300 seconds (5 minutes)!");
                return;
            }
            runCmd(c, time);
        }
    }
    
    public void runCmd(String cmd, int seconds){
        BungeeCord.getInstance().getScheduler().schedule(cron, new Runnable() {
            @Override
            public void run() {
                BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(), cmd);
            }
        }, seconds, TimeUnit.SECONDS);
    }
}
