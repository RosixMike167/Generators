package it.ziopagnotta.bwgenerator.events;

import it.ziopagnotta.bwgenerator.BWGenerator;
import it.ziopagnotta.bwgenerator.generator.Generator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinQuitEvent implements Listener {
    private final BWGenerator plugin;

    public JoinQuitEvent(BWGenerator plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for(Generator generator : plugin.getGeneratorFactory().getGenerators().values()) {
            if(!generator.isEnabled())
                continue;

            generator.toggleLinesFor(player);
        }
    }
}
