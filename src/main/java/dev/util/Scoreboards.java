package dev.util;

import dev.IceWars;
import dev.NameTag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Scoreboards {

    public static void doScoreboard() {
        Scoreboard board;

        for (Player p : Bukkit.getOnlinePlayers()) {
            board = Bukkit.getScoreboardManager().getNewScoreboard();

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
                if (p == player) {
                    String color = Ranks.getColorIgnoreNick(p);
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
                } else {
                    if (NameTag.NICKED.containsKey(player)) {
                        System.out.println(NameTag.NICKED.get(player));
                        switch (NameTag.NICKED.get(player)) {
                            case PREMIUM:
                                premium.addEntry(player.getDisplayName());
                                break;
                            case PLAYER:
                                user.addEntry(player.getDisplayName());
                                break;
                        }
                    } else {
                        String color = Ranks.getColor(player);
                        if (color.equals("§4"))
                            admin.addEntry(player.getDisplayName());
                        else if (color.equals("§b"))
                            developer.addEntry(player.getDisplayName());
                        else if (color.equals("§e") && Ranks.getGroup(player).equalsIgnoreCase("moderator"))
                            moderator.addEntry(player.getDisplayName());
                        else if (color.equals("§e") && Ranks.getGroup(player).equalsIgnoreCase("srmoderator"))
                            srmoderator.addEntry(player.getDisplayName());
                        else if (color.equals("§5"))
                            vip.addEntry(player.getDisplayName());
                        else if (color.equals("§6"))
                            premium.addEntry(player.getDisplayName());
                        else if (color.equals("§a"))
                            builder.addEntry(player.getDisplayName());
                        else
                            user.addEntry(player.getDisplayName());
                    }
                }
            }
            p.setScoreboard(board);
        }
    }

    public static void doIngameScoreboard() {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Team blue = board.registerNewTeam("blue");
        Team red = board.registerNewTeam("red");
        Team green = board.registerNewTeam("green");
        Team yellow = board.registerNewTeam("yellow");
        Team noTeam = board.registerNewTeam("zunlisted");
        blue.setPrefix("§b");
        red.setPrefix("§c");
        green.setPrefix("§a");
        yellow.setPrefix("§e");
        noTeam.setPrefix("§7");

        Bukkit.getOnlinePlayers().forEach(p -> {
            dev.util.Team team = IceWars.getTeam(p);
            if (team == null) {
                noTeam.addEntry(p.getDisplayName());
            } else {
                switch (team.getColor()) {
                    case AQUA:
                        blue.addEntry(p.getDisplayName());
                        break;
                    case GREEN:
                        green.addEntry(p.getDisplayName());
                        break;
                    case RED:
                        red.addEntry(p.getDisplayName());
                        break;
                    case YELLOW:
                        yellow.addEntry(p.getDisplayName());
                        break;
                    default:
                        noTeam.addEntry(p.getDisplayName());
                        break;
                }
            }
        });

        Bukkit.getOnlinePlayers().forEach(p -> p.setScoreboard(board));

    }

}
