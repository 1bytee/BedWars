package dev;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import static org.bukkit.Material.*;

import java.util.List;

public class WitchMenu implements Listener {

    private List<Player> players = Lists.newArrayList();

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (e.getRightClicked().getType() == EntityType.WITCH) {
            openInventory(p);
        }
    }

    public void openInventory(Player p) {

        Inventory inv = Bukkit.createInventory(null, 9, "§cShopkeeper");

        inv.setItem(0, getItem(SANDSTONE, "§eBlocks"));
        inv.setItem(1, getItem(IRON_CHESTPLATE, "§bArmor"));
        inv.setItem(2, getItem(WOOD_PICKAXE, "§bPickaxes"));
        inv.setItem(3, getItem(IRON_SWORD, "§bSwords"));
        inv.setItem(4, getItem(BOW, "§bBows"));
        inv.setItem(5, getItem(APPLE, "§cFood"));
        inv.setItem(6, getItem(CHEST, "§eChests"));
        inv.setItem(7, getItem(POTION, "§ePotions"));
        inv.setItem(8, getItem(TNT, "§eSpecial"));

        p.openInventory(inv);
        players.add(p);
    }

    private ItemStack getItem(Material mat, String display) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getItem(Material mat, int amount, String display) {
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getItem(Material mat, String display, Enchantments... enchantments) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(display);
        for (Enchantments enchantment : enchantments) {
            meta.addEnchant(enchantment.e, enchantment.l, true);
        }
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack leather(Material mat, String display, Player p) {
        ItemStack item = new ItemStack(mat);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.spigot().setUnbreakable(true);
        meta.setColor(IceWars.getInstance().getColor(p));
        meta.setDisplayName(display);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            if (e.getCurrentItem() == null)
                return;
            if (players.contains(p)) {
                e.setCancelled(true);
                p.updateInventory();

                if (e.getCurrentItem().getType() == SANDSTONE) {
                    openBlocks(p);
                } else if (e.getCurrentItem().getType() == IRON_CHESTPLATE) {
                    openArmor(p);
                } else if (e.getCurrentItem().getType() == WOOD_PICKAXE) {

                }
            }
        }
    }

    public void openBlocks(Player p) {
        TradingInventory inv = new TradingInventory("§eBlocks");
        inv.addTrade(getItem(CLAY_BRICK, "§cBronze"), getItem(SANDSTONE, 2, "§eSandstone"));
        inv.addTrade(getItem(CLAY_BRICK, 7, "§cBronze"), getItem(ENDER_STONE, "§eEnd Stone"));
        inv.addTrade(getItem(IRON_INGOT, 3, "§7Silver"), getItem(IRON_BLOCK, "§eIron Block"));
        inv.addTrade(getItem(CLAY_BRICK, 15, "§cBronze"), getItem(GLOWSTONE, 4, "§eGlowstone"));
        inv.addTrade(getItem(CLAY_BRICK, 4, "§cBronze"), getItem(GLASS, "§eGlass"));
        inv.openTrade(p);
    }

    public void openArmor(Player p) {
        TradingInventory inv = new TradingInventory("§bArmor");
        inv.addTrade(getItem(CLAY_BRICK, "§cBronze"), leather(LEATHER_HELMET, "§bLeather Helmet", p));
        inv.addTrade(getItem(CLAY_BRICK, "§cBronze"), leather(LEATHER_LEGGINGS, "§bLeather Leggings", p));
        inv.addTrade(getItem(CLAY_BRICK, "§cBronze"), leather(LEATHER_BOOTS, "§bLeather Boots", p));
        inv.addTrade(getItem(IRON_INGOT, "§7Silver"), getItem(CHAINMAIL_CHESTPLATE, "§bChainmail Chestplate Lvl 1",
                Enchantments.builder().e(Enchantment.PROTECTION_PROJECTILE).l(1).build(),
                Enchantments.builder().e(Enchantment.DURABILITY).l(1).build()));
        inv.addTrade(getItem(IRON_INGOT, 3, "§7Silver"), getItem(CHAINMAIL_CHESTPLATE, "§bChainmail Chestplate Lvl 2",
                Enchantments.builder().e(Enchantment.PROTECTION_PROJECTILE).l(2).build(),
                Enchantments.builder().e(Enchantment.DURABILITY).l(1).build()));
        inv.addTrade(getItem(IRON_INGOT, 7, "§7Silver"), getItem(CHAINMAIL_CHESTPLATE, "§bChainmail Chestplate Lvl 3",
                Enchantments.builder().e(Enchantment.PROTECTION_PROJECTILE).l(2).build(),
                Enchantments.builder().e(Enchantment.DURABILITY).l(1).build()));
        inv.openTrade(p);
    }

    public void openPickaxe(Player p) {
        TradingInventory inv = new TradingInventory("§bPickaxes");
    }

    @Builder
    @NoArgsConstructor
    @Getter
    private class Enchantments {

        private Enchantment e;
        private int l;

    }

    private class TradingInventory {

        private String invname;
        private MerchantRecipeList l = new MerchantRecipeList();

        public TradingInventory(String invname) {
            this.invname = invname;
        }

        public TradingInventory addTrade(ItemStack in, ItemStack out) {
            MerchantRecipe recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(in), CraftItemStack
                    .asNMSCopy(out));
            recipe.a(999999999);
            l.add(recipe);

            return this;
        }

        public void openTrade(Player who) {
            final EntityHuman e = ((CraftPlayer) who).getHandle();
            e.openTrade(new IMerchant() {
                @Override
                public MerchantRecipeList getOffers(EntityHuman arg0) {
                    return l;
                }

                @Override
                public void a_(net.minecraft.server.v1_8_R3.ItemStack arg0) {
                }

                @Override
                public void a_(EntityHuman arg0) {
                }

                @Override
                public IChatBaseComponent getScoreboardDisplayName() {
                    return IChatBaseComponent.ChatSerializer.a(invname);
                }

                @Override
                public EntityHuman v_() {
                    return e;
                }

                @Override
                public void a(MerchantRecipe arg0) {
                }
            });
        }

    }

}
