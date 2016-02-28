package dev.api;

import com.google.common.collect.Lists;
import dev.IceWars;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public class Team {

    private List<Player> players;
    private final ChatColor color;

    public Team(ChatColor color) {
        this.color = color;
        players = Lists.newArrayList();
    }

    public void addPlayer(Player p) {
        if (players.size() < IceWars.getType().getTeamSize()) {
            players.add(p);
        }
    }

}
