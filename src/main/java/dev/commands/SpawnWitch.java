package dev.commands;

import dev.IceWars;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftWitch;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class SpawnWitch implements CommandExecutor {

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
        witch.setCustomName("§eShopkeeper");
        witch.setCustomNameVisible(true);

        EntityWitch nmsWitch = ((CraftWitch) witch).getHandle();

        try {

            System.out.println(nmsWitch.getClass().getSuperclass().getSimpleName());
            System.out.println(nmsWitch.getClass().getSuperclass().getSuperclass().getSimpleName());
            System.out.println(nmsWitch.getClass().getSuperclass().getSuperclass().getSuperclass().getSimpleName());

            Field goal = nmsWitch.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("goalSelector");
            goal.setAccessible(true);

            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(goal, goal.getModifiers() & ~Modifier.FINAL);

            PathfinderGoalSelector goalSelector = new PathfinderGoalSelector(nmsWitch.world != null && nmsWitch.world.methodProfiler != null ? nmsWitch.world.methodProfiler : null);
            goal.set(nmsWitch, goalSelector);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        p.sendMessage(IceWars.PREFIX + "Witch has been spawned.");
        return true;
    }
}
