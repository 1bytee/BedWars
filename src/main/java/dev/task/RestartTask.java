package dev.task;

import dev.IceWars;
import dev.util.Locations;
import dev.util.Team;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RestartTask {

    public static void execute() {
        if (IceWars.CURRENT_TASK != null) {
            IceWars.CURRENT_TASK.cancel();
        }
        if (IceWars.ITEM_TASK != null) {
            IceWars.ITEM_TASK.cancel();
        }
        broadcast("The game has ended.");
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setFlying(false);
            p.setAllowFlight(false);
            p.getInventory().clear();
            p.getInventory().setArmorContents(new ItemStack[4]);
            p.getActivePotionEffects().forEach(potionEffect -> p.removePotionEffect(potionEffect.getType()));
            p.setFireTicks(0);
            p.teleport(Locations.getLocation("spawn"));
        }
        if (IceWars.getTeams().size() != 1) {
            broadcast("There was no winner.");
        } else {
            Team team = IceWars.getTeams().get(0);
            broadcast("Team " + team.getColor() + team.getName() + " §7has won the game.");
            sendTitle(team.getColor() + team.getName(), "§7wins!");
        }
        Bukkit.broadcastMessage("§cServer restarting in 10 seconds.");
        Bukkit.getScheduler().scheduleSyncDelayedTask(IceWars.getInstance(), Bukkit::shutdown, 10 * 20L);
    }

    private static void broadcast(String message) {
        Bukkit.broadcastMessage(IceWars.PREFIX + message);
    }

    private static void sendTitle(String title, String subtitle) {
        Bukkit.getOnlinePlayers().forEach(p -> sendTitle(p, title, subtitle));
    }

    private static void sendTitle(Player p, String title, String subtitle) {
        CraftPlayer cp = (CraftPlayer) p;
        PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title + "\"}"));
        PacketPlayOutTitle packetSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subtitle + "\"}"));
        PacketPlayOutTitle times = new PacketPlayOutTitle(10, 40, 10);
        cp.getHandle().playerConnection.sendPacket(times);
        cp.getHandle().playerConnection.sendPacket(packetTitle);
        cp.getHandle().playerConnection.sendPacket(packetSubtitle);
    }

}
