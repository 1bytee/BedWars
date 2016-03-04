package dev.util;

import com.google.common.collect.Lists;
import dev.IceWars;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false, exclude = {"players", "iceBlock"})
public class Team {

    private List<Player> players;
    private final ChatColor color;
    private final String name;
    private final Block iceBlock;

    public Team(ChatColor color) {
        this.color = color;
        iceBlock = iceblock(this);
        name = color.name().equals("AQUA") ? "Blue" : firstCharUppercase(color.name());
        players = Lists.newArrayList();
    }

    public void addPlayer(Player p) {
        if (players.size() < IceWars.getType().getTeamSize()) {
            players.add(p);
        }
    }

    public void removePlayer(Player p) {
        players.remove(p);
    }

    public void sendMessage(String message) {
        players.forEach(player -> player.sendMessage(message));
    }

    public boolean hasIceblock() {
        return iceBlock.getType() == Material.ICE;
    }

    private static String firstCharUppercase(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1, string.length());
    }

    private static Block iceblock(Team team) {
        String path = IceWars.MAP + "." + team.name + ".iceblock";
        File f = new File(IceWars.getInstance().getDataFolder(), IceWars.getType().name().toLowerCase() + ".yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        int x = cfg.getInt(path + "X");
        int y = cfg.getInt(path + "Y");
        int z = cfg.getInt(path + "Z");
        World world = Bukkit.getWorld(cfg.getString(path + "W"));
        return world.getBlockAt(x, y, z);
    }

}
