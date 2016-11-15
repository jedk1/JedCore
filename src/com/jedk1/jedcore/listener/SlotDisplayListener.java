package com.jedk1.jedcore.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.jedk1.jedcore.JedCore;
import com.jedk1.jedcore.listener.slotbar.SlotDisplayBar;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.util.ActionBar;

public class SlotDisplayListener implements Listener {

	private List<UUID> recentLogin = new ArrayList<>();

	@EventHandler(priority = EventPriority.NORMAL)
	public void onJoin(PlayerJoinEvent event) {
		recentLogin.add(event.getPlayer().getUniqueId());
		new BukkitRunnable() {
			@Override
			public void run() {
				recentLogin.remove(event.getPlayer().getUniqueId());
			}
		}.runTaskLater(JedCore.plugin, 20);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSlotChange(PlayerItemHeldEvent event) {
		if (SlotDisplayBar.isDisabled(event.getPlayer()) || recentLogin.contains(event.getPlayer().getUniqueId())) {
			return;
		}
		String leftAbil = ChatColor.WHITE + "" + ChatColor.ITALIC + "Empty";
		String currAbil = ChatColor.WHITE + "" + ChatColor.ITALIC + "Empty";
		String rightAbil = ChatColor.WHITE + "" + ChatColor.ITALIC + "Empty";

		BendingPlayer bplayer = BendingPlayer.getBendingPlayer(event.getPlayer());

		int left = (event.getNewSlot() == 0 ? 8 : event.getNewSlot() - 1) + 1;
		int center = event.getNewSlot() + 1;
		int right = (event.getNewSlot() == 8 ? 0 : event.getNewSlot() + 1) + 1;

		if (bplayer.getAbilities().get(center) != null) {
			CoreAbility ca = CoreAbility.getAbility(bplayer.getAbilities().get(center));
			if (ca != null) {
				StringBuilder sb = new StringBuilder((ca.getElement() != null ? ca.getElement().getColor() : ChatColor.DARK_PURPLE) + "");
				if (bplayer.isOnCooldown(ca.getName())) {
					sb.append(ChatColor.STRIKETHROUGH);
				}
				currAbil = sb.append(ca.getName()).toString();
			}
		}
		if (bplayer.getAbilities().get(left) != null) {
			CoreAbility ca = CoreAbility.getAbility(bplayer.getAbilities().get(left));
			if (ca != null) {
				StringBuilder sb = new StringBuilder((ca.getElement() != null ? ca.getElement().getColor() : ChatColor.DARK_PURPLE) + "");
				if (bplayer.isOnCooldown(ca.getName())) {
					sb.append(ChatColor.STRIKETHROUGH);
				}
				leftAbil = sb.append(ca.getName()).toString();
			}
		}
		if (bplayer.getAbilities().get(right) != null) {
			CoreAbility ca = CoreAbility.getAbility(bplayer.getAbilities().get(right));
			if (ca != null) {
				StringBuilder sb = new StringBuilder((ca.getElement() != null ? ca.getElement().getColor() : ChatColor.DARK_PURPLE) + "");
				if (bplayer.isOnCooldown(ca.getName())) {
					sb.append(ChatColor.STRIKETHROUGH);
				}
				rightAbil = sb.append(ca.getName()).toString();
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append(leftAbil)
		.append(ChatColor.WHITE).append(ChatColor.BOLD).append(" < ")
		.append(currAbil)
		.append(ChatColor.WHITE).append(ChatColor.BOLD).append(" > ")
		.append(rightAbil);

		ActionBar.sendActionBar(sb.toString(), event.getPlayer());
	}
}