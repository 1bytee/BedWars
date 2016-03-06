package dev.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Scoreboards {

    public static void doScoreboard() {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

        Team admin = board.registerNewTeam("1admin");
        admin.setPrefix("§7[ADMIN] §4");
        Team developer = board.registerNewTeam("2developer");
        developer.setPrefix("§7[DEV] §b");
        Team srmoderator = board.registerNewTeam("3srmoderator");
        srmoderator.setPrefix("§7[SRMOD] §e");
        Team moderator = board.registerNewTeam("4moderator");
        moderator.setPrefix("§7[MOD] §e");
        Team builder = board.registerNewTeam("5builder");
        builder.setPrefix("§7[BUILDER] §a");
        Team vip = board.registerNewTeam("5vip");
        vip.setPrefix("§7[VIP] §5");
        Team premium = board.registerNewTeam("6premium");
        premium.setPrefix("§6");
        Team user = board.registerNewTeam("7user");
        user.setPrefix("§7");

        for (Player player : Bukkit.getOnlinePlayers()) {
            String color = Ranks.getColor(player);
            if (color.equals("§4"))
                admin.addEntry(player.getName());
            else if (color.equals("§b"))
                developer.addEntry(player.getName());
            else if (color.equals("§e") && Ranks.getGroup(player).equalsIgnoreCase("moderator"))
                moderator.addEntry(player.getName());
            else if (color.equals("§e") && Ranks.getGroup(player).equalsIgnoreCase("srmoderator"))
                srmoderator.addEntry(player.getName());
            else if (color.equals("§5"))
                vip.addEntry(player.getName());
            else if (color.equals("§6"))
                premium.addEntry(player.getName());
            else if (color.equals("§a"))
                builder.addEntry(player.getName());
            else
                user.addEntry(player.getName());
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(board);
        }
    }

}
