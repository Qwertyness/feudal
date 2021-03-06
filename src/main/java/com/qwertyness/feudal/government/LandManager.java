package com.qwertyness.feudal.government;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.configuration.ConfigurationSection;

import com.qwertyness.feudal.Configuration;
import com.qwertyness.feudal.Feudal;
import com.qwertyness.feudal.resource.DynamicCache;
import com.qwertyness.feudal.util.Util;

public class LandManager {
	private Feudal plugin;
	private DynamicCache<String> cache;

	public List<Land> land = new ArrayList<Land>();
	
	public LandManager() {
		this.plugin = Feudal.getInstance();
		
		cache = new DynamicCache<String>(Configuration.instance.landCacheInterval) {
			public void decache(String coordinates) {
				Land decacheLand = null;
				for (Land testLand : land) {
					if (testLand.getCoordinates().equals(coordinates))
						decacheLand = testLand;
				}
				if (decacheLand != null)
					unregisterLand(decacheLand);
			}
		};
	}
	
	public void registerLand(Land land) {
		if (!this.land.contains(land)) {
			this.land.add(land);
		}
	}
	
	public void unregisterLand(Land land) {
		for (Land testLand : new ArrayList<Land>(this.land)) {
			if (land.getCoordinates().equals(testLand.getCoordinates())) {
				this.land.remove(testLand);
			}
		}
	}
	
	public Land getLand(String coordinates) {
		cache.cached(coordinates);
		for (Land land : this.land) {
			if (land.getCoordinates().equals(coordinates))
				return land;
		}
		
		Land land = createLand(coordinates);
		return land;
	}
	
	public Land createLand(String coordinates) {
		if (this.plugin.getLandData().get().isConfigurationSection(coordinates))
			return loadLand(coordinates);
		
		ConfigurationSection landSection = this.plugin.getLandData().get().createSection(coordinates);
		Land land = new Land(Util.toChunk(coordinates), null, null, new ArrayList<UUID>(), landSection);
		if (land.getChunk() == null)
			return null;
		registerLand(land);
		return land;
	}
	
	public Land loadLand(String coordinates) {
		ConfigurationSection section = this.plugin.getLandData().get().getConfigurationSection(coordinates);
		
		Chunk chunk = Util.toChunk(coordinates);
		Kingdom kingdom = this.plugin.getKingdomManager().getKingdom(section.getString("kingdom"));
		Fief fief = this.plugin.getFiefManager().getFief((kingdom == null) ? "" : kingdom.getName(), section.getString("fief"));
		List<UUID> owners = Util.toUUIDList(section.getStringList("owners"));
		
		Land land = new Land(chunk, kingdom, fief, owners, section);
		land.setFortress(section.getBoolean("fortress"));
		
		if (kingdom != null)
			kingdom.addLand(land);
		if (fief != null)
			fief.addLand(land);
		registerLand(land);
		return land;
	}
	
	public void saveLand(Land land) {
		ConfigurationSection section = land.getDataPath();
		
		section.set("kingdom", (land.kingdom == null) ? null : land.kingdom.getName());
		section.set("fief", (land.fief == null) ? null : land.fief.getName());
		section.set("owners", Util.toStringList(land.owners));
		section.set("fortress", land.isFortress());
	}
	
	public void saveAll() {
		for (Land land : this.land)
			this.saveLand(land);
	}
}
