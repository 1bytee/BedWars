package dev.api;

import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Ranks {

    public static String getGroup(Player p) {
        return getAsPermissionUser(p).getParentIdentifiers().get(0);
    }

    private static PermissionUser getAsPermissionUser(Player p) {
        return PermissionsEx.getUser(p);
    }

    public static String getPrefix(Player p) {
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
        } else if (group.equalsIgnoreCase("premium")) {
            prefix = "§6[Premium] §7";
        }
        return prefix;
    }

    public static String getColor(Player p) {
        String group = getGroup(p).toLowerCase();
        String color = "§7";
        if (group.equalsIgnoreCase("admin")) {
            color = "§4";
        } else if (group.equalsIgnoreCase("developer")) {
            color = "§b";
        } else if (group.equalsIgnoreCase("moderator")) {
            color = "§e";
        } else if (group.equalsIgnoreCase("vip")) {
            color = "§5";
        } else if (group.equalsIgnoreCase("premium")) {
            color = "§6";
        }
        return color;
    }

}
