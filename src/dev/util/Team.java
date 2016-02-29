package dev.util;

import com.google.common.collect.Lists;
import dev.IceWars;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public class Team {

    private List<Player> players;
    private final ChatColor color;
    private final String name;

    public Team(ChatColor color) {
        this.color = color;
        name = color.name().equals("AQUA") ? "Blue" : firstCharUppercase(color.name());
        players = Lists.newArrayList();
    }

    public void addPlayer(Player p) {
        if (players.size() < IceWars.getType().getTeamSize()) {
            players.add(p);
        }
    }

    public Location getSpawn() {
        return null;
    }

    private String firstCharUppercase(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1, string.length());
    }

}
