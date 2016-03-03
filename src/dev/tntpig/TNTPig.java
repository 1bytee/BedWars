package dev.tntpig;

import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityTargetEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class TNTPig extends EntityPig implements ITNTPig {

    private Location loc;
    private Player p;
    private TNTPrimed tnt;

    public TNTPig(World world) {
        super(world);
    }

    @SneakyThrows
    public TNTPig(Location loc, Player p) {
        super((World) loc.getWorld());
        this.loc = loc;
        this.p = p;

        Field b = goalSelector.getClass().getDeclaredField("b");
        b.setAccessible(true);
        b.set(goalSelector, new ArrayList<>());
        getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(1280);
        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.4D);

        goalSelector.a(0, new PathfinderGoalPlayer(this, EntityHuman.class, 1D, false));
        setGoalTarget(((CraftPlayer) p).getHandle(), EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, false);
        ((Creature) getBukkitEntity()).setTarget(p);
    }

    @Override
    public Location getLocation() {
        return new Location(loc.getWorld(), locX, locY, locZ);
    }

    @Override
    public void setTNT(TNTPrimed tnt) {
        this.tnt = tnt;
    }

    @Override
    public TNTPrimed getTNT() {
        return tnt;
    }

    @Override
    public void setPassenger(TNTPrimed tnt) {
        getBukkitEntity().setPassenger(tnt);
    }

    @Override
    public void remove() {
        getBukkitEntity().remove();
    }

    @SneakyThrows
    @Override
    public void setTNTSource(Entity source) {
        if (source == null) {
            return;
        }

        Field f = EntityTNTPrimed.class.getDeclaredField("source");
        f.setAccessible(true);
        f.set(((CraftTNTPrimed) tnt).getHandle(), ((CraftEntity) source).getHandle());
    }

}
