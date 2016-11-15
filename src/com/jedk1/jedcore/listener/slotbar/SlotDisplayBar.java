package com.jedk1.jedcore.listener.slotbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.jedk1.jedcore.configuration.Config;
import com.jedk1.jedcore.configuration.JedCoreConfig;

public class SlotDisplayBar {

	public static List<UUID> disabled = new ArrayList<UUID>();
	public static String empty;
	public static String toggleOn;
	public static String toggleOff;
	public static Config toggled;
	public static boolean disabledworlds;
	
	static {
		toggled = new Config(new File("/slotbar/players.yml"));

		List<String> uuids = toggled.getConfig().getStringList("Players");
		if (uuids != null && !uuids.isEmpty()) {
			if (!uuids.get(0).matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")) {
				toggled.getConfig().set("Players", new ArrayList<String>());
				toggled.saveConfig();
			} else {
				for (String s : uuids) {
					disabled.add(UUID.fromString(s));
				}
			}
		}
	}
	
	public static void setFields() {
		empty = ChatColor.translateAlternateColorCodes('&', JedCoreConfig.slotbar.getConfig().getString("Settings.EmptySlot"));
		toggleOn = ChatColor.translateAlternateColorCodes('&', JedCoreConfig.slotbar.getConfig().getString("Settings.Toggle.On"));
		toggleOff = ChatColor.translateAlternateColorCodes('&', JedCoreConfig.slotbar.getConfig().getString("Settings.Toggle.Off"));
		disabledworlds = JedCoreConfig.slotbar.getConfig().getBoolean("Settings.Display.DisabledWorlds");
	}
	
	public static void toggle(Player player) {
		List<String> uuids = new ArrayList<String>();
		uuids.addAll(toggled.getConfig().getStringList("Players"));
		if (uuids.contains(player.getUniqueId().toString())) {
			uuids.remove(player.getUniqueId().toString());
			disabled.remove(player.getUniqueId());
			player.sendMessage(toggleOn);
		} else {
			uuids.add(player.getUniqueId().toString());
			disabled.add(player.getUniqueId());
			player.sendMessage(toggleOff);
		}
		toggled.getConfig().set("Players", uuids);
		toggled.saveConfig();
	}
	
	public static boolean isDisabled(Player player) {
		return disabled.contains(player.getUniqueId());
	}
}
