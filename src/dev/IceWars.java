package dev;

import com.google.common.collect.Lists;
import dev.api.Team;
import dev.api.TeamType;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class IceWars extends JavaPlugin {

    public static final String PREFIX = "§c[IceWars] §7";
    @Getter
    private static IceWars instance;
    private final ChatColor[] colors = new ChatColor[] {
            ChatColor.AQUA, ChatColor.GREEN, ChatColor.RED, ChatColor.YELLOW
    };

    @Getter
    private static TeamType type = TeamType.T2x2;
    @Getter
    private static List<Team> teams = Lists.newArrayList();

    @Override
    public void onEnable() {
        instance = this;
        if (!new File(getDataFolder(), "config.yml").exists())
            saveDefaultConfig();

        getConfig().addDefault("type", "2x2");
        getConfig().options().copyDefaults(true);
        saveConfig();
        reloadConfig();
        type = TeamType.of(getConfig().getString("type"));

        if (type == null) {
            getServer().getPluginManager().disablePlugin(this);
            System.out.println("[IceWars] Invalid teamtype: " + getConfig().getString("type"));
        }

        for (int i = 0; i < type.getTeamSize(); i++) {
            teams.add(new Team(colors[i]));
        }

    }

    public Color getColor(Player p) {
        for (Team team : getTeams()) {
            if (team.getPlayers().contains(p)) {
                switch (team.getColor()) {
                    case AQUA:
                        return Color.AQUA;
                    case GREEN:
                        return Color.GREEN;
                    case RED:
                        return Color.RED;
                    case YELLOW:
                        return Color.YELLOW;
                    default:
                        break;
                }
            }
        }
        return null;
    }

}
