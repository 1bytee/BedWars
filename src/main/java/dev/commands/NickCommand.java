package dev.commands;

import dev.NameTag;
import dev.UUIDFetcher;
import dev.util.Scoreboards;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("llio.nick")) {
            p.sendMessage("§c[LLIO] §7You are not allowed to execute this command.");
            return true;
        }

        if (NameTag.NICKED.containsKey(p)) {
            NameTag.NICKED.remove(p);
            NameTag.getInstance().refreshPlayer(p);
            p.setDisplayName(p.getName());
            Scoreboards.doScoreboard();
            p.sendMessage("§c[Nick] §7Your nick has been removed.");
        } else {
            NameTag.NICKED.put(p, NameTag.NickType.PREMIUM);

            String nick = null;

            boolean found = false;
            while (!found) {
                String search = NameTag.getInstance().getNICKS().get(NameTag.getInstance().getRandom().nextInt(NameTag.getInstance().getNICKS().size()));
                if (!NameTag.getInstance().getUSED().contains(search)) {
                    nick = search;
                    NameTag.getInstance().getUSED().add(search);
                    found = true;
                }
            }

            UUIDFetcher.getUUID(nick);
            p.setDisplayName(nick);
            NameTag.getInstance().refreshPlayer(p);
            Scoreboards.doScoreboard();
            p.sendMessage("§c[Nick] §7You are now nicked as §6" + nick + "§7.");
        }
        return true;
    }

}
