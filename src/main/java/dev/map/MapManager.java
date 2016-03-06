package dev.map;

import com.google.common.collect.Maps;
import dev.IceWars;
import dev.util.TeamType;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public final class MapManager {

    @Getter
    private static final HashMap<Integer, String> mapsById = Maps.newHashMap();
    @Getter
    private static final HashMap<String, Integer> mapsByName = Maps.newHashMap();
    @Getter
    private static final HashMap<Integer, World> worldsById = Maps.newHashMap();

    private static boolean loaded = false;

    @SneakyThrows
    public static void init() {
        File f = new File(IceWars.getInstance().getDataFolder(), "worldnames.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);

        for (TeamType type : TeamType.values()) {
            cfg.addDefault(type.name(), new String[]{
                    "Map1", "Map2", "Map3"
            });
        }
        cfg.options().copyDefaults(true);
        cfg.save(f);

        cfg = YamlConfiguration.loadConfiguration(f);

        List<String> worlds = cfg.getStringList(IceWars.getType().name());
        for (int i = 0; i < worlds.size(); i++) {
            mapsById.put(i + 1, worlds.get(i));
            mapsByName.put(worlds.get(i), i + 1);
        }

        loadWorlds();
    }

    @SneakyThrows
    public static void loadAllWorlds() {
        System.out.println("Hallo");
        File f = new File(IceWars.getInstance().getDataFolder(), "worldnames.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);

        for (TeamType type : TeamType.values()) {
            cfg.addDefault(type.name(), new String[]{
                    "Map1", "Map2", "Map3"
            });
        }
        cfg.options().copyDefaults(true);
        cfg.save(f);

        cfg = YamlConfiguration.loadConfiguration(f);
        for (TeamType type : TeamType.values()) {
            List<String> worlds = cfg.getStringList(type.name());
            int i = 1;
            for (String ignored : worlds) {
                String w = type.name() + "_Map" + i;
                Bukkit.createWorld(new WorldCreator(w));
                i++;
            }
        }
    }

    public static void loadWorlds() {
        if (loaded) throw new IllegalStateException("Worlds already loaded!");

        for (int i : getMapsById().keySet()) {
            String w = IceWars.getType().name() + "_Map" + i;
            World world = Bukkit.createWorld(new WorldCreator(w));
            worldsById.put(i, world);
        }

        loaded = true;
        System.out.println("[IceWars] " + getMapsById().size() + " worlds loaded.");
    }

    public static void loadDefaultWorld() {
        Bukkit.createWorld(new WorldCreator("Lobby"));
    }
}
