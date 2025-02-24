package com.badbones69.vouchers.api.enums;

import com.badbones69.vouchers.Methods;
import com.badbones69.vouchers.Vouchers;
import com.badbones69.vouchers.api.FileManager.Files;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum Messages {
    
    RELOAD("Config-Reload", "&7You have just reloaded the Config.yml"),
    INVENTORY_FULL("Inventory-Full", "&cYour inventory is to full. Please open up some space to buy that."),
    PLAYERS_ONLY("Reload", "&cOnly players can use this command."),
    NO_PERMISSION("No-Permission", "&cYou do not have permission to use that command!"),
    NO_PERMISSION_TO_VOUCHER("No-Permission-To-Voucher", "&cYou do not have permission to use that voucher."),
    NOT_ONLINE("Not-Online", "&cThat player is not online at this time."),
    NOT_A_NUMBER("Not-A-Number", "&c%Arg% is not a number."),
    NOT_A_VOUCHER("Not-A-Voucher", "&cThat is not a Voucher Type."),
    NOT_A_PLAYER("Not-A-Player", "&cYou must be a player to use this command."),
    CODE_UNAVAILABLE("Code-UnAvailable", "&cThe Voucher code &6%Arg% &cis incorrect or unavailable at this time."),
    CODE_USED("Code-Used", "&cThe voucher code &6%code% &chas already been used."),
    GIVEN_A_VOUCHER("Given-A-Voucher", "&3You have just given &6%Player% &3a &6%Voucher% &3voucher."),
    GIVEN_ALL_PLAYERS_VOUCHER("Given-All-Players-Voucher", "&3You have just given all players a &6%Voucher% &3voucher."),
    HIT_LIMIT("Hit-Limit", "&cYou have hit your limit for using this voucher."),
    TWO_STEP_AUTHENTICATION("Two-Step-Authentication", "&7Right click again to confirm that you want to use this voucher."),
    HAS_BLACKLIST_PERMISSION("Has-Blacklist-Permission", "&cSorry but you can not use this voucher because you have a black-listed permission."),
    NOT_IN_WHITELISTED_WORLD("Not-In-Whitelisted-World", "&cYou can not use that voucher here as you are not in a whitelisted world for this voucher."),
    UNSTACK_ITEM("Unstack-Item", "&cYou need to unstack that item before you can use it."),
    NO_PERMISSION_TO_USE_VOUCHER_IN_OFFHAND("No-Permission-To-Use-Voucher-In-OffHand", "&cYou do not have permission to use vouchers in your off hand."),
    CANNOT_PUT_ITEMS_IN_CRAFTING_TABLE("Cannot-Put-Items-In-Crafting-Table", "&cYou cannot put vouchers in the crafting table."),
    HELP("Help",
    Arrays.asList(
    "&8- &6/Voucher Help &3Lists all the commands for vouchers.",
    "&8- &6/Voucher Types &3Lists all types of vouchers and codes.",
    "&8- &6/Voucher Redeem <Code> &3Allows player to redeem a voucher code.",
    "&8- &6/Voucher Give <Type> [Amount] [Player] [Arguments] &3Gives a player a voucher.",
    "&8- &6/Voucher GiveAll <Type> [Amount] [Arguments] &3Gives all players a voucher.",
    "&8- &6/Voucher Open [Page] &3Opens a GUI so you can get vouchers easy.",
    "&8- &6/Voucher Reload &3Reloaded the configuration files."));
    
    private final String path;
    private String defaultMessage;
    private List<String> defaultListMessage;
    
    Messages(String path, String defaultMessage) {
        this.path = path;
        this.defaultMessage = defaultMessage;
    }
    
    Messages(String path, List<String> defaultListMessage) {
        this.path = path;
        this.defaultListMessage = defaultListMessage;
    }

    private static Vouchers plugin = Vouchers.getPlugin();

    private static Methods methods = plugin.getMethods();
    
    public static String convertList(List<String> list) {
        StringBuilder message = new StringBuilder();

        for (String line : list) {
            message.append(methods.color(line)).append("\n");
        }

        return message.toString();
    }
    
    public static void addMissingMessages() {
        FileConfiguration messages = Files.MESSAGES.getFile();
        boolean saveFile = false;

        for (Messages message : values()) {
            if (!messages.contains("Messages." + message.getPath())) {
                saveFile = true;

                if (message.getDefaultMessage() != null) {
                    messages.set("Messages." + message.getPath(), message.getDefaultMessage());
                } else {
                    messages.set("Messages." + message.getPath(), message.getDefaultListMessage());
                }
            }
        }

        if (saveFile) {
            Files.MESSAGES.saveFile();
        }
    }
    
    public String getMessage() {
        return getMessage(true);
    }
    
    public String getMessage(String placeholder, String replacement) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);
        return getMessage(placeholders, true);
    }
    
    public String getMessage(HashMap<String, String> placeholders) {
        return getMessage(placeholders, true);
    }
    
    public String getMessageNoPrefix() {
        return getMessage(false);
    }
    
    public String getMessageNoPrefix(String placeholder, String replacement) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);
        return getMessage(placeholders, false);
    }
    
    public String getMessageNoPrefix(HashMap<String, String> placeholders) {
        return getMessage(placeholders, false);
    }
    
    public static String replacePlaceholders(HashMap<String, String> placeholders, String message) {
        for (String placeholder : placeholders.keySet()) {
            message = message.replaceAll(placeholder, placeholders.get(placeholder))
            .replaceAll(placeholder.toLowerCase(), placeholders.get(placeholder));
        }

        return message;
    }
    
    public static List<String> replacePlaceholders(HashMap<String, String> placeholders, List<String> messageList) {
        List<String> newMessageList = new ArrayList<>();

        for (String message : messageList) {
            for (String placeholder : placeholders.keySet()) {
                newMessageList.add(message.replaceAll(placeholder, placeholders.get(placeholder))
                .replaceAll(placeholder.toLowerCase(), placeholders.get(placeholder)));
            }
        }

        return newMessageList;
    }
    
    // check if message is blank
    // if message is blank return true
    // if message is not blank return false
    public boolean isBlank() {
        return getMessage(false).equals("");
    }
    
    private String getMessage(boolean prefix) {
        return getMessage(new HashMap<>(), prefix);
    }
    
    private String getMessage(HashMap<String, String> placeholders, boolean prefix) {
        String message;
        boolean isList = isList();
        boolean exists = exists();

        if (isList) {
            if (exists) {
                message = methods.color(convertList(Files.MESSAGES.getFile().getStringList("Messages." + path)));
            } else {
                message = methods.color(convertList(getDefaultListMessage()));
            }
        } else {
            if (exists) {
                message = methods.color(Files.MESSAGES.getFile().getString("Messages." + path));
            } else {
                message = methods.color(getDefaultMessage());
            }
        }

        for (String placeholder : placeholders.keySet()) {
            message = message.replaceAll(placeholder, placeholders.get(placeholder))
            .replaceAll(placeholder.toLowerCase(), placeholders.get(placeholder));
        }

        if (isList) { // Don't want to add a prefix to a list of messages.
            return methods.color(message);
        } else { // If the message isn't a list.
            if (prefix) { // If the message needs a prefix.
                return methods.getPrefix(message);
            } else { // If the message doesn't need a prefix.
                return methods.color(message);
            }
        }
    }
    
    private boolean exists() {
        return Files.MESSAGES.getFile().contains("Messages." + path);
    }
    
    private boolean isList() {
        if (Files.MESSAGES.getFile().contains("Messages." + path)) {
            return !Files.MESSAGES.getFile().getStringList("Messages." + path).isEmpty();
        } else {
            return defaultMessage == null;
        }
    }
    
    private String getPath() {
        return path;
    }
    
    private String getDefaultMessage() {
        return defaultMessage;
    }
    
    private List<String> getDefaultListMessage() {
        return defaultListMessage;
    }
}