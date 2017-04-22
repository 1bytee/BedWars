package dev.commands;

import dev.IceWars;
import dev.stats.PlayerStats;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StatsCommand implements CommandExecutor {

    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.YY HH:mm:ss [zzz]", Locale.ENGLISH);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return true;

        String name = sender.getName();

        if (args.length == 1) {
            name = args[0];
        }

        if (!IceWars.STATS.containsKey(name)) {
            boolean found = false;
            for (String key : IceWars.STATS.keySet()) {
                if (key.equalsIgnoreCase(name)) {
                    name = key;
                    found = true;
                    break;
                }
            }
            if (!found) {
                PlayerStats stats = new PlayerStats(name);
                if (!stats.loadStats()) {
                    sender.sendMessage(IceWars.PREFIX + "Can't find §e" + name + "'s §7stats!");
                    return true;
                } else {
                    IceWars.STATS.put(name, stats);
                }
            }
        }

        PlayerStats stats = IceWars.STATS.get(name);
        sender.sendMessage(IceWars.PREFIX + "§e" + name + "'s §7Stats:");
        sender.sendMessage(IceWars.PREFIX + "Kills: §e" + stats.getKills());
        sender.sendMessage(IceWars.PREFIX + "Deaths: §e" + stats.getDeaths());
        sender.sendMessage(IceWars.PREFIX + "K/D: §e" + stats.getKD());
        sender.sendMessage(IceWars.PREFIX + "Victories: §e" + stats.getVictories());
        sender.sendMessage(IceWars.PREFIX + "Games played: §e" + stats.getGamesPlayed());
        sender.sendMessage(IceWars.PREFIX + "Ice blocks destroyed: §e" + stats.getDestroyedIceBlocks());
        sender.sendMessage(IceWars.PREFIX + "Last updated: §e" + format.format(new Date(stats.getUpdated())));
        return true;
    }

}
