package fr.perrier.guard.manager.listener.commands;

import fr.perrier.guard.manager.commands.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;

public class CpsListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Action a = e.getAction();
        Player p = e.getPlayer();

        if (a.equals(Action.LEFT_CLICK_AIR) || a.equals(Action.LEFT_CLICK_BLOCK)) {
            if (!Cps.testedG.containsKey(p.getUniqueId())) return;
            Cps.testedG.replace(p.getUniqueId(), Cps.testedG.get(p.getUniqueId()) + 1.0);
        }
        if (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (!Cps.testedD.containsKey(e.getPlayer().getUniqueId())) return;
            Cps.testedD.replace(p.getUniqueId(), Cps.testedD.get(p.getUniqueId()) + 1.0);
        }
    }
}
