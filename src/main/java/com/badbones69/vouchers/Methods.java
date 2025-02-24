package com.badbones69.vouchers;

import com.badbones69.vouchers.api.FileManager.Files;
import com.badbones69.vouchers.api.enums.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Methods {

    private final Vouchers plugin = Vouchers.getPlugin();
    
    public final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F\\d]{6}");
    
    public void removeItem(ItemStack item, Player player) {
        if (item.getAmount() <= 1) {
            player.getInventory().removeItem(item);
        } else if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        }
    }
    
    public String getPrefix(String message) {
        return color(Files.CONFIG.getFile().getString("Settings.Prefix") + message);
    }

    public String color(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group()).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }
    
    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }
    
    public boolean isInt(CommandSender sender, String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%Arg%", s);
            placeholders.put("%arg%", s);
            sender.sendMessage(Messages.NOT_A_NUMBER.getMessage(placeholders));
            return false;
        }

        return true;
    }
    
    public boolean isOnline(CommandSender sender, String name) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) return true;
        }

        sender.sendMessage(Messages.NOT_ONLINE.getMessage());
        return false;
    }
    
    public boolean hasPermission(Player player, String perm) {
        if (!player.hasPermission("voucher." + perm)) {
            player.sendMessage(Messages.NO_PERMISSION.getMessage());
            return false;
        }

        return true;
    }
    
    public boolean hasPermission(CommandSender sender, String perm) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("voucher." + perm)) {
                player.sendMessage(Messages.NO_PERMISSION.getMessage());
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    
    public boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }
    
    public void fireWork(Location loc, List<Color> list) {
        if (loc.getWorld() == null) return;

        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(list).trail(false).flicker(false).build());
        meta.setPower(0);
        firework.setFireworkMeta(meta);
        plugin.getFireworkDamageAPI().addFirework(firework);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, firework::detonate, 2);
    }
    
    public Color getColor(String color) {
        if (color.equalsIgnoreCase("AQUA")) return Color.AQUA;
        if (color.equalsIgnoreCase("BLACK")) return Color.BLACK;
        if (color.equalsIgnoreCase("BLUE")) return Color.BLUE;
        if (color.equalsIgnoreCase("FUCHSIA")) return Color.FUCHSIA;
        if (color.equalsIgnoreCase("GRAY")) return Color.GRAY;
        if (color.equalsIgnoreCase("GREEN")) return Color.GREEN;
        if (color.equalsIgnoreCase("LIME")) return Color.LIME;
        if (color.equalsIgnoreCase("MAROON")) return Color.MAROON;
        if (color.equalsIgnoreCase("NAVY")) return Color.NAVY;
        if (color.equalsIgnoreCase("OLIVE")) return Color.OLIVE;
        if (color.equalsIgnoreCase("ORANGE")) return Color.ORANGE;
        if (color.equalsIgnoreCase("PURPLE")) return Color.PURPLE;
        if (color.equalsIgnoreCase("RED")) return Color.RED;
        if (color.equalsIgnoreCase("SILVER")) return Color.SILVER;
        if (color.equalsIgnoreCase("TEAL")) return Color.TEAL;
        if (color.equalsIgnoreCase("WHITE")) return Color.WHITE;
        if (color.equalsIgnoreCase("YELLOW")) return Color.YELLOW;

        return Color.WHITE;
    }
    
    public boolean isSimilar(ItemStack one, ItemStack two) {
        if (one.getType() == two.getType()) {
            if (one.hasItemMeta()) {
                if (one.getItemMeta().hasDisplayName()) {
                    if (one.getItemMeta().getDisplayName().equalsIgnoreCase(two.getItemMeta().getDisplayName())) {
                        if (one.getItemMeta().hasLore()) {
                            if (one.getItemMeta().getLore().size() == two.getItemMeta().getLore().size()) {
                                int i = 0;

                                for (String lore : one.getItemMeta().getLore()) {
                                    if (!lore.equals(two.getItemMeta().getLore().get(i))) {
                                        return false;
                                    }

                                    i++;
                                }

                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }
}