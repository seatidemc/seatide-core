package top.seatide.seatidecore;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class Events implements Listener {
    @EventHandler
    public void banExplosion(EntityExplodeEvent e) {
        if (e.getEntityType() == EntityType.PRIMED_TNT || e.getEntityType() == EntityType.CREEPER) {
            e.blockList().clear();
        }
    }
}
