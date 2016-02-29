package dev.commands;

import dev.IceWars;
import dev.util.Locations;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLobbySpawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player p = (Player) sender;

        if (!p.hasPermission("icewars.admin")) {
            p.sendMessage(IceWars.PREFIX + "You don't have permission to execute this command.");
            return true;
        }

        Locations.saveLocation(p.getLocation(), "spawn");
        p.sendMessage(IceWars.PREFIX + "Spawn has been set.");
        return true;
    }
}
