package com.norcode.bukkit.mojangnotice;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public class CommandExecutor implements TabExecutor {

	private MojangNoticePlugin plugin;
	
	public CommandExecutor(MojangNoticePlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String label, String[] params) {
		
		return null;
	}

	public void showHelp(CommandSender sender) {
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] params) {
		if (command.getName().equalsIgnoreCase("mojangnotice")) {
			
			if (params.length == 0) {
				showHelp(sender);
				return true;
			}
			
			switch (params[0].toLowerCase()) {
			case "check":
				if (!sender.hasPermission("mojangnotice.check")) return false;
				plugin.scheduleTimer(sender);
				break;
			case "reload":
				if (!sender.hasPermission("mojangnotice.reload")) return false;
				plugin.reloadConfig();
				plugin.scheduleRepeatTask();
				break;
			case "set":
				if (params.length < 3) {
					sender.sendMessage("'set' expects 2 arguments.");
					return true;
				}
				String key = params[1];
				switch (key.toLowerCase()) {
				case "motd":
					if (!sender.hasPermission("mojangnotice.set.motd")) return false;
					String newMotd = "";
					for (int i=2;i<params.length;i++) newMotd += params[i] + " ";
					if (newMotd.endsWith(" ")) newMotd = newMotd.substring(0,newMotd.length()-1);
					plugin.getConfig().set("motd", newMotd);
					plugin.saveConfig();
					sender.sendMessage("MOTD Override Message set to: " + newMotd);
					break;
				case "checkinterval":
					if (!sender.hasPermission("mojangnotice.set.check-interval")) return false;
					Long newCI = plugin.getConfig().getLong("check-interval");
					try {
						newCI = Long.parseLong(params[2].trim());
					} catch (IllegalArgumentException ex) {}
					plugin.getConfig().set("check-interval", newCI);
					plugin.saveConfig();
					plugin.scheduleTimer(null);
					sender.sendMessage("Check inverval set to " + newCI + " ticks.");
					break;
				case "repeatinterval":
					if (!sender.hasPermission("mojangnotice.set.repeat-interval")) return false;
					Long newRI = plugin.getConfig().getLong("repeat-interval");
					try {
						newRI = Long.parseLong(params[2].trim());
					} catch (IllegalArgumentException ex) {}
					plugin.getConfig().set("repeat-interval", newRI);
					plugin.saveConfig();
					plugin.scheduleTimer(null);
					sender.sendMessage("Repeat inverval set to " + newRI + " ticks.");
					break;
				case "overridemotd":
					if (!sender.hasPermission("mojangnotice.set.override-motd")) return false;
					Boolean newOR = plugin.getConfig().getBoolean("override-motd");
					String arg = params[2].trim().toLowerCase();
					switch (arg) {
					case "y":
					case "yes":
					case "on":
					case "1":
						newOR = true;
						break;
					case "0":
					case "n":
					case "no":
					case "off":
						newOR = false;
						break;
					}
					plugin.getConfig().set("override-motd", newOR);
					plugin.saveConfig();
					sender.sendMessage("MOTD Override is " + (newOR ? "enabled" : "disabled") + ".");
					break;
				default:
					sender.sendMessage("Unknown variable: " + params[1]);
				}
				break;
			}
			return true;
		}
		return false;
	}
	
	
}
