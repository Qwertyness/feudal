package com.qwertyness.feudal.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.qwertyness.feudal.Configuration;
import com.qwertyness.feudal.Feudal;
import com.qwertyness.feudal.Util;
import com.qwertyness.feudal.command.KingdomCommand;
import com.qwertyness.feudal.government.Fief;
import com.qwertyness.feudal.government.Kingdom;

public class ChunkListener implements Listener {
	private Feudal plugin;
	public static List<String> activeAutoclaim = new ArrayList<String>();
	
	public ChunkListener(Feudal plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (Util.toString(event.getFrom().getChunk()).equals(Util.toString(event.getTo().getChunk()))) {
			return;
		}
		
		updateLocation(event.getPlayer(), event);
		attemptClaim(event.getPlayer());
	}
	
	public void updateLocation(Player player, PlayerMoveEvent event) {
		Kingdom fromKingdom = this.plugin.kingdomManager.getLandOwner(event.getFrom().getChunk());
		String fromKingdomName = (fromKingdom == null) ? "" : fromKingdom.getName();
		Kingdom toKingdom = this.plugin.kingdomManager.getLandOwner(event.getTo().getChunk());
		String toKingdomName = (toKingdom == null) ? "" : toKingdom.getName();
		Fief fromFief = this.plugin.fiefManager.getLandOwner(fromKingdom, event.getFrom().getChunk());
		String fromFiefName = (fromFief == null) ? "" : fromFief.getName();
		Fief toFief = this.plugin.fiefManager.getLandOwner(toKingdom, event.getFrom().getChunk());
		String toFiefName = (toFief == null) ? "" : toFief.getName();
		
		String kingdomTitle = "";
		String fiefTitle = "";
		
		if (!fromKingdomName.equals(toKingdomName)) {
			kingdomTitle = toKingdomName;
		}
		if (!fromFiefName.equals(toFiefName)) {
			fiefTitle = toFiefName;
		}
		
		if (!kingdomTitle.equals("")) {
			if (!fiefTitle.equals("")) {
				event.getPlayer().sendMessage(Configuration.instance.messages.prefix + "You have entered " + fiefTitle + " in the kingdom of " + kingdomTitle);
			}
			else {
				event.getPlayer().sendMessage(Configuration.instance.messages.prefix + "You have entered the kingdom of " + kingdomTitle);
			}
		}
		else {
			if (!fiefTitle.equals("")) {
				event.getPlayer().sendMessage(Configuration.instance.messages.prefix + "You have entered " + fiefTitle);
			}
		}
	}
	
	public void attemptClaim(Player player) {
		if (!activeAutoclaim.contains(player.getUniqueId().toString())) {
			return;
		}
		KingdomCommand.instance.claim(player);
	}
}