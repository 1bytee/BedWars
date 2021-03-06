package dev.util;

import com.google.common.collect.Lists;
import dev.IceWars;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

@Getter
@EqualsAndHashCode(exclude = {"players", "iceBlock"})
@ToString
public class Team {

    private List<Player> players;
    private final ChatColor color;
    private final String name;
    private final Block iceBlock;
    private Location bronzeSpawn;
    private Location silverSpawn;

    public Team(ChatColor color) {
        this.color = color;
        name = color.name().equals("AQUA") ? "Blue" : firstCharUppercase(color.name());
        players = Lists.newArrayList();
        if (color == ChatColor.BLACK) {
            iceBlock = null;
            bronzeSpawn = null;
            silverSpawn = null;
            return;
        }
        iceBlock = iceBlock(this);
        bronzeSpawn = bronzeSpawn(this);
        silverSpawn = silverSpawn(this);
    }

    public boolean addPlayer(Player p) {
        if (players.size() < IceWars.getType().getTeamSize()) {
            players.add(p);
            return true;
        }
        return false;
    }

    public void removePlayer(Player p) {
        players.remove(p);
    }

    public void sendMessage(String message) {
        players.forEach(player -> player.sendMessage(message));
    }

    public boolean hasIceblock() {
        return iceBlock.getType() == Material.PACKED_ICE;
    }

    private static String firstCharUppercase(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1, string.length()).toLowerCase();
    }

    private static Location silverSpawn(Team team) {
        String path = IceWars.MAPID + "." + team.name + ".silver.";
        File f = new File(IceWars.getInstance().getDataFolder(), IceWars.getType().name().toLowerCase() + ".yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        int x = cfg.getInt(path + "X");
        int y = cfg.getInt(path + "Y");
        int z = cfg.getInt(path + "Z");
        World world = Bukkit.getWorld(cfg.getString(path + "W"));
        return new Location(world, x, y, z);
    }

    private static Location bronzeSpawn(Team team) {
        String path = IceWars.MAPID + "." + team.name + ".bronze.";
        File f = new File(IceWars.getInstance().getDataFolder(), IceWars.getType().name().toLowerCase() + ".yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        int x = cfg.getInt(path + "X");
        int y = cfg.getInt(path + "Y");
        int z = cfg.getInt(path + "Z");
        World world = Bukkit.getWorld(cfg.getString(path + "W"));
        return new Location(world, x, y, z);
    }

    private static Block iceBlock(Team team) {
        System.out.println(team);
        String path = IceWars.MAPID + "." + team.name + ".iceblock.";
        System.out.println(path);
        File f = new File(IceWars.getInstance().getDataFolder(), IceWars.getType().name().toLowerCase() + ".yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        int x = cfg.getInt(path + "X");
        int y = cfg.getInt(path + "Y");
        int z = cfg.getInt(path + "Z");
        World world = Bukkit.getWorld(cfg.getString(path + "W"));
        return world.getBlockAt(x, y, z);
    }

}
