package dev.events;

import dev.IceWars;
import dev.stats.PlayerStats;
import dev.task.RestartTask;
import dev.util.*;
import net.minecraft.server.v1_9_R1.IChatBaseComponent;
import net.minecraft.server.v1_9_R1.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
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

        PlayerStats stats = new PlayerStats(p);
        stats.loadStats();
        IceWars.STATS.put(p.getName(), stats);

        if (p.isDead())
            p.spigot().respawn();

        if (IceWars.STATE == GameState.WARMUP) {
            e.setJoinMessage(IceWars.PREFIX + "§e" + p.getDisplayName() + " §7joined the game.");
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

        IceWars.STATS.get(p.getDisplayName()).saveStats();

        if (IceWars.STATE == GameState.WARMUP) {
            e.setQuitMessage(IceWars.PREFIX + "§e" + p.getDisplayName() + " §7left the game.");
        } else {
            e.setQuitMessage("");
            if (IceWars.INGAME.contains(p)) {
                Team team = IceWars.getTeam(p);
                team.removePlayer(p);
                Bukkit.broadcastMessage(IceWars.PREFIX + "Player " + team.getColor() + p.getDisplayName() + " §7left the game.");
                Bukkit.broadcastMessage(IceWars.PREFIX + "Team " + team.getColor() + team.getName() + " §7has §e" + team.getPlayers().size() + " §7players left.");
                IceWars.INGAME.remove(p);
            } else if (IceWars.SPECTATING.contains(p)) {
                IceWars.SPECTATING.remove(p);
            }
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player p = e.getPlayer();

        IceWars.STATS.get(p.getDisplayName()).saveStats();

        if (IceWars.STATE == GameState.WARMUP) {
            e.setLeaveMessage(IceWars.PREFIX + "§e" + p.getDisplayName() + " §7left the game.");
        } else {
            e.setLeaveMessage("");
            if (IceWars.INGAME.contains(p)) {
                Team team = IceWars.getTeam(p);
                team.removePlayer(p);
                Bukkit.broadcastMessage(IceWars.PREFIX + "Player " + team.getColor() + p.getDisplayName() + " §7left the game.");
                Bukkit.broadcastMessage(IceWars.PREFIX + "Team " + team.getColor() + team.getName() + " §7has §e" + team.getPlayers().size() + " §7players left.");
                IceWars.INGAME.remove(p);
            } else if (IceWars.SPECTATING.contains(p)) {
                IceWars.SPECTATING.remove(p);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        e.setCancelled(true);
        if (IceWars.STATE != GameState.INGAME) {
            Bukkit.broadcastMessage(Ranks.getPrefix(p) + p.getDisplayName() + ": §f" + e.getMessage());
        } else {
            if (IceWars.INGAME.contains(p)) {
                Team team = IceWars.getTeam(p);
                if (e.getMessage().startsWith("@all")) {
                    Bukkit.broadcastMessage("§7[ALL] " + team.getColor() + p.getDisplayName() + ": §f" + e.getMessage().substring(5, e.getMessage().length()));
                } else {
                    team.sendMessage(team.getColor() + p.getDisplayName() + ": §f" + e.getMessage());
                }
            } else {
                IceWars.SPECTATING.forEach(player -> player.sendMessage("§c[DEAD] " + Ranks.getPrefix(p) + p.getDisplayName() + ": §f" + e.getMessage()));
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
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Witch || IceWars.STATE != GameState.INGAME) {
            e.setCancelled(true);
        }

        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (IceWars.SPECTATING.contains(p)) {
                e.setCancelled(true);
                e.setDamage(0D);
                return;
            }
            Player killer = null;
            if (e.getFinalDamage() > p.getHealth()) {

                p.setHealth(20D);
                p.setFoodLevel(20);
                p.getInventory().clear();
                p.getInventory().setArmorContents(new ItemStack[4]);

                e.setCancelled(true);
                e.setDamage(0D);

                if (p.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) p.getLastDamageCause();
                    if (event.getDamager() instanceof Player) {
                        Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7was killed by §e" + ((Player) event.getDamager()).getDisplayName() + "§7.");
                        killer = (Player) event.getDamager();
                    } else if (event.getDamager() instanceof Arrow) {
                        Arrow arrow = (Arrow) event.getDamager();
                        if (arrow.getShooter() instanceof Player) {
                            Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7was killed by §e" + ((Player) arrow.getShooter()).getDisplayName() + "§7.");
                            killer = (Player) arrow.getShooter();
                        } else {
                            Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7died.");
                        }
                    } else if (event.getDamager() instanceof FishHook) {
                        FishHook hook = (FishHook) event.getDamager();
                        if (hook.getShooter() instanceof Player) {
                            Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7was killed by §e" + ((Player) hook.getShooter()).getDisplayName() + "§7.");
                            killer = (Player) hook.getShooter();
                        } else {
                            Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7died.");
                        }
                    } else {
                        Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7died.");
                    }
                } else {
                    Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7died.");
                }

                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 255));
                sendTitle(p, "§cYOU DIED");
                Team team = IceWars.getTeam(p);

                if (team == null) {
                    p.kickPlayer("§cAn error occurred.");
                    System.err.println("[IceWars] Player " + p.getDisplayName() + " wasn't in a team? [EntityDamageByEntityEvent]");
                    return;
                }

                if (team.hasIceblock()) {
                    p.teleport(Locations.getSpawn(team));
                } else {
                    PlayerStats stats = IceWars.STATS.get(p.getDisplayName());
                    stats.addDeath();
                    IceWars.STATS.put(p.getDisplayName(), stats);
                    if (killer != null) {
                        PlayerStats killerStats = IceWars.STATS.get(killer.getDisplayName());
                        killerStats.addKill();
                        IceWars.STATS.put(killer.getDisplayName(), stats);
                    }
                    p.teleport(Locations.getLocation("spawn"));
                    team.removePlayer(p);
                    if (team.getPlayers().size() > 0) {
                        Bukkit.broadcastMessage(IceWars.PREFIX + "Team " + team.getColor() + team.getName() + " §7has only §e" + team.getPlayers().size() + (team.getPlayers().size() == 0 ? " §7player " : " §7players ") + "left.");
                    } else {
                        Bukkit.broadcastMessage(IceWars.PREFIX + "Team " + team.getColor() + team.getName() + " §7has been §c§lELIMINATED§7.");
                        IceWars.getTeams().remove(team);
                    }
                    if (IceWars.getTeams().size() <= 1) {
                        RestartTask.execute();
                    }
                    p.sendMessage(IceWars.PREFIX + "You have been eliminated.");
                    IceWars.setToSpectator(p);
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Player killer = null;
        if (IceWars.SPECTATING.contains(p)) {
            return;
        }
        if (p.getLocation().getY() < -23) {
            p.setFallDistance(-100F);

            p.setHealth(20D);
            p.setFoodLevel(20);
            p.getInventory().clear();
            p.getInventory().setArmorContents(new ItemStack[4]);

            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 255));
            sendTitle(p, "§cYOU DIED");
            Team team = IceWars.getTeam(p);

            if (p.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) p.getLastDamageCause();
                if (event.getDamager() instanceof Player) {
                    Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7was killed by §e" + ((Player) event.getDamager()).getDisplayName() + "§7.");
                    killer = (Player) event.getDamager();
                } else if (event.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) event.getDamager();
                    if (arrow.getShooter() instanceof Player) {
                        Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7was killed by §e" + ((Player) arrow.getShooter()).getDisplayName() + "§7.");
                        killer = (Player) arrow.getShooter();
                    } else {
                        Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7died.");
                    }
                } else if (event.getDamager() instanceof FishHook) {
                    FishHook hook = (FishHook) event.getDamager();
                    if (hook.getShooter() instanceof Player) {
                        Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7was killed by §e" + ((Player) hook.getShooter()).getDisplayName() + "§7.");
                        killer = (Player) hook.getShooter();
                    } else {
                        Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7died.");
                    }
                } else {
                    Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7died.");
                }
            } else {
                Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7died.");
            }

            if (team == null) {
                p.kickPlayer("§cAn error occurred.");
                System.err.println("[IceWars] Player " + p.getDisplayName() + " wasn't in a team? [PlayerMoveEvent]");
                return;
            }

            if (team.hasIceblock()) {
                p.teleport(Locations.getSpawn(team));
            } else {
                PlayerStats stats = IceWars.STATS.get(p.getDisplayName());
                stats.addDeath();
                IceWars.STATS.put(p.getDisplayName(), stats);
                if (killer != null) {
                    PlayerStats killerStats = IceWars.STATS.get(killer.getDisplayName());
                    killerStats.addKill();
                    IceWars.STATS.put(killer.getDisplayName(), stats);
                }
                p.teleport(Locations.getLocation("spawn"));
                team.removePlayer(p);
                if (team.getPlayers().size() > 0) {
                    Bukkit.broadcastMessage(IceWars.PREFIX + "Team " + team.getColor() + team.getName() + " §7has only §e" + team.getPlayers().size() + (team.getPlayers().size() == 0 ? " §7player " : " §7players ") + "left.");
                } else {
                    Bukkit.broadcastMessage(IceWars.PREFIX + "Team " + team.getColor() + team.getName() + " §7has been §c§lELIMINATED§7.");
                    IceWars.getTeams().remove(team);
                }
                if (IceWars.getTeams().size() <= 1) {
                    RestartTask.execute();
                }
                p.sendMessage(IceWars.PREFIX + "You have been eliminated.");
                IceWars.setToSpectator(p);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (!IceWars.INGAME.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        e.setCancelled(true);
        e.getWhoClicked().closeInventory();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            e.setCancelled(true);
            e.setDamage(0D);
            return;
        }

        Player p = (Player) e.getEntity();
        Player killer = null;

        if (IceWars.SPECTATING.contains(p)) {
            e.setCancelled(true);
            e.setDamage(0D);
            return;
        }

        if (e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();
            if (IceWars.getTeam(damager).equals(IceWars.getTeam(p))) {
                e.setCancelled(true);
                e.setDamage(0D);
                return;
            } else if (damager.getInventory().getItemInMainHand() == null || damager.getInventory().getItemInMainHand().getType() == Material.AIR) {
                e.setDamage(0.5D);
            }
        }

        if (e.getFinalDamage() > p.getHealth()) {

            p.setHealth(20D);
            p.setFoodLevel(20);
            p.getInventory().clear();
            p.getInventory().setArmorContents(new ItemStack[4]);

            e.setCancelled(true);
            e.setDamage(0D);

            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 255));
            sendTitle(p, "§cYOU DIED");
            Team team = IceWars.getTeam(p);

            if (team == null) {
                p.kickPlayer("§cAn error occurred.");
                System.err.println("[IceWars] Player " + p.getDisplayName() + " wasn't in a team? [EntityDamageByEntityEvent]");
                return;
            }

            if (e.getDamager() instanceof Player) {
                Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7was killed by §e" + ((Player) e.getDamager()).getDisplayName() + "§7.");
                killer = (Player) e.getDamager();
            } else if (e.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getDamager();
                if (arrow.getShooter() instanceof Player) {
                    Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7was killed by §e" + ((Player) arrow.getShooter()).getDisplayName() + "§7.");
                    killer = (Player) arrow.getShooter();
                } else {
                    Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7died.");
                }
            } else if (e.getDamager() instanceof FishHook) {
                FishHook hook = (FishHook) e.getDamager();
                if (hook.getShooter() instanceof Player) {
                    Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7was killed by §e" + ((Player) hook.getShooter()).getDisplayName() + "§7.");
                    killer = (Player) hook.getShooter();
                } else {
                    Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7died.");
                }
            } else {
                Bukkit.broadcastMessage(IceWars.PREFIX + "Player §e" + p.getDisplayName() + " §7died.");
            }

            if (team.hasIceblock()) {
                p.teleport(Locations.getSpawn(team));
            } else {
                PlayerStats stats = IceWars.STATS.get(p.getDisplayName());
                stats.addDeath();
                IceWars.STATS.put(p.getDisplayName(), stats);
                if (killer != null) {
                    PlayerStats killerStats = IceWars.STATS.get(killer.getDisplayName());
                    killerStats.addKill();
                    IceWars.STATS.put(killer.getDisplayName(), stats);
                }
                p.teleport(Locations.getLocation("spawn"));
                team.removePlayer(p);
                if (team.getPlayers().size() > 0) {
                    Bukkit.broadcastMessage(IceWars.PREFIX + "Team " + team.getColor() + team.getName() + " §7has only §e" + team.getPlayers().size() + (team.getPlayers().size() == 0 ? " §7player " : " §7players ") + "left.");
                } else {
                    Bukkit.broadcastMessage(IceWars.PREFIX + "Team " + team.getColor() + team.getName() + " §7has been §c§lELIMINATED§7.");
                    IceWars.getTeams().remove(team);
                }
                if (IceWars.getTeams().size() <= 1) {
                    RestartTask.execute();
                }
                p.sendMessage(IceWars.PREFIX + "You have been eliminated.");
                IceWars.setToSpectator(p);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        Player p = (Player) e.getEntity();
        if (IceWars.STATE != GameState.INGAME) {
            e.setCancelled(true);
            e.setFoodLevel(20);
        } else {
            p.setSaturation(4f);
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
