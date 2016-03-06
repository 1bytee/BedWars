package dev.tntpig;

import dev.IceWars;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import net.minecraft.server.v1_8_R3.EntityTypes;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.HashMap;

public class TNTPigRegister {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void registerEntities(int entityId) {
        try {
            Class<?> entityTypeClass = EntityTypes.class;

            Field c = entityTypeClass.getDeclaredField("c");
            c.setAccessible(true);
            HashMap c_map = (HashMap) c.get(null);
            c_map.put("TNTPig", TNTPig.class);

            Field d = entityTypeClass.getDeclaredField("d");
            d.setAccessible(true);
            HashMap d_map = (HashMap) d.get(null);
            d_map.put(TNTPig.class, "TNTPig");

            Field e = entityTypeClass.getDeclaredField("e");
            e.setAccessible(true);
            HashMap e_map = (HashMap) e.get(null);
            e_map.put(entityId, TNTPig.class);

            Field f = entityTypeClass.getDeclaredField("f");
            f.setAccessible(true);
            HashMap f_map = (HashMap) f.get(null);
            f_map.put(TNTPig.class, entityId);

            Field g = entityTypeClass.getDeclaredField("g");
            g.setAccessible(true);
            HashMap g_map = (HashMap) g.get(null);
            g_map.put("TNTPig", entityId);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ITNTPig spawnCreature(final TNTPigExecutor item, final Location location, final Player owner, Player target) {
        final TNTPig pig = new TNTPig(location, target);

        ((CraftWorld) location.getWorld()).getHandle().addEntity(pig, CreatureSpawnEvent.SpawnReason.NATURAL);
        pig.setPosition(location.getX(), location.getY(), location.getZ());

        new BukkitRunnable() {

            @Override
            public void run() {
                TNTPrimed primedTnt = (TNTPrimed) location.getWorld().spawnEntity(location.add(0.0, 1.0, 0.0), EntityType.PRIMED_TNT);
                pig.getBukkitEntity().setPassenger(primedTnt);
                pig.setTNT(primedTnt);

                try {
                    Field sourceField = EntityTNTPrimed.class.getDeclaredField("source");
                    sourceField.setAccessible(true);
                    sourceField.set(((CraftTNTPrimed) primedTnt).getHandle(), ((CraftLivingEntity) owner).getHandle());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                pig.getTNT().setFuseTicks(Math.round(8 * 20));
                pig.getTNT().setIsIncendiary(false);
                item.updateTNT();
            }
        }.runTaskLater(IceWars.getInstance(), 5L);

        return pig;
    }


}
