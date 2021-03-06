package com.nullblock.vemacs.bitwise;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class Bitwise extends JavaPlugin implements Listener {

    public static int ratio;
    public static int min;
    public static int swaps;
    public static int special;

    public void onDisable() {
    }

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        ratio = this.getConfig().getInt("ratio");
        min = this.getConfig().getInt("min");
        swaps = this.getConfig().getInt("swaps");
        special = this.getConfig().getInt("special");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        String parsed = parse(message);
        if (!message.equals(parsed)) {
            event.setMessage(parsed);
            this.getLogger().info("Operated on message by " + event.getPlayer().getName());
        }
    }

    public static String parse(String s) {
        if (isSuspicious(s)) {
            s = operate(s);
        }
        if (s.length() >= min && getSanitization(s)) {
            s = s.replaceAll("[^a-zA-Z0-9]+","");
        }
        return s;
    }

    public static boolean isSuspicious(String s) {
        if (s.length() < min) {
            return false;
        }
        if (StringUtils.countMatches(s, " ") == 0) {
            return true;
        }
        if (((double) (s.length()) / ((double) StringUtils.countMatches(s, " "))) >= (double) ratio) {
            return true;
        }
        return false;
    }

    public static boolean getSanitization(String s) {
        int length = s.length();
        int sanitized = s.replaceAll("[^\\w\\s\\.]", "").length();
        float f = (float) length / (float) (length - sanitized);
        if (f <= special) {
            return true;
        }
        return false;
    }

    public static String operate(String s) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < swaps; i++) {
            int position = new Random().nextInt(s.length());
            while (String.valueOf(s.charAt(position)).equals(" ")) {
                position = new Random().nextInt(s.length());
            }
            sb.setCharAt(position, reverse(s.charAt(position)));
        }
        return sb.toString();
    }

    public static char reverse(char ch) {
        if (!String.valueOf(ch).toUpperCase().equals(String.valueOf(ch))) {
            return String.valueOf(ch).toUpperCase().charAt(0);
        }
        if (!String.valueOf(ch).toLowerCase().equals(String.valueOf(ch))) {
            return String.valueOf(ch).toLowerCase().charAt(0);
        }
        if (isInteger(String.valueOf(ch))) {
            return Integer.toString(new Random().nextInt(9)).charAt(0);
        }
        return ch;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(sender.hasPermission("bitwise.reload") && cmd.getName().equalsIgnoreCase("bitwise")){
            this.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Reloaded Bitwise.");
            return true;
        }
        return false;
    }


}

