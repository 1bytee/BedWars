package dev.commands;

import dev.IceWars;
import dev.util.TeamType;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class SetTeamSpawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player p = (Player) sender;

        if (!p.hasPermission("icewars.admin")) {
            p.sendMessage(IceWars.PREFIX + "You don't have permission to execute this command.");
            return true;
        }

        if (args.length < 3) {
            p.sendMessage(IceWars.PREFIX + "Usage: §e/setteamspawn <TeamType> <Map> <Team>");
            return true;
        }

        TeamType type = TeamType.of(args[0]);
        if (type == null) {
            p.sendMessage(IceWars.PREFIX + "Unknown TeamType: §e" + args[0]);
            return true;
        }

        int map = Integer.parseInt(args[1]);
        String team = args[2];

        save(p.getLocation(), type, map, team);
        p.sendMessage(IceWars.PREFIX + String.format("Spawn has been set. (%1s, %d, %2s)", type.name(), map, team));
        return true;
    }

    @SneakyThrows
    public void save(Location loc, TeamType type, int map, String team) {
        String path = map + "." + team + ".";
        File f = new File(IceWars.getInstance().getDataFolder(), type.name().toLowerCase() + ".yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        cfg.set(path + "X", loc.getX());
        cfg.set(path + "Y", loc.getY());
        cfg.set(path + "Z", loc.getZ());
        cfg.set(path + "yaw", loc.getYaw());
        cfg.set(path + "pitch", loc.getPitch());
        cfg.set(path + "W", loc.getWorld().getName());
        cfg.save(f);
    }

}
