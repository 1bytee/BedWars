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

public class SetItemSpawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player p = (Player) sender;

        if (!p.hasPermission("icewars.admin")) {
            p.sendMessage(IceWars.PREFIX + "You don't have permission to execute this command.");
            return true;
        }

        TeamType type = TeamType.of(args[0]);
        if (type == null) {
            p.sendMessage(IceWars.PREFIX + "Unknown TeamType: §e" + args[0]);
            return true;
        }

        if (args.length == 3) {

            int map = Integer.parseInt(args[1]);
            String itemType = args[2];

            save(p.getLocation(), type, map, itemType);
            p.sendMessage(IceWars.PREFIX + String.format("Item spawn has been set. (%1s, %d, %2s)", type.name(), map, itemType));

        } else {

            int map = Integer.parseInt(args[1]);
            String team = args[2];
            String itemType = args[3];

            save(p.getLocation(), type, map, team, itemType);
            p.sendMessage(IceWars.PREFIX + String.format("Item spawn has been set. (%1s, %d, %2s, %3s)", type.name(), map, team, itemType));

        }

        return false;
    }

    @SneakyThrows
    public void save(Location loc, TeamType type, int map, String itemType) {
        String path = map + "." + itemType + ".";
        File f = new File(IceWars.getInstance().getDataFolder(), type.name().toLowerCase() + ".yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        cfg.set(path + "X", loc.getX());
        cfg.set(path + "Y", loc.getY());
        cfg.set(path + "Z", loc.getZ());
        cfg.set(path + "W", loc.getWorld().getName());
        cfg.save(f);
    }

    @SneakyThrows
    public void save(Location loc, TeamType type, int map, String team, String itemType) {
        String path = map + "." + team + "." + itemType + ".";
        File f = new File(IceWars.getInstance().getDataFolder(), type.name().toLowerCase() + ".yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        cfg.set(path + "X", loc.getX());
        cfg.set(path + "Y", loc.getY());
        cfg.set(path + "Z", loc.getZ());
        cfg.set(path + "W", loc.getWorld().getName());
        cfg.save(f);
    }

}
