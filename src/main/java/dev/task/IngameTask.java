package dev.task;

import dev.IceWars;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class IngameTask extends AbstractTask {

    private int cooldown = 30 * 60;

    @Override
    public void run() {
        if (cooldown != 0) {
            if (cooldown == 30 * 60 || cooldown == 25 * 60 || cooldown == 20 * 60 || cooldown == 15 * 60 || cooldown == 10 * 60 || cooldown == 5 * 60) {
                sendBar("§7Time left: §e" + cooldown / 60 + " §7minutes");
            }
            if (cooldown == 120 || cooldown == 60) {
                broadcast("Game ending in §e" + cooldown / 60 + " §7minute" + (cooldown / 60 == 1 ? "." : "s."));
            }
            if (cooldown == 30 || cooldown == 20 || cooldown == 10) {
                broadcast("Game ending in §e" + cooldown + " §7seconds.");
            }
            if (cooldown <= 5 && cooldown >= 1) {
                broadcast("Game ending in §e" + cooldown + " §7second" + (cooldown == 1 ? "." : "s."));
            }
            cooldown--;
        } else {
            RestartTask.execute();
            cancel();
            IceWars.CURRENT_TASK = null;
        }
    }

    public void sendBar(String content) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            CraftPlayer cp = (CraftPlayer) p;
            PacketPlayOutChat chat = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + content + "\"}"), (byte) 2);
            cp.getHandle().playerConnection.sendPacket(chat);
        }
    }
}
