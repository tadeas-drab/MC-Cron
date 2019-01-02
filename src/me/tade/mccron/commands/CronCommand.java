package me.tade.mccron.commands;

import me.tade.mccron.Cron;
import me.tade.mccron.job.CronJob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author The_TadeSK
 */
public class CronCommand implements CommandExecutor {

    private Cron cron;

    public CronCommand(Cron cron) {
        this.cron = cron;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("mccron")){
            if(args.length == 0){
                sender.sendMessage("§aMC-Cron system by §eThe_TadeSK");
                sender.sendMessage("§aMC-Cron version: §e" + cron.getDescription().getVersion());
                return true;
            }else if(args[0].equalsIgnoreCase("reload")){
                if(!sender.hasPermission("mccron.reload")){
                    sender.sendMessage("§cNo permission!");
                    return true;
                }
                sender.sendMessage("§aReloading jobs..");
                for(CronJob j : cron.getJobs().values())
                    j.stopJob();

                cron.getJobs().clear();

                cron.reloadConfig();
                cron.saveConfig();

                cron.loadJobs();
                sender.sendMessage("§aJobs reloaded!");
            }else if(args[0].equalsIgnoreCase("list")){
                if(!sender.hasPermission("mccron.list")){
                    sender.sendMessage("§cNo permission!");
                    return true;
                }
                sender.sendMessage("§aAll Cron jobs:");
                int id = 1;
                for(CronJob j : cron.getJobs().values()){
                    sender.sendMessage("§c" + id + "# §a" + j.getName() + " §e(" + j.getTime() + ") §c" + j.getCommands().size() + " commands");
                    id++;
                }
            }
        }
        return true;
    }

}
