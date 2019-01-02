package me.tade.mccron.bungee.commands;

import me.tade.mccron.Cron;
import me.tade.mccron.bungee.BungeeCron;
import me.tade.mccron.bungee.job.BungeeCronJob;
import me.tade.mccron.job.CronJob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author The_TadeSK
 */
public class BungeeCronCommand extends net.md_5.bungee.api.plugin.Command {

    private BungeeCron cron;

    public BungeeCronCommand(BungeeCron cron) {
        super("mccron");
        this.cron = cron;
    }

    @Override
    public void execute(net.md_5.bungee.api.CommandSender sender, String[] args) {
        if(args.length == 0){
            sender.sendMessage("§aMC-Cron system by §eThe_TadeSK");
            sender.sendMessage("§aMC-Cron version: §e" + cron.getDescription().getVersion());
        }else if(args[0].equalsIgnoreCase("reload")){
            if(!sender.hasPermission("mccron.reload")){
                sender.sendMessage("§cNo permission!");
                return;
            }
            sender.sendMessage("§aReloading jobs..");
            for(BungeeCronJob j : cron.getJobs().values())
                j.stopJob();

            cron.getJobs().clear();

            cron.reloadConfig();
            cron.saveConfig();

            cron.loadJobs();
            sender.sendMessage("§aJobs reloaded!");
        }else if(args[0].equalsIgnoreCase("list")){
            if(!sender.hasPermission("mccron.list")){
                sender.sendMessage("§cNo permission!");
                return;
            }
            sender.sendMessage("§aAll Cron jobs:");
            int id = 1;
            for(BungeeCronJob j : cron.getJobs().values()){
                sender.sendMessage("§c" + id + "# §a" + j.getName() + " §e(" + j.getTime() + ") §c" + j.getCommands().size() + " commands");
                id++;
            }
        }
    }
}
