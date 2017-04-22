package dev;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import dev.commands.*;
import dev.events.BlockListener;
import dev.events.PlayerListener;
import dev.events.SpectatorListener;
import dev.events.WorldListener;
import dev.map.MapManager;
import dev.map.MapReset;
import dev.stats.PlayerStats;
import dev.task.AbstractTask;
import dev.task.WarmupTask;
import dev.tntpig.TNTPigExecutor;
import dev.tntpig.TNTPigRegister;
import dev.util.*;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IceWars extends JavaPlugin {

    public static final String PREFIX = "§c[IceWars] §7";
    public static String SERVER;
    public static GameState STATE = GameState.WARMUP;
    public static String MAP = null;
    public static int MAPID = -1;
    public static boolean SETUPMODE;

    public static final List<Player> INGAME = Lists.newArrayList(), SPECTATING = Lists.newArrayList();
    public static AbstractTask CURRENT_TASK, ITEM_TASK;
    public static List<Location> goldSpawns = Lists.newArrayList();

    public static final Map<String, PlayerStats> STATS = Maps.newLinkedHashMap();
    public static final TNTPigRegister REGISTER = new TNTPigRegister();

    @Getter
    private static IceWars instance;

    private final ChatColor[] colors = new ChatColor[]{
            ChatColor.AQUA, ChatColor.GREEN, ChatColor.RED, ChatColor.YELLOW
    };

    @Getter
    private static TeamType type = TeamType.T2x2;
    @Getter
    private static List<Team> teams = Lists.newArrayList();

    @Override
    @SneakyThrows
    public void onEnable() {
        Class.forName("dev.util.MongoConnection");

        System.out.println("[IceWars] Loading all features...");
        if (!new File(getDataFolder(), "config.yml").exists())
            saveDefaultConfig();

        getConfig().addDefault("server", "IW0");
        getConfig().addDefault("setupMode", true);
        getConfig().options().copyDefaults(true);
        saveConfig();
        reloadConfig();

        SERVER = getConfig().getString("server");
        SETUPMODE = getConfig().getBoolean("setupMode");

        MongoCollection<Document> collection = MongoConnection.getCollection("server", "serverinfo");
        Document doc = new Document("name", SERVER)
                .append("status", "ONLINE")
                .append("type", "ICEWARS")
                .append("state", 1)
                .append("online", 0)
                .append("max", 0);
        collection.replaceOne(Filters.eq("name", SERVER), doc);

        registerEvents();
        registerCommands();
        MapManager.loadDefaultWorld();

        if (!SETUPMODE) {
            CURRENT_TASK = new WarmupTask();
        } else {
            MapManager.loadAllWorlds();
            type = TeamType.T2x2;
        }

        System.out.println("[IceWars] Features loaded.");
    }

    @Override
    public void onDisable() {
        MongoCollection<Document> collection = MongoConnection.getCollection("server", "serverinfo");
        Document doc = new Document("name", SERVER)
                .append("status", "OFFLINE")
                .append("type", "ICEWARS")
                .append("state", 0)
                .append("online", 0)
                .append("max", 0);
        collection.replaceOne(Filters.eq("name", SERVER), doc);
    }

    public void load() {

        teams.clear();

        Document info = MongoConnection.info();
        type = TeamType.of(info.getString("teamType"));
        MAPID = info.getInteger("map");
        MapManager.init();
        MAP = MapManager.getMapsById().get(MAPID);

        if (type == null) {
            getServer().getPluginManager().disablePlugin(this);
            System.out.println("[IceWars] Invalid TeamType: " + info.getString("teamType"));
            return;
        }

        for (int i = 0; i < type.getAmount(); i++) {
            teams.add(new Team(colors[i]));
        }

        goldSpawns = Locations.goldSpawns();

        //TODO fix this...
        Bukkit.getWorlds().forEach(world -> world.setPVP(true));
        Bukkit.getWorlds().forEach(world -> world.getEntities().stream().filter(e -> e.getType() != EntityType.WITCH && e.getType() != EntityType.PLAYER).forEach(Entity::remove));
    }

    @Override
    public void onLoad() {
        instance = this;

        System.out.println("[IceWars] loading world-reset...");
        MapReset manager = new MapReset();

        boolean backupsFound = false;
        File backupDir = new File(getDataFolder(), "backups");
        if (!backupDir.exists()) {
            System.out.println("[IceWars] No backup directory found! Creating...");
            backupDir.mkdirs();
        } else {
            System.out.println("[IceWars] Looking for world backups...");
            for (File f : backupDir.listFiles())
                if ((backupDir.isDirectory()) & (f.listFiles().length != 0))
                    backupsFound = true;
            if (!backupsFound) {
                System.out.println("[IceWars] No backups found or files are not directories?!");
            } else {
                System.out.println("[IceWars] Starting world transfer...");
                manager.importWorlds();
                System.out.println("[IceWars] Transfer complete!");
            }
        }
        System.out.println("[IceWars] Starting main system...");

    }

    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new WitchMenu(), this);
        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new BlockListener(), this);
        pm.registerEvents(new SpectatorListener(), this);
        pm.registerEvents(new SpectatorListener.SpectatorCompass(), this);
        pm.registerEvents(new TeamSelector(), this);
        pm.registerEvents(new TNTPigExecutor(), this);
        pm.registerEvents(new WorldListener(), this);
    }

    private void registerCommands() {
        getCommand("spawnwitch").setExecutor(new SpawnWitch());
        getCommand("setteamspawn").setExecutor(new SetTeamSpawn());
        getCommand("seticeblock").setExecutor(new SetIceBlock());
        getCommand("setlobbyspawn").setExecutor(new SetLobbySpawn());
        getCommand("world").setExecutor(new WorldCommand());
        getCommand("setitemspawn").setExecutor(new SetItemSpawn());
        getCommand("stats").setExecutor(new StatsCommand());
        getCommand("clearentities").setExecutor(new ClearEntities());
    }

    public static Team getTeam(Player p) {
        for (Team team : getTeams()) {
            if (team.getPlayers().contains(p)) {
                return team;
            }
        }
        return null;
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

    public static Team nextFreeTeam() {
        Team lowest = null;
        for (Team team : teams) {
            if (team == null && team.getPlayers().size() != type.getTeamSize()) {
                lowest = team;
            } else {
                if (team.getPlayers().size() < lowest.getPlayers().size() && team.getPlayers().size() != type.getTeamSize()) {
                    lowest = team;
                }
            }
        }
        return lowest == null ? new Team(ChatColor.BLACK) : lowest;
    }

    public static void setToSpectator(Player p) {
        Bukkit.getOnlinePlayers().stream().filter(o -> o != p).forEach(o -> o.hidePlayer(p));

        if (INGAME.contains(p)) {
            INGAME.remove(p);
        }
        SPECTATING.add(p);

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.setDisplayName("§eTeleporter");
        meta.setLore(Arrays.asList("", "§7Right-click to open inventory."));
        compass.setItemMeta(meta);

        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[4]);

        p.getInventory().addItem(compass);
        p.getInventory().setHeldItemSlot(0);
        p.setAllowFlight(true);
        p.setFlying(true);

        p.sendMessage(PREFIX + "You are now spectating.");
        p.sendMessage(PREFIX + "Use the compass to spectate other players!");

        Scoreboards.doIngameScoreboard();
    }

    private static final Map<String, UUID> uuidMap = Maps.newHashMap();
    private static final Gson gson = new Gson();

    @SneakyThrows
    public static UUID getUUID(String name) {
        if (uuidMap.containsKey(name)) {
            return uuidMap.get(name);
        } else {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openConnection();
            JsonObject object = gson.fromJson(IOUtils.toString(connection.getInputStream()), JsonObject.class);
            uuidMap.put(object.get("name").getAsString(), UUID.fromString(object.get("id").getAsString().replaceAll("(?i)(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w+)", "$1-$2-$3-$4-$5")));
            return uuidMap.get(object.get("name").getAsString());
        }
    }

    public static UUID getUUID(Player p) {
        return getUUID(p.getName());
    }

}
