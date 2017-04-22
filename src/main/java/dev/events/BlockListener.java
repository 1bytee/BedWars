package dev.events;

import dev.IceWars;
import dev.stats.PlayerStats;
import dev.util.GameState;
import dev.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class BlockListener implements Listener {

    private final List<Material> materials = Arrays.asList(Material.CHEST, Material.SANDSTONE, Material.IRON_BLOCK, Material.ENDER_STONE, Material.GLOWSTONE);

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (IceWars.SETUPMODE)
            return;

        Player p = e.getPlayer();
        Material mat = e.getBlock().getType();
        if (IceWars.STATE != GameState.INGAME) {
            e.setCancelled(true);
        } else {
            if (mat == Material.PACKED_ICE) {
                Team nearestTeam = IceWars.getTeams().get(0);
                for (Team team : IceWars.getTeams()) {
                    if (team.getIceBlock().getLocation().distance(e.getBlock().getLocation()) < nearestTeam.getIceBlock().getLocation().distance(e.getBlock().getLocation())) {
                        nearestTeam = team;
                    }
                }

                if (nearestTeam.equals(IceWars.getTeam(p))) {
                    p.sendMessage(IceWars.PREFIX + "You can't destroy the ice block of your team.");
                    e.setCancelled(true);
                    return;
                }

                Bukkit.broadcastMessage(IceWars.PREFIX + "Team " + nearestTeam.getColor() + nearestTeam.getName() + "§7's ice block has been destroyed.");
                PlayerStats stats = IceWars.STATS.get(p.getName());
                stats.addDestroyedIceBlock();
                IceWars.STATS.put(p.getName(), stats);
            } else {
                if (!materials.contains(mat)) {
                    e.setCancelled(true);
                    p.sendMessage(IceWars.PREFIX + "You are not allowed to break that block.");
                }
            }
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        e.blockList().clear();
    }

    //TNT
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (IceWars.INGAME.contains(p)) {
            if (e.getBlock().getType() == Material.TNT) {
                e.setCancelled(true);
                p.getInventory().remove(new ItemStack(Material.TNT));
                e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation(), EntityType.PRIMED_TNT);
            }
        }
    }

    @EventHandler
    public void onFade(BlockFadeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFireSpread(BlockSpreadEvent e) {
        if (e.getBlock().getType() == Material.FIRE)
            e.setCancelled(true);
    }

    @EventHandler
    public void onLeaveDecay(LeavesDecayEvent e) {
        e.setCancelled(true);
    }

}
