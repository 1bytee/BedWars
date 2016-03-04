package dev.task;

import dev.IceWars;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;

public abstract class AbstractTask implements Runnable {

    @Getter(AccessLevel.PROTECTED)
    private final int PID;

    protected AbstractTask() {
        PID = Bukkit.getScheduler().scheduleSyncRepeatingTask(IceWars.getInstance(), this, 0, 20L);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(PID);
    }

    protected void broadcast(String message) {
        Bukkit.broadcastMessage(IceWars.PREFIX + message);
    }

}
