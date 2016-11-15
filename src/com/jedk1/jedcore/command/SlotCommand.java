package com.jedk1.jedcore.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jedk1.jedcore.listener.slotbar.SlotDisplayBar;
import com.projectkorra.projectkorra.command.PKCommand;

public class SlotCommand extends PKCommand {
	
	public SlotCommand() {
		super("slotdisplay", "/bending slotdisplay", "Toggles the visibility of abilities above the hotbar.", new String[] { "slotdisplay", "sd" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!isPlayer(sender) || !correctLength(sender, args.size(), 0, 0) || !hasPermission(sender)) {
			return;
		}
		if (args.size() == 0) {
			SlotDisplayBar.toggle((Player) sender);
		} else {
			help(sender, false);
		}
	}
}