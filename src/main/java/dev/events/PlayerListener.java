package dev.events;

import dev.IceWars;
import dev.util.*;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        Scoreboards.doScoreboard();

        if (p.isDead())
            p.spigot().respawn();

        if (IceWars.STATE == GameState.WARMUP) {
            e.setJoinMessage(IceWars.PREFIX + "§e" + p.getName() + " §7joined the game.");
            p.sendMessage(IceWars.PREFIX + "Welcome to IceWars.");
            p.getActivePotionEffects().forEach(potionEffect -> p.removePotionEffect(potionEffect.getType()));
            p.setHealth(20D);
            p.setFoodLevel(20);
            p.setFireTicks(0);
            p.setFlying(false);
            p.setAllowFlight(false);
            p.getInventory().clear();
            p.getInventory().setArmorContents(new ItemStack[4]);
            p.setGameMode(GameMode.SURVIVAL);

            if (Locations.getLocation("spawn") != null) {
                p.teleport(Locations.getLocation("spawn"));
            }

            ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§eTeam Selector");
            meta.setLore(Arrays.asList("", "§7Click to select your team."));
            item.setItemMeta(meta);
            p.getInventory().addItem(item);

        } else {
            e.setJoinMessage("");
            IceWars.setToSpectator(p);
            if (Locations.getLocation("spawn") != null) {
                p.teleport(Locations.getLocation("spawn"));
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (IceWars.STATE == GameState.WARMUP) {
            e.setQuitMessage(IceWars.PREFIX + "§e" + p.getName() + " §7left the game.");
        } else {
            e.setQuitMessage("");
            if (IceWars.INGAME.contains(p)) {
                Team team = IceWars.getTeam(p);
                team.removePlayer(p);
                Bukkit.broadcastMessage(IceWars.PREFIX + "Player " + team.getColor() + p.getName() + " §7left the game.");
                Bukkit.broadcastMessage(IceWars.PREFIX + "Team " + team.getColor() + team.getName() + " §7has §e" + team.getPlayers().size() + " §7players left.");
                IceWars.INGAME.remove(p);
            } else if (IceWars.SPECTATING.contains(p)) {
                IceWars.SPECTATING.remove(p);
            }
        }
    }

    @EventHandler
    public void onCamKittys(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        e.setCancelled(true);
        if (IceWars.STATE != GameState.INGAME) {
            Bukkit.broadcastMessage(Ranks.getPrefix(p) + p.getName() + ": §f" + e.getMessage());
        } else {
            if (IceWars.INGAME.contains(p)) {
                Team team = IceWars.getTeam(p);
                if (e.getMessage().startsWith("@all")) {
                    Bukkit.broadcastMessage(team.getColor() + p.getName() + ": §f" + e.getMessage().substring(5, e.getMessage().length()));
                } else {
                    team.sendMessage(team.getColor() + p.getName() + ": §f" + e.getMessage());
                }
            } else {
                IceWars.SPECTATING.forEach(player -> player.sendMessage("§4§l[X] " + Ranks.getPrefix(p) + ": §f" + e.getMessage()));
            }
        }
    }



    @EventHandler
    public void onFarCry(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        Team team = IceWars.getTeam(p);
        if (team != null && team.hasIceblock()) {
            e.setRespawnLocation(Locations.getSpawn(team));
        } else {
            e.setRespawnLocation(Locations.getLocation("spawn"));
            p.sendMessage(IceWars.PREFIX + "You have been eliminated.");
        }
    }

    @EventHandler
    public void onCedricWech(EntityDamageByEntityEvent e) {
        Player p = (Player) e.getEntity();

        if (e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();
            if (IceWars.getTeam(damager) == IceWars.getTeam(p)) {
                e.setCancelled(true);
                e.setDamage(0D);
                return;
            }
        }

        if (e.getDamage() > p.getHealth()) {
            e.setCancelled(true);
            e.setDamage(0D);

            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2, 255));
            sendTitle(p, "§cYOU DIED");
            Team team = IceWars.getTeam(p);

            if (team == null) {
                p.kickPlayer("§cAn error occurred.");
                System.err.println("[IceWars] Player " + p.getName() + " wasn't in a team? [PlayerDeathEvent]");
                return;
            }

            if (team.hasIceblock()) {
                p.teleport(Locations.getSpawn(team));
            } else {
                p.teleport(Locations.getLocation("spawn"));
                team.removePlayer(p);
                if (team.getPlayers().size() > 0) {
                    Bukkit.broadcastMessage(IceWars.PREFIX + "Team §e" + team.getName() + " §7has only §e" + team.getPlayers().size() + (team.getPlayers().size() == 0 ? " §7player " : " §7players ") + "left.");
                } else {
                    Bukkit.broadcastMessage(IceWars.PREFIX + "Team §e" + team.getName() + " §7has been §c§lELIMINATED§7.");
                    IceWars.getTeams().remove(team);
                }
                p.sendMessage(IceWars.PREFIX + "You have been eliminated.");
                IceWars.setToSpectator(p);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (IceWars.STATE != GameState.INGAME) {
            e.setCancelled(true);
            e.setFoodLevel(20);
        }
    }

    private void sendTitle(Player p, String title) {
        CraftPlayer cp = (CraftPlayer) p;
        PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title + "\"}"));
        PacketPlayOutTitle times = new PacketPlayOutTitle(10, 20, 10);
        cp.getHandle().playerConnection.sendPacket(times);
        cp.getHandle().playerConnection.sendPacket(packetTitle);
    }

}
