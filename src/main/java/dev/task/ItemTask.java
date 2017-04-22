package dev.task;

import dev.IceWars;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static org.bukkit.Material.*;

public class ItemTask extends AbstractTask {

    private int iron = 0;
    private int gold = 0;

    public ItemTask() {
        super(30);
    }

    @Override
    public void run() {
        if (gold < 34) {
            gold++;
        } else {
            IceWars.goldSpawns.forEach(location -> location.getWorld().dropItemNaturally(location, getItem(GOLD_INGOT, "§6Gold")));
            gold = 0;
        }
        if (iron < 8) {
            iron++;
        } else {
            IceWars.getTeams().forEach(team -> team.getSilverSpawn().getWorld().dropItemNaturally(team.getSilverSpawn(), getItem(IRON_INGOT, "§7Silver")));
            iron = 0;
        }
        IceWars.getTeams().forEach(team -> team.getBronzeSpawn().getWorld().dropItemNaturally(team.getBronzeSpawn(), getItem(CLAY_BRICK, "§cBronze")));
    }

    private ItemStack getItem(Material mat, String display) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display);
        item.setItemMeta(meta);
        return item;
    }

}
