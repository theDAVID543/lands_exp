package group.david.the;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.levels.Level;
import me.angeschossen.lands.api.levels.requirement.CachedRequirement;
import me.angeschossen.lands.api.levels.requirement.Requirement;
import me.angeschossen.lands.api.memberholder.MemberHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class requirement extends JavaPlugin {
	public static LandsIntegration landsAPI;
	private Plugin myPlugin;
	public static JavaPlugin instance;

	@Override
	public void onEnable() {
		// Plugin startup logic
		Bukkit.getPluginManager().registerEvents(new eventListener(), this);
		instance = this;
		ConfigReader.createCustomConfig();
	}
	@Override
	public void onLoad() {
		saveDefaultConfig();
		this.myPlugin = this;
		this.landsAPI = getLandsAPI();
		Lands();
	}
	private LandsIntegration getLandsAPI(){
		LandsIntegration landsAPI;
		landsAPI = apiGetter(this);

		if(landsAPI == null){
			getServer().getPluginManager().disablePlugin(this);
		}

		Bukkit.getLogger().info("Lands API successfully initialized!");
		return landsAPI;
	}
	public LandsIntegration apiGetter(Plugin plugin){
		try {
			return LandsIntegration.of(this);
		}
		catch (NullPointerException nullPointerException){
			plugin.getLogger().severe("[Landlord] Failed to initialize the LandsAPI!");
			return null;
		}
	}
	public void Lands() {
		landsAPI.onLoad(() -> { // ensures that the levels API is ready

			/*
			 Land specific requirements
			 This example gets the level requirement configuration from the Lands levels.yml file.
			 You can use your own requirement configuration file, if you want. However, its recommended
			 to provide a central file, which allows to configure all requirements. Thats what the Lands levels.yml
			 file is for.
			 */
			@Nullable Map<Level, ConfigurationSection> landLevels = landsAPI.getLevelsHandler().getLandSection();
			if (landLevels != null) {
				for (Map.Entry<Level, ConfigurationSection> entry : landLevels.entrySet()) {
					ConfigurationSection requirement = entry.getValue().getConfigurationSection("requirements.expsPlayerGot.exps");
					if (requirement != null) {
						int value = Math.max(requirement.getInt("required", 0), 0);
						String reqName = Objects.requireNonNull(requirement.getString("title", "Requirement Name"));
						List<String> description = Objects.requireNonNull(requirement.getStringList("description"));
						for (int i = 0; i < description.size(); i++) {
							description.set(i, description.get(i));
						}
						entry.getKey().addRequirement(new CachedRequirement(myPlugin, "expsplayergot", reqName, description, value, String.valueOf(value)) {
							@Override
							public @NotNull String getProgressDisplay(@NotNull MemberHolder memberHolder) {
								return (int) getValue(memberHolder) + "/" + value;
							}

							@Override
							public float retrieveValue(@NotNull MemberHolder memberHolder) {
								final Land land = (Land) memberHolder;
								if(modified.get(land)!=null){
									return ConfigReader.getLandExp(land) - modified.get(land);
								}else{
									return ConfigReader.getLandExp(land);
								}
							}
						});
					}
				}
			}

			// nation specific requirements
			@Nullable Map<Level, ConfigurationSection> nationLevels = landsAPI.getLevelsHandler().getNationSection();
			if (nationLevels != null) {
				for (Map.Entry<Level, ConfigurationSection> entry : nationLevels.entrySet()) {
					ConfigurationSection requirement = entry.getValue().getConfigurationSection("requirements.expsPlayerGot.exps");
					if (requirement != null) {
						int value = Math.max(requirement.getInt("required", 0), 0);
						String reqName = Objects.requireNonNull(requirement.getString("title", "Requirement Name"));
						List<String> description = Objects.requireNonNull(requirement.getStringList("description"));
						for (int i = 0; i < description.size(); i++) {
							description.set(i, description.get(i));
						}
						entry.getKey().addRequirement(new CachedRequirement(myPlugin, "expsplayergot", reqName, description, value, String.valueOf(value)) {
							@Override
							public @NotNull String getProgressDisplay(@NotNull MemberHolder memberHolder) {
								return (int) getValue(memberHolder) + "/" + value;
							}

							@Override
							public float retrieveValue(@NotNull MemberHolder memberHolder) {
								final Land land = (Land) memberHolder;
								if(modified.get(land)!=null){
									return ConfigReader.getLandExp(land) - modified.get(land);
								}else{
									return ConfigReader.getLandExp(land);
								}
							}
						});
					}
				}
			}
		});
	}
	public static Map<Land,Integer> modified = new HashMap<>();
	public static void handle(Location location, int modify) {
		Area area = landsAPI.getArea(location);
		if (area == null) {
			return;
		}
		Land land = area.getLand();
		modified.put(land,modify);
		area.getLand().isRequirementCached("expsplayergot");
		if (land == null) {
			return;
		}
		ConfigReader.setConfig(land,ConfigReader.getLandExp(land) + modify);
		land.modifyRequirementCache("expsplayergot", modify, true);
	}
}
