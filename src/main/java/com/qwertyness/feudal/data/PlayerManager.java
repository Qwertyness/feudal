package com.qwertyness.feudal.data;

import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import com.qwertyness.feudal.Feudal;

public class PlayerManager {
	List<FeudalPlayer> players;
	
	public PlayerManager() {
		for (String uuid : Feudal.getInstance().playerData.get().getKeys(false)) {
			this.regiseterPlayer(this.loadPlayer(uuid));
		}
	}
	
	public FeudalPlayer getPlayer(UUID player) {
		for (FeudalPlayer feudalPlayer : this.players) {
			if (feudalPlayer.player.toString().equals(player.toString())) {
				return feudalPlayer;
			}
		}
		return null;
	}
	
	public boolean isPlayer(UUID player) {
		return this.getPlayer(player) == null;
	}
	
	public void regiseterPlayer(FeudalPlayer player) {
		if (!isPlayer(player.player)) {
			this.players.add(player);
		}
	}
	
	public void unregisterPlayer(FeudalPlayer player) {
		if (!isPlayer(player.player)) {
			return;
		}
		this.players.remove(player);
	}
	
	public FeudalPlayer loadPlayer(String uuid) {
		ConfigurationSection playerSection = Feudal.getInstance().playerData.get().getConfigurationSection(uuid);
		
		UUID playerUUID = UUID.fromString(uuid);
		boolean male = playerSection.getBoolean("gender");
		
		return new FeudalPlayer(playerUUID, male, playerSection);
	}
	
	public void savePlayer(FeudalPlayer player) {
		ConfigurationSection section = player.getDataPath();
		
		section.set("gender", player.male);
	}
	
	public void saveAll() {
		for (FeudalPlayer player : this.players) {
			this.savePlayer(player);
		}
	}
}