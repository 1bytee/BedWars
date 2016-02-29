package dev.commands;

import dev.IceWars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;

public class SpawnWitchCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player p = (Player) sender;

        if (!p.hasPermission("icewars.admin")) {
            p.sendMessage(IceWars.PREFIX + "You don't have permission to execute this command.");
            return true;
        }


        Witch witch = p.getWorld().spawn(p.getLocation(), Witch.class);
        witch.setPassenger(witch);
        witch.setCustomName("§eShopkeeper");
        witch.setCustomNameVisible(true);
        p.sendMessage(IceWars.PREFIX + "Witch has been spawned.");
        return true;
    }
}
