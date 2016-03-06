package dev.task;

import dev.IceWars;
import dev.util.GameState;
import dev.util.Locations;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class WarmupTask extends AbstractTask {

    int cooldown = 60;

    @Override
    public void run() {
        if (Bukkit.getOnlinePlayers().size() < 2 && cooldown == 60) {
            cooldown = 60;
        } else if (Bukkit.getOnlinePlayers().size() < 2 && cooldown < 40) {
            broadcast("Not enough players online. §c§lRestarting...");
            cancel();
            Bukkit.getScheduler().scheduleSyncDelayedTask(IceWars.getInstance(), Bukkit::shutdown, 20L);
        } else if (cooldown != 0) {
            if (cooldown == 60) {
                IceWars.getInstance().load();
            }

            if (cooldown == 60 || cooldown == 30 || cooldown == 15 || cooldown == 10) {
                broadcast("Game starting in §e" + cooldown + "§7 seconds.");
            }

            if (cooldown <= 5 && cooldown >= 1) {
                broadcast("Game starting in §e" + cooldown + "§7 " + (cooldown == 1 ? "second." : "seconds."));
                sendTitle();
            }

            cooldown--;
        } else {
            Bukkit.getOnlinePlayers().forEach(IceWars.INGAME::add);
            Bukkit.getOnlinePlayers().forEach(p -> p.getInventory().clear());
            IceWars.STATE = GameState.INGAME;

            Bukkit.getOnlinePlayers().stream().filter(p -> IceWars.getTeam(p) == null).forEach(p -> IceWars.nextFreeTeam().addPlayer(p));

            IceWars.getTeams().forEach(team -> team.getPlayers().forEach(p -> p.teleport(Locations.getSpawn(team))));
            cancel();

            IceWars.CURRENT_TASK = new IngameTask();
            IceWars.ITEM_TASK = new ItemTask();
        }
    }

    public void sendTitle() {
        if (color() == null)
            return;

        Bukkit.getOnlinePlayers().forEach(p -> sendTitle(p, color().toString() + cooldown));
    }

    private void sendTitle(Player p, String title) {
        CraftPlayer cp = (CraftPlayer) p;
        PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title + "\"}"));
        PacketPlayOutTitle times = new PacketPlayOutTitle(5, 12, 3);
        cp.getHandle().playerConnection.sendPacket(times);
        cp.getHandle().playerConnection.sendPacket(packetTitle);
    }

    private ChatColor color() {
        switch (cooldown) {
            case 5:
                return ChatColor.DARK_RED;
            case 4:
                return ChatColor.RED;
            case 3:
                return ChatColor.GOLD;
            case 2:
                return ChatColor.YELLOW;
            case 1:
                return ChatColor.GREEN;
        }
        return null;
    }

}
