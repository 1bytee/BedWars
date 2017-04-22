package dev.commands;

import dev.IceWars;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class ClearEntities implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("icewars.clearentities")) {
            p.sendMessage(IceWars.PREFIX + "You are not allowed to execute this command.");
            return true;
        }

        if (args.length == 1) {
            String world = args[0];
            World w = Bukkit.getWorld(world);
            if (w == null) {
                p.sendMessage(IceWars.PREFIX + "Unknown world: §e" + world);
                return true;
            }
            int i = 0;
            for (Entity entity : w.getEntities()) {
                if (entity.getType() != EntityType.PLAYER) {
                    entity.remove();
                    i++;
                }
            }
            p.sendMessage(IceWars.PREFIX + "§e" + i + " §7entities removed in world§e " + w.getName() + "§7.");
        } else {
            int i = 0;
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if (entity.getType() != EntityType.PLAYER) {
                        entity.remove();
                        i++;
                    }
                }
            }
            p.sendMessage(IceWars.PREFIX + "§e" + i + " §7entities removed in §e" + Bukkit.getWorlds().size() + " §7worlds.");
        }
        return true;
    }
}
