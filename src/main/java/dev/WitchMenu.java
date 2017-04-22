package dev;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Getter;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;

import static org.bukkit.Material.*;

import org.bukkit.Material;

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

    private ItemStack getItem(Material mat, String display, int durability) {
        ItemStack item = new ItemStack(mat, 1, (short) durability);
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
                    players.remove(p);
                    openBlocks(p);
                } else if (e.getCurrentItem().getType() == IRON_CHESTPLATE) {
                    players.remove(p);
                    openArmor(p);
                } else if (e.getCurrentItem().getType() == WOOD_PICKAXE) {
                    players.remove(p);
                    openPickaxe(p);
                } else if (e.getCurrentItem().getType() == IRON_SWORD) {
                    players.remove(p);
                    openSwords(p);
                } else if (e.getCurrentItem().getType() == BOW) {
                    players.remove(p);
                    openBows(p);
                } else if (e.getCurrentItem().getType() == APPLE) {
                    players.remove(p);
                    openFood(p);
                } else if (e.getCurrentItem().getType() == CHEST) {
                    players.remove(p);
                    openChests(p);
                } else if (e.getCurrentItem().getType() == POTION) {
                    players.remove(p);
                    openPotions(p);
                } else if (e.getCurrentItem().getType() == TNT) {
                    players.remove(p);
                    openSpecial(p);
                }
            }
        }
    }

    @EventHandler
    public void onKick(PlayerQuitEvent e) {
        closeInventory(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        closeInventory(e.getPlayer());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (players.contains(p)) {
            players.remove(p);
        }
    }

    private void closeInventory(Player p) {
        if (players.contains(p)) {
            players.remove(p);
            p.closeInventory();
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
        inv.addTrade(getItem(CLAY_BRICK, 4, "§cBronze"), getItem(Material.WOOD_PICKAXE, "§bWood Pickaxe",
                Enchantments.builder().e(Enchantment.DIG_SPEED).l(1).build(),
                Enchantments.builder().e(Enchantment.DURABILITY).l(1).build()
        ));
        inv.addTrade(getItem(IRON_INGOT, 2, "§7Silver"), getItem(Material.STONE_PICKAXE, "§bStone Pickaxe",
                Enchantments.builder().e(Enchantment.DIG_SPEED).l(1).build(),
                Enchantments.builder().e(Enchantment.DURABILITY).l(1).build()
        ));
        inv.addTrade(getItem(GOLD_INGOT, "§6Gold"), getItem(IRON_PICKAXE, "bIron Pickaxe",
                Enchantments.builder().e(Enchantment.DIG_SPEED).l(3).build(),
                Enchantments.builder().e(Enchantment.DURABILITY).l(1).build()
        ));
        inv.openTrade(p);
    }

    public void openSwords(Player p) {
        TradingInventory inv = new TradingInventory("§bPickaxes");
        inv.addTrade(getItem(CLAY_BRICK, 8, "§cBronze"), getItem(STICK, "§bKnockback Stick",
                Enchantments.builder().e(Enchantment.KNOCKBACK).l(3).build()
        ));
        inv.addTrade(getItem(IRON_INGOT, "§7Silver"), getItem(GOLD_SWORD, "§bGold Sword Lvl 1",
                Enchantments.builder().e(Enchantment.DAMAGE_ALL).l(1).build(),
                Enchantments.builder().e(Enchantment.DURABILITY).l(1).build()
        ));
        inv.addTrade(getItem(IRON_INGOT, 3, "§7Silver"), getItem(GOLD_SWORD, "§bGold Sword Lvl 2",
                Enchantments.builder().e(Enchantment.DAMAGE_ALL).l(2).build(),
                Enchantments.builder().e(Enchantment.DURABILITY).l(1).build()
        ));
        inv.addTrade(getItem(GOLD_INGOT, 5, "§6Gold"), getItem(IRON_SWORD, "§bIron Sword",
                Enchantments.builder().e(Enchantment.DAMAGE_ALL).l(3).build(),
                Enchantments.builder().e(Enchantment.DURABILITY).l(1).build(),
                Enchantments.builder().e(Enchantment.KNOCKBACK).l(1).build()
        ));
        inv.openTrade(p);
    }

    public void openBows(Player p) {
        TradingInventory inv = new TradingInventory("§bBows");
        inv.addTrade(getItem(GOLD_INGOT, 3, "§6Gold"), getItem(BOW, "§bBow Lvl 1",
                Enchantments.builder().e(Enchantment.ARROW_INFINITE).l(1).build()
        ));
        inv.addTrade(getItem(GOLD_INGOT, 7, "§6Gold"), getItem(BOW, "§bBow Lvl 2",
                Enchantments.builder().e(Enchantment.ARROW_DAMAGE).l(1).build(),
                Enchantments.builder().e(Enchantment.ARROW_INFINITE).l(1).build()
        ));
        inv.addTrade(getItem(GOLD_INGOT, 13, "§6Gold"), getItem(BOW, "§bBow Lvl 3",
                Enchantments.builder().e(Enchantment.ARROW_DAMAGE).l(1).build(),
                Enchantments.builder().e(Enchantment.ARROW_KNOCKBACK).l(1).build(),
                Enchantments.builder().e(Enchantment.ARROW_INFINITE).l(1).build()
        ));
        inv.addTrade(getItem(GOLD_INGOT, "§6Gold"), getItem(ARROW, "§bArrow"));
        inv.openTrade(p);
    }

    public void openFood(Player p) {
        TradingInventory inv = new TradingInventory("§cFood");
        inv.addTrade(getItem(CLAY_BRICK, "§cBronze"), getItem(APPLE, "§cApple"));
        inv.addTrade(getItem(CLAY_BRICK, 2, "§cBronze"), getItem(GRILLED_PORK, "§cCooked Porkchop"));
        inv.addTrade(getItem(IRON_INGOT, "§7Silver"), getItem(CAKE, "§cCake"));
        inv.addTrade(getItem(GOLD_INGOT, 2, "§6Gold"), getItem(GOLDEN_APPLE, "§cGolden Apple"));
        inv.openTrade(p);
    }

    public void openChests(Player p) {
        TradingInventory inv = new TradingInventory("§eChests");
        inv.addTrade(getItem(IRON_INGOT, "§7Silver"), getItem(CHEST, "§eChest"));
        inv.openTrade(p);
    }

    public void openPotions(Player p) {
        TradingInventory inv = new TradingInventory("§cPotions");
        inv.addTrade(getItem(IRON_INGOT, 3, "§7Silver"), getItem(POTION, "§cHealing Lvl 1", 16453));
        inv.addTrade(getItem(IRON_INGOT, 5, "§7Silver"), getItem(POTION, "§cHealing Lvl 2", 16421));
        inv.addTrade(getItem(IRON_INGOT, 7, "§7Silver"), getItem(POTION, "§cSpeed", 16386));
        inv.openTrade(p);
    }

    public void openSpecial(Player p) {
        TradingInventory inv = new TradingInventory("§cSpecial");
        inv.addTrade(getItem(CLAY_BRICK, "§cBronze"), getItem(LADDER, "§cLadder"));
        inv.addTrade(getItem(CLAY_BRICK, 16, "§cBronze"), getItem(WEB, "§cCobweb"));
        inv.addTrade(getItem(IRON_INGOT, 5, "§7Silver"), getItem(FISHING_ROD, "§cFishing Rod"));
        inv.addTrade(getItem(IRON_INGOT, 7, "§7Silver"), getItem(FLINT_AND_STEEL, "§cFlint And Steel"));
        inv.addTrade(getItem(GOLD_INGOT, 3, "§6Gold"), getItem(TNT, "§cTNT"));
        inv.addTrade(getItem(GOLD_INGOT, 13, "§6Gold"), getItem(ENDER_PEARL, "§cEnder Pearl"));
      //  inv.addTrade(getItem(CLAY_BRICK, 64, "§cBronze"), getItem(MONSTER_EGG, "§cJihadi", 90));
        inv.openTrade(p);
    }

    @Builder
    @Getter
    private static class Enchantments {

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
                public void setTradingPlayer(EntityHuman entityHuman) {

                }

                @Override
                public EntityHuman t_() {
                    return e;
                }

                @Override
                public MerchantRecipeList getOffers(EntityHuman arg0) {
                    return l;
                }

                @Override
                public IChatBaseComponent getScoreboardDisplayName() {
                    return IChatBaseComponent.ChatSerializer.a(invname);
                }

                @Override
                public void a(MerchantRecipe arg0) {
                }

                @Override
                public void a(net.minecraft.server.v1_9_R1.ItemStack itemStack) {

                }
            });
        }

    }

}
