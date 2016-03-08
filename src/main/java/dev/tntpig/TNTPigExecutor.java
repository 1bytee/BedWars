package dev.tntpig;

import dev.IceWars;
import dev.util.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class TNTPigExecutor implements Listener {

    static {
        IceWars.REGISTER.registerEntities(90);
    }

    private ITNTPig pig;

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {

            if (p.getItemInHand().getType() != Material.MONSTER_EGG)
                return;

            ItemStack used = p.getItemInHand().clone();
            used.setAmount(1);
            p.getInventory().removeItem(used);
            Player target = findTargetPlayer(p);
            if (target == null) {
                p.sendMessage(IceWars.PREFIX + "There is no target available.");
                return;
            }

            Location start = p.getLocation();

            new BukkitRunnable() {

                @Override
                public void run() {
                    pig = IceWars.REGISTER.spawnCreature(TNTPigExecutor.this, start, p, target);

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            pig.getTNT().remove();
                            pig.remove();
                        }

                    }.runTaskLater(IceWars.getInstance(), 8 * 20 + 13);

                }

            }.runTask(IceWars.getInstance());
        }

    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof ITNTPig) {
            e.setCancelled(true);
            return;
        }

        if (e.getEntity() instanceof Player && e.getDamager() instanceof TNTPrimed) {
            Player damaged = (Player) e.getEntity();
            if (IceWars.getTeam(damaged).equals(IceWars.getTeam((Player) ((TNTPrimed) e.getDamager()).getSource()))) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof ITNTPig || (e.getRightClicked().getVehicle() != null && e.getRightClicked().getVehicle() instanceof ITNTPig)) {
            e.setCancelled(true);
        }
    }

    private Player findTargetPlayer(Player player) {
        Player foundPlayer = null;
        double distance = Double.MAX_VALUE;

        Team playerTeam = IceWars.getTeam(player);

        ArrayList<Player> possibleTargets = new ArrayList<>();
        IceWars.getTeams().forEach(team -> possibleTargets.addAll(team.getPlayers()));
        possibleTargets.removeAll(playerTeam.getPlayers());

        for (Player p : possibleTargets) {
            double dist = player.getLocation().distance(p.getLocation());
            if (dist < distance) {
                foundPlayer = p;
                distance = dist;
            }
        }

        return foundPlayer;
    }

    public void updateTNT() {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (pig == null || pig.getTNT() == null) {
                    return;
                }

                TNTPrimed old = pig.getTNT();
                final int fuse = old.getFuseTicks();

                if (fuse <= 0) {
                    return;
                }

                final Entity source = old.getSource();
                final Location oldLoc = old.getLocation();
                final float yield = old.getYield();
                old.leaveVehicle();
                old.remove();

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        TNTPrimed primed = pig.getLocation().getWorld().spawn(oldLoc, TNTPrimed.class);
                        primed.setFuseTicks(fuse);
                        primed.setYield(yield);
                        primed.setIsIncendiary(false);
                        pig.setPassenger(primed);
                        pig.setTNT(primed);
                        pig.setTNTSource(source);

                        if (primed.getFuseTicks() >= 60) {
                            updateTNT();
                        }

                    }

                }.runTaskLater(IceWars.getInstance(), 3L);


            }

        }.runTaskLater(IceWars.getInstance(), 60L);
    }
}
