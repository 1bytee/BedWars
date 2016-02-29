package dev;

import com.google.common.collect.Lists;
import dev.commands.SetIceBlock;
import dev.util.GameState;
import dev.util.Team;
import dev.util.TeamType;
import dev.commands.SetTeamSpawn;
import dev.commands.SpawnWitchCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class IceWars extends JavaPlugin {

    public static final String PREFIX = "§c[IceWars] §7";
    public static GameState STATE = GameState.WARMUP;

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

        registerEvents();
        registerCommands();

    }

    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new WitchMenu(), this);
    }

    private void registerCommands() {
        getCommand("spawnwitch").setExecutor(new SpawnWitchCommand());
        getCommand("setteamspawn").setExecutor(new SetTeamSpawn());
        getCommand("seticeblock").setExecutor(new SetIceBlock());
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
