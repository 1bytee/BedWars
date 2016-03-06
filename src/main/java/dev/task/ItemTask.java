package dev.task;

import dev.IceWars;
import dev.util.Team;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemTask extends AbstractTask {

    private int iron = 0;
    private int gold = 0;

    @Override
    public void run() {
        if (gold < 45) {
            gold++;
        } else {
            IceWars.goldSpawn.getWorld().dropItemNaturally(IceWars.goldSpawn.add(0, 0.2D, 0), getItem(Material.GOLD_INGOT, "§6Gold"));
            gold = 0;
        }
        if (iron < 10) {
            iron++;
        } else {
            for (Team team : IceWars.getTeams()) {
                team.getSilverSpawn().getWorld().dropItemNaturally(team.getSilverSpawn().add(0, 0.2D, 0), getItem(Material.IRON_INGOT, "§7Silver"));
            }
            iron = 0;
        }
        for (Team team : IceWars.getTeams()) {
            team.getBronzeSpawn().getWorld().dropItemNaturally(team.getBronzeSpawn().add(0, 0.2D, 0), getItem(Material.CLAY_BRICK, "§cBronze"));
        }
    }

    private ItemStack getItem(Material mat, String display) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display);
        item.setItemMeta(meta);
        return item;
    }

}
