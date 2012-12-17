package com.norcode.bukkit.mojangnotice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class MojangNoticePlugin extends JavaPlugin implements Listener {
	int checkTaskId = -1;
	int repeatTaskId = -1;
	public ConcurrentHashMap<String, Boolean> statuses = new ConcurrentHashMap<String, Boolean>();
	private URL statusURL;
	
	@Override
	public void onEnable() {
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);
		saveConfig();
		getServer().getPluginManager().registerEvents(this, this);
		statuses.put("login", true);
		statuses.put("session", true);
		scheduleTimer(null);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onServerListPing(ServerListPingEvent event) {
		if (getConfig().getBoolean("override-motd") && !allGood()) {
			event.setMotd(interpolate(getConfig().getString("motd")));
		}
	}
	
	public void scheduleRepeatTask() {
		repeatTaskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				executeCommands("commands.repeatdown");
			}
		}, 0, getConfig().getLong("repeat-interval"));
	}
	
	public void scheduleTimer(final CommandSender sender) {
		
		if (repeatTaskId != -1) {
			// Cancel any existing repeat task. 
			getServer().getScheduler().cancelTask(repeatTaskId);
		}
		
		if (checkTaskId != -1) {
			// Cancel any existing scheduled task.
			getServer().getScheduler().cancelTask(checkTaskId);
		}
		
		try {
			statusURL = new URL(getConfig().getString("status-url"));
		} catch (MalformedURLException ex) {
			getLogger().severe("Malformed URL: " + getConfig().getString("status-url") + ", MojangNotice is disabled.");
			return;
		}
		
		checkTaskId = getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				final StatusResponse resp = fetchStatus();
				if (resp != null) {
					getServer().getScheduler().scheduleSyncDelayedTask(MojangNoticePlugin.this, new Runnable() {
						@Override
						public void run() {
							 boolean allGood = allGood();
							 statuses.put("login", resp.getLoginUp());
							 statuses.put("session", resp.getSessionUp());
							 if (allGood && !(resp.getLoginUp() && resp.getSessionUp())) {
								 // Going Down.
								 scheduleRepeatTask();
								 executeCommands("commands.down");
							 } else if ((!allGood) && (resp.getLoginUp() && resp.getSessionUp())) {
								 // Back up
								 if (repeatTaskId != -1) {
									 getServer().getScheduler().cancelTask(repeatTaskId);
								 }
								 executeCommands("commands.up");
							 }
							 // if this was requested by command, return a response.
							 if (sender != null) {
								 boolean send = false;
								 if ((sender instanceof Player)  && 
										 ((Player)sender).isValid() && 
										 ((Player)sender).isOnline()) {
									 	send = true;
								 } else if (sender instanceof ConsoleCommandSender) {
									 send = true;
								 }
								 if (send) sender.sendMessage(interpolate("Mojang Server Status: [Logins: { logins }] [Sessions: { sessions }]"));
							 }
							 
							 
						}
					});
				}
			}
		}, 100, 
		   getConfig().getLong("check-interval"));
		
	}
	
	
	private String interpolate(String cmd) {
		String sessions = statuses.get("session") ? "up" : "down";
		String logins = statuses.get("login") ? "up" : "down";
		String now = new Date().toLocaleString();
		cmd = cmd.replaceAll("\\{\\s*sessions\\s*\\}", sessions);
		cmd = cmd.replaceAll("\\{\\s*logins\\s*\\}", logins);
		cmd = cmd.replaceAll("\\{\\s*now\\s*\\}", now);
		return cmd;
	}
	
	private void executeCommands(String key) {
		for (String cmd: getConfig().getStringList(key)) {
			getServer().dispatchCommand(getServer().getConsoleSender(), interpolate(cmd));
		}
	}

	public boolean allGood() {
		return statuses.get("login") && statuses.get("session");
	}
	
	public StatusResponse fetchStatus() {
		URLConnection conn;
		StatusResponse resp = null;
		try {
			conn = statusURL.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64; rv:15.0) Gecko/20120910144328 Firefox/15.0.2");
		    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line = reader.readLine().trim();
		    line = line.substring(1, line.length()-2);
		    resp = new StatusResponse((JSONObject)JSONValue.parse(line));
		    
		} catch (IOException e) {
			getLogger().warning("Connection to " + statusURL.toString() + " Failed.");
			
		}
	    return resp;
	}
	
}
