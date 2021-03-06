package com.qwertyness.feudal.government;

import java.util.List;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.configuration.ConfigurationSection;

import com.qwertyness.feudal.Feudal;
import com.qwertyness.feudal.government.settings.Settings;
import com.qwertyness.feudal.util.LandUtil;
import com.qwertyness.feudal.util.Util;

public class FiefManager {
	private Feudal plugin;
	
	public FiefManager() {
		this.plugin = Feudal.getInstance();
	}
	
	public boolean isFief(String kingdomName, String fiefName) {
		if (getFief(kingdomName, fiefName) != null) {
			return true;
		}
		return false;
	}
	
	public Fief getFief(String kingdomName, final String fiefName) {
		Kingdom kingdom = this.plugin.getKingdomManager().getKingdom(kingdomName);
		if (kingdom != null) {
			for (Fief fief : kingdom.getFiefs()) {
				if (fief.getName().equals(fiefName)) {
					return fief;
				}
			}
		}
		return null;
	}
	
	public Fief loadFief(String kingdomName, String fiefName) {
		ConfigurationSection fiefSection = this.plugin.getFiefData().get().getConfigurationSection(kingdomName + "." + fiefName);
		
		String name = fiefSection.getName();
		UUID baron = (fiefSection.getString("baron") != null) ? UUID.fromString(fiefSection.getString("baron")) : null;
		UUID baroness = (fiefSection.getString("baroness") != null) ? UUID.fromString(fiefSection.getString("baroness")) : null;
		List<UUID> peasents = Util.toUUIDList(fiefSection.getStringList("peasents"));
		List<UUID> serfs = Util.toUUIDList(fiefSection.getStringList("serfs"));
		Bank bank = this.plugin.getBankManager().loadBank(fiefSection);
		Army army = this.plugin.getArmyManager().loadArmy(fiefSection);
		Church church = this.plugin.getChurchManager().loadChurch(fiefSection);
		Chunk capital = Util.toChunk(fiefSection.getString("capital"));
		Settings settings = new Settings(fiefSection.getConfigurationSection("settings"));
		
		return new Fief(name, baron, baroness, peasents, serfs, bank, army, church, capital, settings, fiefSection);
	}
	
	public void saveFief(Fief fief) {
		ConfigurationSection fiefSection = fief.getDataPath();
		
		fiefSection.set("baron", (fief.getBaron() == null) ? null : fief.getBaron().toString());
		fiefSection.set("baroness", (fief.getBaroness() == null) ? null : fief.getBaroness().toString());
		fiefSection.set("peasents", Util.toStringList(fief.getPeasents()));
		fiefSection.set("serfs", Util.toStringList(fief.getSerfs()));
		fiefSection.set("capital", LandUtil.toString(fief.getCapital()));
		this.plugin.getBankManager().saveBank(fief.getBank());
		this.plugin.getArmyManager().saveArmy(fief.getArmy());
		this.plugin.getChurchManager().saveChurch(fief.getChurch());
		ConfigurationSection settingsSection = fiefSection.getConfigurationSection("settings");
		if (settingsSection == null) {
			settingsSection = fiefSection.createSection("settings");
		}
		fief.getSettings().saveSettings();
	}
	
	public void deleteFief(Kingdom kingdom, Fief fief) {
		this.plugin.getFiefData().get().set(fief.getDataPath().getCurrentPath(), null);
		kingdom.getFiefs().remove(fief);
	}
	
	public Fief getLandOwner(Chunk chunk) {
		for (Kingdom kingdom : this.plugin.getKingdomManager().kingdoms) {
			Fief fief = getLandOwner(kingdom, chunk);
			if (fief != null) {
				return fief;
			}
		}
		return null;
	}
	
	public Fief getLandOwner(Kingdom kingdom, Chunk chunk) {
		Land land = this.plugin.getLandManager().getLand(LandUtil.toString(chunk));
		if (land != null) {
			return land.fief;
		}
		return null;
	}
}
