package me.badbones69.vouchers.api.objects;

import me.badbones69.vouchers.Methods;
import me.badbones69.vouchers.api.FileManager.Files;
import me.badbones69.vouchers.api.enums.Messages;
import me.badbones69.vouchers.api.itemnbtapi.NBTItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Voucher {
	
	private String name;
	private Boolean usesArgs;
	private Material itemMaterial;
	private Short itemMetaData;
	private String itemName;
	private Boolean itemGlow;
	private String usedMessage;
	private Boolean whitelistPermissionToggle;
	private List<String> whitelistPermissions = new ArrayList<>();
	private List<String> whitelistCommands = new ArrayList<>();
	private Boolean whitelistWorldsToggle;
	private String whitelistWorldMessage;
	private List<String> whitelistWorlds = new ArrayList<>();
	private List<String> whitelistWorldCommands = new ArrayList<>();
	private Boolean blacklistPermissionsToggle;
	private String blacklistPermissionMessage;
	private List<String> blacklistCommands = new ArrayList<>();
	private List<String> blacklistPermissions = new ArrayList<>();
	private Boolean limiterToggle;
	private Integer limiterLimit;
	private Boolean twostepAuthentication;
	private Boolean soundToggle;
	private List<Sound> sounds = new ArrayList<>();
	private Boolean fireworkToggle;
	private List<Color> fireworkColors = new ArrayList<>();
	private boolean isEdible;
	private List<String> itemLore;
	private List<String> commands = new ArrayList<>();
	private List<VoucherCommand> randomCoammnds = new ArrayList<>();
	private List<VoucherCommand> chanceCommands = new ArrayList<>();
	private List<ItemStack> items = new ArrayList<>();
	
	/**
	 * This is just used for imputing fake vouchers.
	 * @param number Fake Voucher Number.
	 */
	public Voucher(int number) {
		this.name = number + "";
		this.usesArgs = false;
		this.itemMaterial = Material.STONE;
		this.itemMetaData = 0;
		this.itemName = number + "";
		this.itemGlow = false;
		this.usedMessage = "";
		this.whitelistPermissionToggle = false;
		this.whitelistWorldsToggle = false;
		this.whitelistWorldMessage = "";
		this.blacklistPermissionsToggle = false;
		this.blacklistPermissionMessage = "blacklistPermissionMessage";
		this.limiterToggle = false;
		this.limiterLimit = 0;
		this.twostepAuthentication = false;
		this.soundToggle = false;
		this.fireworkToggle = false;
		this.isEdible = false;
	}
	
	public Voucher(String name) {
		this.name = name;
		this.usesArgs = false;
		FileConfiguration config = Files.CONFIG.getFile();
		String path = "Vouchers." + name + ".";
		String id = config.getString(path + "Item");
		itemMetaData = 0;
		if(id.contains(":")) {
			String[] b = id.split(":");
			id = b[0];
			itemMetaData = Short.parseShort(b[1]);
		}
		itemMaterial = Material.matchMaterial(id);
		this.itemName = config.getString(path + "Name");
		this.itemLore = config.getStringList(path + "Lore");
		if(this.itemName.toLowerCase().contains("%arg%")) {
			this.usesArgs = true;
		}
		if(!usesArgs) {
			for(String lore : this.itemLore) {
				if(lore.toLowerCase().contains("%arg%")) {
					this.usesArgs = true;
					break;
				}
			}
		}
		this.itemGlow = config.contains(path + "Glowing") && config.getBoolean(path + "Glowing");
		this.commands = config.getStringList(path + "Commands");
		for(String commands : config.getStringList(path + "Random-Commands")) {
			this.randomCoammnds.add(new VoucherCommand(commands));
		}
		for(String line : config.getStringList(path + "Chance-Commands")) {// - '%chance% %command%, %command%, %command%, ... etc'
			try {
				String[] split = line.split(" ");
				VoucherCommand voucherCommand = new VoucherCommand(line.substring(split[0].length() + 1));
				for(int i = 1; i <= Integer.parseInt(split[0]); i++) {
					chanceCommands.add(voucherCommand);
				}
			}catch(Exception e) {
				System.out.println("[Vouchers] An issue occurred when trying to use chance commands.");
				e.printStackTrace();
			}
		}
		for(String itemString : config.getStringList(path + "Items")) {
			this.items.add(Methods.makeItem(itemString));
		}
		this.usedMessage = config.getString(path + "Options.Message", "");
		if(config.contains(path + "Options.Permission.Whitelist-Permission")) {
			this.whitelistPermissionToggle = config.getBoolean(path + "Options.Permission.Whitelist-Permission.Toggle");
			if(config.contains(path + "Options.Permission.Whitelist-Permission.Node")) {
				whitelistPermissions.add("voucher." + config.getString(path + "Options.Permission.Whitelist-Permission.Node").toLowerCase());
			}
			whitelistPermissions.addAll(config.getStringList(path + "Options.Permission.Whitelist-Permission.Permissions").stream().map(String :: toLowerCase).collect(Collectors.toList()));
			this.whitelistCommands = config.getStringList(path + "Options.Permission.Whitelist-Permission.Commands");
		}else {
			this.whitelistPermissionToggle = false;
		}
		if(config.contains(path + "Options.Whitelist-Worlds.Toggle")) {
			this.whitelistWorlds.addAll(config.getStringList(path + "Options.Whitelist-Worlds.Worlds").stream().map(String :: toLowerCase).collect(Collectors.toList()));
			if(config.contains(path + "Options.Whitelist-Worlds.Message")) {
				this.whitelistWorldMessage = config.getString(path + "Options.Whitelist-Worlds.Message");
			}else {
				this.whitelistWorldMessage = Messages.NOT_IN_WHITELISTED_WORLD.getMessageNoPrefix();
			}
			this.whitelistWorldCommands = config.getStringList(path + "Options.Whitelist-Worlds.Commands");
			this.whitelistWorldsToggle = !this.whitelistWorlds.isEmpty() && config.getBoolean(path + "Options.Whitelist-Worlds.Toggle");
		}else {
			this.whitelistWorldsToggle = false;
		}
		if(config.contains(path + "Options.Permission.Blacklist-Permissions")) {
			this.blacklistPermissionsToggle = config.getBoolean(path + "Options.Permission.Blacklist-Permissions.Toggle");
			if(config.contains(path + "Options.Permission.Blacklist-Permissions.Message")) {
				this.blacklistPermissionMessage = config.getString(path + "Options.Permission.Blacklist-Permissions.Message");
			}else {
				this.blacklistPermissionMessage = Messages.HAS_BLACKLIST_PERMISSION.getMessageNoPrefix();
			}
			this.blacklistPermissions = config.getStringList(path + "Options.Permission.Blacklist-Permissions.Permissions");
			this.blacklistCommands = config.getStringList(path + "Options.Permission.Blacklist-Permissions.Commands");
		}else {
			this.blacklistPermissionsToggle = false;
		}
		if(config.contains(path + "Options.Limiter")) {
			this.limiterToggle = config.getBoolean(path + "Options.Limiter.Toggle");
			this.limiterLimit = config.getInt(path + "Options.Limiter.Limit");
		}else {
			this.limiterToggle = false;
		}
		this.twostepAuthentication = config.contains(path + "Options.Two-Step-Authentication") && config.getBoolean(path + "Options.Two-Step-Authentication.Toggle");
		if(config.contains(path + "Options.Sound")) {
			this.soundToggle = config.getBoolean(path + "Options.Sound.Toggle");
			for(String sound : config.getStringList(path + "Options.Sound.Sounds")) {
				try {
					this.sounds.add(Sound.valueOf(sound));
				}catch(Exception e) {
				}
			}
		}else {
			this.soundToggle = false;
		}
		if(config.getBoolean(path + "Options.Firework.Toggle")) {
			for(String color : config.getString(path + "Options.Firework.Colors", "").split(", ")) {
				this.fireworkColors.add(Methods.getColor(color));
			}
			this.fireworkToggle = !fireworkColors.isEmpty();
		}else {
			this.fireworkToggle = false;
		}
		if(config.getBoolean(path + "Options.Is-Edible")) {
			this.isEdible = itemMaterial.isEdible();
			switch(itemMaterial) {
				case MILK_BUCKET:
				case POTION:
					this.isEdible = true;
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Boolean usesArguments() {
		return usesArgs;
	}
	
	public ItemStack buildItem() {
		ItemStack item = Methods.addGlow(new ItemBuilder()
		.setMaterial(itemMaterial)
		.setMetaData(itemMetaData)
		.setName(itemName)
		.setLore(itemLore)
		.build(), itemGlow);
		NBTItem nbt = new NBTItem(item);
		nbt.setString("voucher", name);
		return nbt.getItem();
	}
	
	public ItemStack buildItem(int amount) {
		ItemStack item = Methods.addGlow(new ItemBuilder()
		.setMaterial(itemMaterial)
		.setMetaData(itemMetaData)
		.setAmount(amount)
		.setName(itemName)
		.setLore(itemLore)
		.build(), itemGlow);
		NBTItem nbt = new NBTItem(item);
		nbt.setString("voucher", name);
		return nbt.getItem();
	}
	
	public ItemStack buildItem(String argument) {
		ItemStack item = Methods.addGlow(new ItemBuilder()
		.setMaterial(itemMaterial)
		.setMetaData(itemMetaData)
		.setName(itemName)
		.setLore(itemLore)
		.addLorePlaceholder("%Arg%", argument)
		.addNamePlaceholder("%Arg%", argument)
		.build(), itemGlow);
		NBTItem nbt = new NBTItem(item);
		nbt.setString("voucher", name);
		nbt.setString("argument", argument);
		return nbt.getItem();
		
	}
	
	public ItemStack buildItem(String argument, int amount) {
		ItemStack item = Methods.addGlow(new ItemBuilder()
		.setMaterial(itemMaterial)
		.setMetaData(itemMetaData)
		.setAmount(amount)
		.setName(itemName)
		.setLore(itemLore)
		.addLorePlaceholder("%Arg%", argument)
		.addNamePlaceholder("%Arg%", argument)
		.build(), itemGlow);
		NBTItem nbt = new NBTItem(item);
		nbt.setString("voucher", name);
		nbt.setString("argument", argument);
		return nbt.getItem();
	}
	
	public String getVoucherUsedMessage() {
		return usedMessage;
	}
	
	public Boolean useWhiteListPermissions() {
		return whitelistPermissionToggle;
	}
	
	public List<String> getWhitelistPermissions() {
		return whitelistPermissions;
	}
	
	public List<String> getWhitelistCommands() {
		return whitelistCommands;
	}
	
	public Boolean usesWhitelistWorlds() {
		return whitelistWorldsToggle;
	}
	
	public List<String> getWhitelistWorlds() {
		return whitelistWorlds;
	}
	
	public String getWhitelistWorldMessage() {
		return whitelistWorldMessage;
	}
	
	public List<String> getWhitelistWorldCommands() {
		return whitelistWorldCommands;
	}
	
	public Boolean useBlackListPermissions() {
		return blacklistPermissionsToggle;
	}
	
	public List<String> getBlackListPermissions() {
		return blacklistPermissions;
	}
	
	public String getBlackListMessage() {
		return blacklistPermissionMessage;
	}
	
	public List<String> getBlacklistCommands() {
		return blacklistCommands;
	}
	
	public Boolean useLimiter() {
		return limiterToggle;
	}
	
	public Integer getLimiterLimit() {
		return limiterLimit;
	}
	
	public Boolean useTwoStepAuthentication() {
		return twostepAuthentication;
	}
	
	public Boolean playSounds() {
		return soundToggle;
	}
	
	public List<Sound> getSounds() {
		return sounds;
	}
	
	public Boolean useFirework() {
		return fireworkToggle;
	}
	
	public List<Color> getFireworkColors() {
		return fireworkColors;
	}
	
	public List<String> getCommands() {
		return commands;
	}
	
	public List<VoucherCommand> getRandomCoammnds() {
		return randomCoammnds;
	}
	
	public List<VoucherCommand> getChanceCommands() {
		return chanceCommands;
	}
	
	public List<ItemStack> getItems() {
		return items;
	}
	
	public boolean isEdible() {
		return isEdible;
	}
	
}