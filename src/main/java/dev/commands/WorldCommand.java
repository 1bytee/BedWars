package dev.commands;

import dev.IceWars;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class WorldCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player p = (Player) sender;

        if (!p.hasPermission("icewars.admin")) {
            p.sendMessage(IceWars.PREFIX + "You don't have permission to execute this command.");
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(IceWars.PREFIX + "Usage: §e/world <Name>");
            p.sendMessage(IceWars.PREFIX + "Available worlds: §e" + Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.joining(", ")));
            return true;
        }

        World world = Bukkit.getWorld(args[0]);

        if (world == null) {
            p.sendMessage(IceWars.PREFIX + "World " + args[0] + " doesn't exist.");
            p.sendMessage(IceWars.PREFIX + "Available worlds: §e" + Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.joining(", ")));
            return true;
        }

        p.teleport(world.getSpawnLocation());
        return true;
    }
}
