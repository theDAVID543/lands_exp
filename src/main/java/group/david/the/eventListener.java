package group.david.the;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import me.angeschossen.lands.api.land.Land;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;

public class eventListener implements Listener {
    public Map<Land, Integer> landExps;
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickExpOrb(PlayerPickupExperienceEvent e){
        int amount = e.getExperienceOrb().getExperience();
        requirement.handle(e.getPlayer().getLocation(),amount);
    }
}
