package me.badbones69.vouchers.controlers;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.badbones69.vouchers.Main;
import me.badbones69.vouchers.Methods;
import me.badbones69.vouchers.api.Version;
import me.badbones69.vouchers.api.Vouchers;

public class VoucherClick implements Listener{
	
	private HashMap<Player, String> twoAuth = new HashMap<Player, String>();
	
	@EventHandler
	public void onVoucherClick(PlayerInteractEvent e){
		ItemStack item = getItemInHand(e.getPlayer());
		Player player = e.getPlayer();
		Action action = e.getAction();
		FileConfiguration data = Main.settings.getData();
		FileConfiguration config = Main.settings.getConfig();
		if(Version.getVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger()){
			if(e.getHand() != EquipmentSlot.HAND){
				return;
			}
		}
		if(action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR){
			if(item == null)return;
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore()){
					for(String voucher : Vouchers.getVouchers()){
						if(Vouchers.hasVoucherItemName(item, voucher) || item.getItemMeta().getDisplayName().equalsIgnoreCase(Vouchers.getVoucher(voucher).getItemMeta().getDisplayName())){
							e.setCancelled(true);
							String id = config.getString("Vouchers." + voucher + ".Item");
							ItemStack i = Methods.makeItem(id, 1);
							if(item.getType() == i.getType()){
								if(passesPermissionChecks(player, voucher)){
									String uuid = player.getUniqueId().toString();
									if(!player.hasPermission("Voucher.Bypass")){
										if(Vouchers.isLimiterEnabled(voucher)){
											if(data.contains("Players." + uuid)){
												if(data.contains("Players." + uuid + ".Vouchers." + voucher)){
													int amount = data.getInt("Players." + uuid + ".Vouchers." + voucher);
													if(amount >= Vouchers.getLimiter(voucher)){
														player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsgs().getString("Messages.Hit-Limit")));
														return;
													}
												}
											}
										}
									}
									if(config.getBoolean("Vouchers." + voucher + ".Options.Two-Step-Authentication.Toggle")){
										if(twoAuth.containsKey(player)){
											if(!twoAuth.get(player).equalsIgnoreCase(voucher)){
												player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsgs().getString("Messages.Two-Step-Authentication")));
												twoAuth.put(player, voucher);
												return;
											}
										}else{
											player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsgs().getString("Messages.Two-Step-Authentication")));
											twoAuth.put(player, voucher);
											return;
										}
									}
									voucherClick(player, item, voucher);
									if(twoAuth.containsKey(player)){
										twoAuth.remove(player);
									}
									return;
								}
							}
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private ItemStack getItemInHand(Player player){
		if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
			return player.getInventory().getItemInMainHand();
		}else{
			return player.getItemInHand();
		}
	}
	
	private boolean passesPermissionChecks(Player player, String voucher){
		Boolean checker = true;
		if(!player.isOp()){
			if(!player.hasPermission(("Voucher." + Vouchers.getWhitelistPermissionNode(voucher)).toLowerCase()) && Vouchers.isWhitelistPermissionEnabled(voucher)){
				player.sendMessage(Methods.color(Methods.getPrefix() + Main.settings.getMsgs().getString("Messages.No-Permission-To-Voucher")));
				checker =  false;
			}
			if(checker){
				if(Vouchers.isBlacklistPermissionsEnabled(voucher)){
					for(String permission : Vouchers.getBlacklistPermissions(voucher)){
						if(player.hasPermission(permission.toLowerCase())){
							player.sendMessage(Methods.color(Methods.getPrefix() + Main.settings.getMsgs().getString("Messages.Has-Blacklist-Permission")));
							checker = false;
						}
					}
				}
			}
		}
		return checker;
	}
	
	private void voucherClick(Player player, ItemStack item, String voucher){
		List<String> lore = item.getItemMeta().getLore();
		List<String> L = Main.settings.getConfig().getStringList("Vouchers." + voucher + ".Lore");
		String name = player.getName();
		String argument = "";
		if(Vouchers.hasVoucherItemName(item, voucher)){
			argument = Vouchers.getVoucherArgumentItemName(item, voucher);
		}
		if(argument == ""){
			int i = 0;
			for(String l : L){
				l = Methods.color(l);
				l = Methods.Args(l);
				String lo = lore.get(i);
				lo = Methods.Args(lo);
				if(l.contains("%Arg%")){
					String[] b = l.split("%Arg%");
					if(b.length>=1)argument = lo.replace(b[0], "");
					if(b.length>=2)argument = argument.replace(b[1], "");
				}
				i++;
			}
		}
		Methods.removeItem(item, player);
		for(String cmd : Vouchers.getCommands(voucher, player, argument)){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
		for(ItemStack it : Vouchers.getItems(voucher)){
			if(!Methods.isInvFull(player)){
				player.getInventory().addItem(it);
			}else{
				player.getWorld().dropItem(player.getLocation(), it);
			}
		}
		if(Vouchers.isSoundEnabled(voucher)){
			for(Sound sound : Vouchers.getSound(voucher)){
				player.playSound(player.getLocation(), sound, 1, 1);
			}
		}
		if(Vouchers.isFireworkEnabled(voucher)){
			Methods.fireWork(player.getLocation(), Vouchers.getFireworkColors(voucher));
		}
		String msg = Main.settings.getConfig().getString("Vouchers." + voucher + ".Options.Message");
		msg = msg.replaceAll("%Player%", name).replaceAll("%player%", name)
				.replaceAll("%Arg%", argument).replaceAll("%arg%", argument);
		if(!msg.equals("")){
			player.sendMessage(Methods.getPrefix() + Methods.color(msg));
		}
		int amount = 0;
		if(Main.settings.getData().contains("Players."+player.getUniqueId()+".Vouchers." + voucher)){
			amount = Main.settings.getData().getInt("Players."+player.getUniqueId()+".Vouchers." + voucher);
		}
		amount = amount+1;
		Main.settings.getData().set("Players."+player.getUniqueId()+".UserName",player.getName());
		Main.settings.getData().set("Players."+player.getUniqueId()+".Vouchers." + voucher, amount);
		Main.settings.saveData();
	}
	
}