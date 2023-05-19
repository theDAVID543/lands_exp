package group.david.the;

import me.angeschossen.lands.api.land.Land;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class ConfigReader {
    public static Integer getLandExp(Land land) {
        if(dataConfig.getString(String.valueOf(land)) == null){
            return 0;
        }else{
            return Integer.valueOf(dataConfig.getString(String.valueOf(land)));
        }
    }

    public static void setConfig(Land land, Integer exp) {
        dataConfig.set(String.valueOf(land),exp);
        requirement.instance.saveConfig();
        try {
            dataConfig.save(dataConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static File dataConfigFile;
    private static FileConfiguration dataConfig;

    public static void createCustomConfig() {
        dataConfigFile = new File(requirement.instance.getDataFolder(), "data/lands_exp.yml");
        if (!dataConfigFile.exists()) {
            dataConfigFile.getParentFile().mkdirs();
            requirement.instance.saveResource("data/lands_exp.yml", false);
        }

        dataConfig = new YamlConfiguration();
        try {
            dataConfig.load(dataConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        /* User Edit:
            Instead of the above Try/Catch, you can also use
            YamlConfiguration.loadConfiguration(customConfigFile)
        */
    }

}

