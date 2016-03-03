package dev.tntpig;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;

public interface ITNTPig {

    Location getLocation();
    void setTNT(TNTPrimed tnt);
    TNTPrimed getTNT();
    void setPassenger(TNTPrimed tnt);
    void remove();
    void setTNTSource(Entity source);

}
