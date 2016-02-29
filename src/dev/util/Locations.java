package dev.util;

import dev.IceWars;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.NumberConversions;

import java.io.File;

public class Locations {

    public static FileConfiguration getConfiguration(String name) {
        return YamlConfiguration.loadConfiguration(new File(IceWars.getInstance().getDataFolder(), name + ".yml"));
    }

    @SneakyThrows
    public static void saveConfiguration(String name, FileConfiguration cfg) {
        cfg.save(new File(IceWars.getInstance().getDataFolder(), name + ".yml"));
    }

    @SneakyThrows(NullPointerException.class)
    public static Location getLocation(String name) {
        FileConfiguration cfg = getConfiguration("locations");
        double x = cfg.getDouble(name.toLowerCase() + ".X");
        double y = cfg.getDouble(name.toLowerCase() + ".Y");
        double z = cfg.getDouble(name.toLowerCase() + ".Z");
        float yaw = NumberConversions.toFloat(cfg.get(name.toLowerCase() + ".yaw"));
        float pitch = NumberConversions.toFloat(cfg.get(name.toLowerCase() + ".pitch"));
        World world = Bukkit.getWorld(cfg.getString(name.toLowerCase() + ".W"));
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static void saveLocation(Location loc, String name) {
        FileConfiguration cfg = getConfiguration("locations");
        cfg.set(name.toLowerCase() + ".X", loc.getX());
        cfg.set(name.toLowerCase() + ".Y", loc.getY());
        cfg.set(name.toLowerCase() + ".Z", loc.getZ());
        cfg.set(name.toLowerCase() + ".yaw", loc.getYaw());
        cfg.set(name.toLowerCase() + ".pitch", loc.getPitch());
        cfg.set(name.toLowerCase() + ".W", loc.getWorld().getName());
        saveConfiguration("locations", cfg);
    }

}
