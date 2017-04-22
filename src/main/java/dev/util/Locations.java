package dev.util;

import com.google.common.collect.Lists;
import dev.IceWars;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.NumberConversions;

import java.io.File;
import java.util.List;

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
        if (cfg.get(name.toLowerCase() + ".X") == null) {
            return null;
        }
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

    public static Location getSpawn(Team team) {
        String path = IceWars.MAPID + "." + team.getName() + ".";
        File f = new File(IceWars.getInstance().getDataFolder(), IceWars.getType().name().toLowerCase() + ".yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        double x = cfg.getDouble(path + "X");
        double y = cfg.getDouble(path + "Y");
        double z = cfg.getDouble(path + "Z");
        float yaw = NumberConversions.toFloat(cfg.get(path + "yaw"));
        float pitch = NumberConversions.toFloat(cfg.get(path + "pitch"));
        World world = Bukkit.getWorld(cfg.getString(path + "W"));
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static List<Location> goldSpawns() {
        String path = IceWars.MAPID + ".gold.";
        File f = new File(IceWars.getInstance().getDataFolder(), IceWars.getType().name().toLowerCase() + ".yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        List<Location> locations = Lists.newArrayList();
        for (int i = 1; i <= 10; i++) {
            if (!cfg.isSet(path + i + ".X")) {
                break;
            }
            int x = cfg.getInt(path + i + ".X");
            int y = cfg.getInt(path + i + ".Y");
            int z = cfg.getInt(path + i + ".Z");
            World world = Bukkit.getWorld(cfg.getString(path + "W"));
            Location loc = new Location(world, x, y, z);
            locations.add(loc);
        }
        return locations;
    }
}
