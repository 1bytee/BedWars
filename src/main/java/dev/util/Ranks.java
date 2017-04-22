package dev.util;

import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import dev.IceWars;
import dev.NameTag;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class Ranks {
    private static final Map<Player, String> GROUP = Maps.newHashMap();

    public static String getGroup(Player p) {

        if (GROUP.containsKey(p)) {
            return GROUP.get(p);
        }

        UUID uuid = IceWars.getUUID(p);
        MongoCollection<Document> collection = MongoConnection.getCollection("perms", "users_in_groups");

        String group = collection.find(eq("uuid", uuid.toString())).first().getString("group");
        GROUP.put(p, group);

        return group;
    }

    public static String getPrefix(Player p) {

        if (NameTag.NICKED.containsKey(p)) {
            if (NameTag.NICKED.get(p) == NameTag.NickType.PREMIUM) {
                return "§6[Premium] §7";
            } else if (NameTag.NICKED.get(p) == NameTag.NickType.PLAYER) {
                return "§7";
            }
        }

        String group = getGroup(p).toLowerCase();
        String prefix = "§7";
        if (group.equalsIgnoreCase("admin")) {
            prefix = "§4[Admin] §7";
        } else if (group.equalsIgnoreCase("developer")) {
            prefix = "§b[Dev] §7";
        } else if (group.equalsIgnoreCase("moderator")) {
            prefix = "§e[Mod] §7";
        } else if (group.equalsIgnoreCase("vip")) {
            prefix = "§5[VIP] §7";
        } else if (group.startsWith("premium")) {
            prefix = "§6[Premium] §7";
        } else if (group.equalsIgnoreCase("srmoderator")) {
            prefix = "§e[SrMod] §7";
        } else if (group.equalsIgnoreCase("builder")) {
            prefix = "§a[Builder] §7";
        }
        return prefix;
    }

    public static String getColor(Player p) {

        if (NameTag.NICKED.containsKey(p)) {
            if (NameTag.NICKED.get(p) == NameTag.NickType.PREMIUM) {
                return "§6";
            } else if (NameTag.NICKED.get(p) == NameTag.NickType.PLAYER) {
                return "§7";
            }
        }

        String group = getGroup(p).toLowerCase();
        String color = "§7";
        if (group.equalsIgnoreCase("admin")) {
            color = "§4";
        } else if (group.equalsIgnoreCase("developer")) {
            color = "§b";
        } else if (group.equalsIgnoreCase("moderator") || group.equalsIgnoreCase("srmoderator")) {
            color = "§e";
        } else if (group.equalsIgnoreCase("vip")) {
            color = "§5";
        } else if (group.startsWith("premium")) {
            color = "§6";
        } else if (group.equalsIgnoreCase("builder")) {
            color = "§a";
        }
        return color;
    }

    public static String getColorIgnoreNick(Player p) {
        String group = getGroup(p).toLowerCase();
        String color = "§7";
        if (group.equalsIgnoreCase("admin")) {
            color = "§4";
        } else if (group.equalsIgnoreCase("developer")) {
            color = "§b";
        } else if (group.equalsIgnoreCase("moderator") || group.equalsIgnoreCase("srmoderator")) {
            color = "§e";
        } else if (group.equalsIgnoreCase("vip")) {
            color = "§5";
        } else if (group.startsWith("premium")) {
            color = "§6";
        } else if (group.equalsIgnoreCase("builder")) {
            color = "§a";
        }
        return color;

    }
}
